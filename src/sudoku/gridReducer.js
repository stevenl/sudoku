import assert from 'assert';
import GridState from './GridState';
import SegmentState from './SegmentState';
import CellState, {AVAILABLE_VALUES} from './CellState';
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
    // Clone the cells array before modifying it
    const newCells = [...cells];

    // Update the cell according to the action
    const oldCell = newCells[action.index];
    const newCell = cellReducer(oldCell, action);
    newCells[action.index] = newCell;

    // Recalculate availableValues for related cells
    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = newCell.segment(segmentType);
        const segment = SegmentState.newFrom(cells, segmentIndex, segmentType);

        for (const segmentCell of segment.cells) {
            if (isNaN(segmentCell.value) && segmentCell.index !== action.index) {
                newCells[segmentCell.index] = cellReducer(segmentCell, action);
            }
        }
    }

    return newCells;
}

function cellReducer(cell, action) {
    assert(!cell.readOnly, `Should not reduce a readOnly cell '${cell}'`);

    if (cell.index === action.index) {
        if (isNaN(action.value)) {
            return clearCell(cell, action);
        } else {
            return setCell(cell, action);
        }
    } else {
        assert(isNaN(cell.value), `Should not refresh cell with value '${cell.value}'`);
        return refreshCell(cell, action);
    }
}

function setCell(cell, action) {
    return new CellState(
        action.index, action.value, action.readOnly,
        action.readOnly ? undefined : cell.errors, //todo
        undefined,
    );
}

function clearCell(cell, action) {
    return new CellState(
        action.index, action.value, action.readOnly, undefined,
        recalculateAvailableValues(cell, action),
    );
}

function refreshCell(cell, action) {
    return new CellState(
        cell.index, cell.value, cell.readOnly, cell.errors,
        recalculateAvailableValues(cell, action),
    );
}

function recalculateAvailableValues(cell, action) {
    const availableValues = new Set(AVAILABLE_VALUES)
    availableValues.delete(action.value);

    for (const segmentType of SEGMENT_TYPES) {
        const segmentIndex = cell.segment(segmentType);
        const segment = SegmentState.newFrom(action.gridCells, segmentIndex, segmentType);

        for (const segmentCell of segment.cells) {
            // The old value of the cell should not be removed from availableValues
            if (!isNaN(segmentCell.value) && segmentCell.index !== action.index) {
                availableValues.delete(segmentCell.value);
            }
        }
    }
    return availableValues;
}

// function setCellError(cell, segmentType) {
//     if (cell.errors[segmentType]) {
//         throw new Error(`Error has already been set for segment type ${segmentType}`);
//     }
//     cell.errors[segmentType] = 1;
//     cell.errors.total += 1;
// }
//
// function clearCellError(cell, segmentType) {
//     if (!cell.errors[segmentType]) {
//         throw new Error(`Error is not set for segment type ${segmentType}`);
//     }
//     cell.errors[segmentType] = 0;
//     cell.errors.total -= 1;
// }
