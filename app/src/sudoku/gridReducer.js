import { CellState, GridState, SEGMENT_TYPES } from './grid';

export function SetValueAction(index, value, readOnly) {
    this.index = index;
    this.value = value;
    this.readOnly = readOnly || false;
}
function IncrementErrorAction(segmentType) {
    this.segmentType = segmentType;
}
function DecrementErrorAction(segmentType) {
    this.segmentType = segmentType;
}
function RemoveAvailableValuesAction(values) {
    this.values = values;
}

export function gridReducer(grid, action) {
    if (action.constructor !== SetValueAction) {
        throw new Error(`Invalid action ${action}`);
    }

    // Don't change anything if the action is invalid
    if (action.value < 1 || action.value > 9) {
        return grid;
    }

    if (!isNaN(action.value)) {
        return setCellValue(grid, action);
    } else {
        return clearCellValue(grid, action);
    }
}

function setCellValue(grid, action) {
    // Clone the cells array before modifying it
    const cells = [...grid.cells];
    const newGrid = new GridState(cells);

    // Update the cell according to the action
    const oldCell = cells[action.index];
    const newCell = cells[action.index] = cellReducer(oldCell, action);

    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = newCell[segmentType];
        const segment = newGrid.segment(segmentType, segmentIndex);

        // Update the availableValues of related cells by removing this used value
        for (const cell of segment.cells) {
            if (isNaN(cell.value) && cell.availableValues.has(action.value)) {
                cells[cell.index] = cellReducer(cell, new RemoveAvailableValuesAction([action.value]));
            }
        }

        // Mark any errors if this new value has caused any
        const valueCells = segment.cells
            .filter((cell) => cell.value === action.value);
        if (valueCells.length > 1) {
            for (const cell of valueCells) {
                if (!cell.readOnly && !cell.errors[segmentType]) {
                    cells[cell.index] = cellReducer(cell, new IncrementErrorAction(segmentType));
                }
            }
        }
    }
    return newGrid;
}

function clearCellValue(grid, action) {
    // Clone the cells array before modifying it
    const cells = [...grid.cells];
    const newGrid = new GridState(cells);

    // Update the cell according to the action
    const oldCell = cells[action.index];
    const newCell = cells[action.index] = cellReducer(oldCell, action);

    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = oldCell[segmentType];
        const segment = newGrid.segment(segmentType, segmentIndex);

        // Re-calculate the availableValues for the cell that has been cleared
        const usedValues = segment.values;
        cells[action.index] = cellReducer(newCell, new RemoveAvailableValuesAction(usedValues));

        // Clear existing errors that have been resolved by clearing the cell
        const valueCells = segment.cells
            .filter((cell) => cell.value === oldCell.value);
        if (valueCells.length === 1) { // More than 1 means it is still an error
            for (const cell of valueCells) {
                if (!cell.readOnly && cell.errors[segmentType]) {
                    cells[cell.index] = cellReducer(cell, new DecrementErrorAction(segmentType));
                }
            }
        }
    }
    return new GridState(cells);
}

function cellReducer(cell, action) {
    // console.log(cell, action);
    if (cell.readOnly) {
        throw new Error(`Attempted to modify readOnly cell ${cell.index}`);
    }

    let readOnly;
    let errors;
    switch (action.constructor) {
        case SetValueAction:
            readOnly = action.readOnly; // true during init()
            errors = !readOnly ? cell.errors : undefined;
            return new CellState(action.index, action.value, readOnly, errors);
            // We will update the error value separately
        case RemoveAvailableValuesAction:
            const availableValues = new Set(cell.availableValues);
            for (const value of action.values) {
                availableValues.delete(value);
            }
            return new CellState(cell.index, cell.value, false, cell.errors, availableValues);
        case IncrementErrorAction:
            errors = {
                ...cell.errors,
                [action.segmentType]: 1,
                total: cell.errors.total + 1,
            };
            return new CellState(cell.index, cell.value, false, errors, cell.availableValues);
        case DecrementErrorAction:
            errors = {
                ...cell.errors,
                [action.segmentType]: 0,
                total: cell.errors.total - 1,
            };
            return new CellState(cell.index, cell.value, false, errors, cell.availableValues);
        default:
            throw new Error(`Unknown action type ${action.type}`);
    }
}
