import { CellState, GridState } from './grid';

export function SetValueAction(index, value) {
    this.index = index;
    this.value = value;
}
function SetErrorAction(segmentType) {
    this.segmentType = segmentType;
}
function ClearErrorAction(segmentType) {
    this.segmentType = segmentType;
}

export function gridReducer(grid, action) {
    if (action.constructor !== SetValueAction) {
        throw new Error(`Invalid action ${action}`);
    }
    // Don't change anything if the action is invalid
    const cell = grid.cells[action.index];
    if (cell.readOnly || action.value < 1 || action.value > 9) {
        return grid;
    }

    // Clone the cells array before modifying it
    const cells = [...grid.cells];
    const newGrid = new GridState(cells);
    // Update the cell according to the action
    cells[action.index] = cellReducer(cell, action);

    // Error checking
    for (const segmentType of ['row', 'column', 'region']) {
        const cellsByValue = newGrid.getCellsGroupedByValue({type: segmentType, index: cell[segmentType]});
        if (!isNaN(action.value)) {
            // Mark errors
            const valueCells = cellsByValue[action.value];
            if (valueCells !== undefined && valueCells.length > 1) {
                for (let c of valueCells) {
                    if (!c.readOnly && !c.errors[segmentType]) {
                        c = cells[c.index]; // Get the latest cell error state
                        cells[c.index] = cellReducer(c, new SetErrorAction(segmentType));
                    }
                }
            }
        } else {
            // Clear errors
            const valueCells = cellsByValue[cell.value];
            if (valueCells !== undefined && valueCells.length === 1) {
                for (let c of valueCells) {
                    if (!c.readOnly && c.errors[segmentType]) {
                        c = cells[c.index]; // Get the latest cell error state
                        cells[c.index] = cellReducer(c, new ClearErrorAction(segmentType));
                    }
                }
            }
        }
    }

    return newGrid;
}

function cellReducer(cell, action) {
    // console.log(cell, action);
    if (cell.readOnly) {
        throw new Error(`Attempted to modify readOnly cell ${cell.index}`);
    }

    let errors;
    switch (action.constructor) {
        case SetValueAction:
            return new CellState(action.index, action.value, false, cell.errors);
        // We will do error checking and update the error value later
        case SetErrorAction:
            errors = {...cell.errors, [action.segmentType]: 1, total: cell.errors.total + 1};
            return new CellState(cell.index, cell.value, false, errors);
        case ClearErrorAction:
            errors = {...cell.errors, [action.segmentType]: 0, total: cell.errors.total - 1};
            return new CellState(cell.index, cell.value, false, errors);
        default:
            throw new Error(`Unknown action type ${action.type}`);
    }
}
