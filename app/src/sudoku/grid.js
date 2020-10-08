export const GRID_SIZE = 9;
export const GRID_INDEXES = [0, 1, 2, 3, 4, 5, 6, 7, 8];

export class GridState {
    constructor(cells) {
        if (!cells) {
            this.cells = this._emptyGrid();
        } else if (typeof cells === 'string') {
            this.cells = this._parseGrid(cells);
        } else {
            this.cells = cells;
        }

        if (this.cells.length !== GRID_SIZE ** 2) {
            throw new Error(`Grid must contain 81 cells: got ${cells.length}`);
        }
    }

    _emptyGrid() {
        let i = 0;
        return GRID_INDEXES.flatMap(() =>
            GRID_INDEXES.map(() =>
                new CellState(i++, NaN, false)
            )
        );
    }

    _parseGrid(gridString) {
        const values = (gridString).split('');
        return values.map((val, idx) => {
            if (!val || val < 1) {
                val = NaN;
            } else {
                val = parseInt(val);
            }
            return new CellState(idx, val, !isNaN(val));
        });
    }
}

function CellState(index, value, readOnly) {
    return {
        index: index,
        value: value,
        ...(readOnly ? {readOnly: true} : {}),
    };
}

export function gridReducer(grid, action) {
    // Don't change anything if the action is invalid
    const cell = grid.cells[action.index];
    if (cell.readOnly || action.value < 1) {
        return grid;
    }

    // Clone the grid
    const newGrid = new GridState(grid.cells);
    // Update the cell according to the action
    newGrid.cells[action.index] = cellReducer(cell, action);

    return newGrid;
}

function cellReducer(cell, action) {
    if (cell.readOnly) {
        throw new Error(`Attempted to modify readOnly cell ${cell.index}`);
    }

    if (action.type === 'setValue') {
        return new CellState(cell.index, action.value);
    } else {
        throw new Error(`Unknown action type ${action.type}`);
    }
}
