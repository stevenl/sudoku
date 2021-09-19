import assert from 'assert';
import CellState from './CellState';
import {gridReducer, SetValueAction} from './gridReducer';
import {GRID_SIZE} from "./Grid";

export default class GridState {
    constructor(cells) {
        this.cells = cells || emptyGrid();
        assert(this.cells.length === GRID_SIZE ** 2,
            `Grid has incorrect number of cells '${this.cells.length}'`);

        Object.freeze(this.cells);
        Object.freeze(this);
    }

    static newFrom(gridString) {
        const cells = parseGridString(gridString);

        // Add each cell incrementally so the availableValues can be kept up-to-date
        let grid = new GridState();
        for (const cell of cells) {
            if (cell.value) {
                grid = gridReducer(grid, new SetValueAction(cell.index, cell.value, cells, true));
            }
        }
        return grid;
    }

    cell(index) {
        return this.cells[index];
    }
}

function emptyGrid() {
    return [...Array(GRID_SIZE ** 2).keys()]
        .map(i => new CellState(i, NaN));
}

function parseGridString(gridString) {
    const values = gridString.split('');
    return values.map((val, idx) => {
        val = val ? parseInt(val) : NaN;
        return new CellState(idx, val, true);
    });
}
