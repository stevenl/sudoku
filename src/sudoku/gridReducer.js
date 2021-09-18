import assert from 'assert';
import GridState from './GridState';
import SegmentState from './SegmentState';
import CellState from './CellState';
import {SEGMENT_TYPES} from "./Grid";

export function SetValueAction(index, value, gridCells, readOnly = false) {
    this.index = index;
    this.value = value;
    this.gridCells = gridCells;

    // Used for initialising the grid
    this.readOnly = readOnly;
}

export function gridReducer(grid, action) {
    assert(action.constructor === SetValueAction,
        `Invalid action '${action.constructor}'`);

    // Don't change anything if the action is invalid
    if (action.value < 1 || action.value > 9) {
        return grid;
    }

    return new GridState(
        cellsReducer(grid.cells, action)
    );
}

function cellsReducer(cells, action) {
    if (!isNaN(action.value)) {
        return setCellValue(cells, action);
    } else {
        return clearCellValue(cells, action);
    }
}

function setCellValue(cells, action) {
    // Clone the cells array before modifying it
    const newCells = [...cells];

    // Update the cell according to the action
    const oldCell = newCells[action.index];
    const newCell = cellReducer(oldCell, action);
    newCells[action.index] = newCell;

    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = newCell.segment(segmentType);
        const segment = SegmentState.newFrom(cells, segmentIndex, segmentType);

        // Update the availableValues of related cells by removing this used value
        for (const cell of segment.cells) {
            if (isNaN(cell.value) && cell.availableValues.has(action.value)) {
                removeCellAvailableValues(cell, [action.value]);
            }
        }
        eliminateAvailableValues(segment);

        // Mark any errors if this new value has caused any
        const valueCells = segment.cells
            .filter(cell => cell.value === action.value);
        if (valueCells.length > 1) {
            for (const cell of valueCells) {
                if (!cell.readOnly && !cell.errors[segmentType]) {
                    setCellError(cell, segmentType);
                }
            }
        }
    }
    return newCells;
}

function clearCellValue(cells, action) {
    // Clone the cells array before modifying it
    const newCells = [...cells];

    // Update the cell according to the action
    const oldCell = newCells[action.index];
    const newCell = cellReducer(oldCell, action);
    newCells[action.index] = newCell;

    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = newCell[segmentType];
        const segment = SegmentState.newFrom(cells, segmentIndex, segmentType);

        // Re-calculate the availableValues for the cell that has been cleared
        removeCellAvailableValues(newCell, segment.values);
        // Add old value back to availableValues of related cells
        relatedCell:
            for (const cell of segment.cells) {
                if (cell.readOnly) {
                    continue;
                }
                for (const segmentType1 of SEGMENT_TYPES) {
                    const segment1 = SegmentState.newFrom(newCells, cell[segmentType1], segmentType1);
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
    return newCells;
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
            // We will update the error value separately
            return new CellState(
                action.index,
                reduceCellValue(cell.value, action),
                action.readOnly, // true during initialisation
                !action.readOnly && !isNaN(action.value) ? cell.errors : undefined,
                //availableValues
            );
        default:
            throw new Error(`Unknown action type ${action.type}`);
    }
}

function reduceCellValue(value, action) {
    if (action.constructor === SetValueAction) {
        return action.value;
    } else {
        return value;
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
