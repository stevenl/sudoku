import { CellState, GridState, SEGMENT_TYPES } from './grid';

export function SetValueAction(index, value, readOnly) {
    this.index = index;
    this.value = value;
    this.readOnly = readOnly || false;
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
                cell.removeAvailableValues([action.value]);
            }
        }

        // Mark any errors if this new value has caused any
        const valueCells = segment.cells
            .filter((cell) => cell.value === action.value);
        if (valueCells.length > 1) {
            for (const cell of valueCells) {
                if (!cell.readOnly && !cell.errors[segmentType]) {
                    cell.setError(segmentType);
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
        newCell.removeAvailableValues(usedValues);

        // Clear errors in related cells that have been resolved by clearing this cell
        const valueCells = segment.cells
            .filter((cell) => cell.value === oldCell.value);
        if (valueCells.length === 1) { // More than 1 means it is still an error
            for (const cell of valueCells) {
                if (!cell.readOnly && cell.errors[segmentType]) {
                    cell.clearError(segmentType);
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

    if (action.constructor === SetValueAction) {
        const readOnly = action.readOnly; // true during init()
        const errors = !readOnly && !isNaN(action.value) ? cell.errors : undefined;
        return new CellState(action.index, action.value, readOnly, errors);
        // We will update the error value separately
    } else {
        throw new Error(`Unknown action type ${action.type}`);
    }
}
