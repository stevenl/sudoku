import CellState from './CellState';
import {gridReducer, SetValueAction} from './gridReducer';

export const GRID_SIZE = 9;
export const GRID_INDEXES = [0, 1, 2, 3, 4, 5, 6, 7, 8];
export const SEGMENT_TYPES = ['row', 'column', 'region'];
const REGION_SIZE = 3;
const REGION_INDEXES = [0, 1, 2];
const AVAILABLE_VALUES = [1, 2, 3, 4, 5, 6, 7, 8, 9];

export default class GridState {
    constructor(cells) {
        if (!cells) {
            this.cells = this._emptyGrid();
        } else {
            this.cells = cells;
        }

        if (this.cells.length !== GRID_SIZE ** 2) {
            throw new Error(`Grid must contain 81 cells: got ${cells.length}`);
        }

        // Object.freeze(this.cells);
        Object.freeze(this);
    }

    static newFrom(gridString) {
        let grid = new GridState();
        const cells = grid._parseGrid(gridString);
        // Add each cell incrementally so the availableValues can be kept up-to-date
        for (const cell of cells) {
            if (!isNaN(cell.value)) {
                grid = gridReducer(grid, new SetValueAction(cell.index, cell.value, true));
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

    row(rowIndex) {
        const startIdx = rowIndex * GRID_SIZE;
        const cells = GRID_INDEXES.map((offset) => this.cells[startIdx + offset]);
        return new SegmentState('row', rowIndex, cells);
    }

    column(columnIndex) {
        const cells = GRID_INDEXES.map((row) => this.cells[(row * GRID_SIZE) + columnIndex]);
        return new SegmentState('column', columnIndex, cells);
    }

    region(regionIndex) {
        const regionRow = Math.trunc(regionIndex / REGION_SIZE);
        const regionCol = regionIndex % REGION_SIZE;

        const cells = REGION_INDEXES.flatMap((rowOffset) => {
            const row = (regionRow * REGION_SIZE) + rowOffset;
            return REGION_INDEXES.map((colOffset) => {
                const col = (regionCol * REGION_SIZE) + colOffset;
                const index = (row * GRID_SIZE) + col;
                return this.cells[index];
            });
        });
        return new SegmentState('region', regionIndex, cells);
    }

    segment(segmentType, segmentIndex) {
        switch (segmentType) {
            case 'row':
                return this.row(segmentIndex);
            case 'column':
                return this.column(segmentIndex);
            case 'region':
                return this.region(segmentIndex);
            default:
                throw new Error(`Unknown segment type '${segmentType}'`);
        }
    }
}

class SegmentState {
    constructor(type, index, cells) {
        this.type = type;
        this.index = index;
        this.cells = cells;
    }

    get values() {
        return this.cells
            .map((cell) => cell.value)
            .filter((value) => !isNaN(value));
    }

    get cellsByAvailableValue() {
        return this._cellsByAvailableValue = this._cellsByAvailableValue
            || this.cells.reduce((acc, cell) => {
                if (isNaN(cell.value)) {
                    for (const value of cell.availableValues) {
                        if (!acc.has(value)) {
                            acc.set(value, []);
                        }
                        acc.get(value).push(cell);
                    }
                }
                return acc;
            }, new Map());
    }

    isValueAvailable(value) {
        return this.cellsByAvailableValue.has(value);
    }
}
