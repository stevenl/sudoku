import assert from 'assert';
import CellState from './CellState';
import {gridReducer, SetValueAction} from './gridReducer';
import {GRID_INDEXES, GRID_SIZE} from "./Grid";

export default class GridState {
    constructor(cells) {
        if (!cells) {
            this.cells = this._emptyGrid();
        } else {
            this.cells = cells;
        }
        assert(this.cells.length === GRID_SIZE ** 2,
            `Grid has incorrect number of cells '${this.cells.length}'`);

        Object.freeze(this.cells);
        Object.freeze(this);
    }

    static newFrom(gridString) {
        let grid = new GridState();
        const cells = grid._parseGridString(gridString);
        // Add each cell incrementally so the availableValues can be kept up-to-date
        for (const cell of cells) {
            if (!isNaN(cell.value)) {
                grid = gridReducer(grid, new SetValueAction(cell.index, cell.value, cells, true));
            }
        }
        return grid;
    }

    _emptyGrid() {
        let i = 0;
        return GRID_INDEXES.flatMap(() =>
            GRID_INDEXES.map(() =>
                new CellState(i++, NaN)
            )
        );
    }

    _parseGridString(gridString) {
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
