import GridState from './GridState';
import CellState from './CellState';
import {SEGMENT_TYPES} from "./Grid";

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
        const segmentIndex = newCell.segment(segmentType);
        const segment = newGrid.segment(segmentType, segmentIndex);

        // Update the availableValues of related cells by removing this used value
        for (const cell of segment.cells) {
            if (isNaN(cell.value) && cell.availableValues.has(action.value)) {
                removeCellAvailableValues(cell, [action.value]);
            }
        }
        eliminateAvailableValues(segment);

        // Mark any errors if this new value has caused any
        const valueCells = segment.cells
            .filter((cell) => cell.value === action.value);
        if (valueCells.length > 1) {
            for (const cell of valueCells) {
                if (!cell.readOnly && !cell.errors[segmentType]) {
                    setCellError(cell, segmentType);
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
        const segmentIndex = newCell[segmentType];
        const segment = newGrid.segment(segmentType, segmentIndex);

        // Re-calculate the availableValues for the cell that has been cleared
        const usedValues = segment.values;
        removeCellAvailableValues(newCell, usedValues);
        // Add old value back to availableValues of related cells
        relatedCell:
            for (const cell of segment.cells) {
                if (cell.readOnly) {
                    continue;
                }
                for (const segmentType1 of SEGMENT_TYPES) {
                    const segment1 = newGrid.segment(segmentType1, cell[segmentType1]);
                    if (!segment1.isValueAvailable(oldCell.value)) {
                        continue relatedCell;
                    }
                }
                addCellAvailableValue(cell, oldCell.value);
            }
        eliminateAvailableValues(segment);

        // Clear errors in related cells that have been resolved by clearing this cell
        const valueCells = segment.cells
            .filter((cell) => cell.value === oldCell.value);
        if (valueCells.length === 1) { // More than 1 means it is still an error
            for (const cell of valueCells) {
                if (!cell.readOnly && cell.errors[segmentType]) {
                    clearCellError(cell, segmentType);
                }
            }
        }
    }
    return new GridState(cells);
}

function eliminateAvailableValues(segment) {
    // Detect values that are available in one cell only
    for (const [value, cells] of segment.cellsByAvailableValue) {
        if (cells.length === 1) {
            setCellAvailableValue(cells[0], value);
        }
    }
}

function cellReducer(cell, action) {
    // console.log(cell, action);
    if (cell.readOnly) {
        throw new Error(`Attempted to modify readOnly cell ${cell.index}`);
    }

    switch (action.constructor) {
        case SetValueAction:
            const readOnly = action.readOnly; // true during init()
            const errors = !readOnly && !isNaN(action.value) ? cell.errors : undefined;
            // We will update the error value separately
            return new CellState(action.index, action.value, readOnly, errors);
        default:
            throw new Error(`Unknown action type ${action.type}`);
    }
}

function setCellError(cell, segmentType) {
    if (cell.errors[segmentType]) {
        throw new Error(`Error has already been set for segment type ${segmentType}`);
    }
    cell.errors[segmentType] = 1;
    cell.errors.total += 1;
}

function clearCellError(cell, segmentType) {
    if (!cell.errors[segmentType]) {
        throw new Error(`Error is not set for segment type ${segmentType}`);
    }
    cell.errors[segmentType] = 0;
    cell.errors.total -= 1;
}

function setCellAvailableValue(cell, value) {
    cell.availableValues.clear();
    cell.availableValues.add(value);
}

function addCellAvailableValue(cell, value) {
    cell.availableValues.add(value);
}

function removeCellAvailableValues(cell, values) {
    for (const value of values) {
        cell.availableValues.delete(value);
    }
}
