import { CellState, GridState } from './grid';

export function SetValueAction(index, value, readOnly) {
    this.index = index;
    this.value = value;
    this.readOnly = readOnly || false;
}
function SetErrorAction(segmentType) {
    this.segmentType = segmentType;
}
function ClearErrorAction(segmentType) {
    this.segmentType = segmentType;
}
function RemovePossibleValueAction(value) {
    this.value = value;
}

export function gridReducer(grid, action) {
    if (action.constructor !== SetValueAction) {
        throw new Error(`Invalid action ${action}`);
    }

    // Don't change anything if the action is invalid
    if (action.value < 1 || action.value > 9) {
        return grid;
    }

    // Update possibleValues to provide hints
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
    const oldCell = cells[action.index] =
        cellReducer(cells[action.index], action);

    for (const segmentType of ['row', 'column', 'region']) {
        const segmentCells = newGrid.segmentCells({type: segmentType, index: oldCell[segmentType]});
        const cellsByValue = getCellsGroupedByValue(segmentCells);

        // Update the possibleValues of related cells by removing this used value
        for (const cell of segmentCells) {
            if (isNaN(cell.value) && cell.possibleValues.has(action.value)) {
                cells[cell.index] = cellReducer(cell, new RemovePossibleValueAction(action.value));
            }
        }

        // Mark any errors if this new value has caused any
        const valueCells = cellsByValue[action.value];
        if (valueCells !== undefined && valueCells.length > 1) {
            for (let cell of valueCells) {
                if (!cell.readOnly && !cell.errors[segmentType]) {
                    cell = cells[cell.index]; // Get the latest cell error state
                    cells[cell.index] = cellReducer(cell, new SetErrorAction(segmentType));
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
    const oldCell = grid.cells[action.index];
    const newCell = cells[action.index] = cellReducer(oldCell, action);

    for (const segmentType of ['row', 'column', 'region']) {
        const segmentCells = newGrid.segmentCells({type: segmentType, index: oldCell[segmentType]});
        const cellsByValue = getCellsGroupedByValue(segmentCells);

        // Ccalculate the possibleValues for the cell that has been cleared
        for (let usedValue in cellsByValue) {
            usedValue = Number(usedValue);
            cells[action.index] = cellReducer(newCell, new RemovePossibleValueAction(usedValue));
        }

        // Clear existing errors that have been resolved by clearing the cell
        const valueCells = cellsByValue[oldCell.value];
        if (valueCells !== undefined && valueCells.length === 1) {
            for (let cell of valueCells) {
                if (!cell.readOnly && cell.errors[segmentType]) {
                    cell = cells[cell.index]; // Get the latest cell error state
                    cells[cell.index] = cellReducer(cell, new ClearErrorAction(segmentType));
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
            return new CellState(action.index, action.value, readOnly, !readOnly ? cell.errors : undefined);
            // We will update the error value separately
        case RemovePossibleValueAction:
            const possibleValues = new Set(cell.possibleValues);
            possibleValues.delete(action.value);
            return new CellState(cell.index, cell.value, false, cell.errors, possibleValues);
        case SetErrorAction:
            errors = {...cell.errors, [action.segmentType]: 1, total: cell.errors.total + 1};
            return new CellState(cell.index, cell.value, false, errors, cell.possibleValues);
        case ClearErrorAction:
            errors = {...cell.errors, [action.segmentType]: 0, total: cell.errors.total - 1};
            return new CellState(cell.index, cell.value, false, errors, cell.possibleValues);
        default:
            throw new Error(`Unknown action type ${action.type}`);
    }
}

function getCellsGroupedByValue(cells) {
    return cells.reduce((acc, cell) => {
        const value = cell.value;
        const groupedCells = acc[value] || [];
        return {
            ...acc,
            ...(!isNaN(value) ? {[value]: [...groupedCells, cell]} : {}),
        };
    }, {});
}
