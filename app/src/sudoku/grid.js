import { gridReducer, SetValueAction } from './gridReducer';

export const GRID_SIZE = 9;
export const GRID_INDEXES = [0, 1, 2, 3, 4, 5, 6, 7, 8];
export const SEGMENT_TYPES = ['row', 'column', 'region'];
const REGION_SIZE = 3;
const REGION_INDEXES = [0, 1, 2];
const AVAILABLE_VALUES = [1, 2, 3, 4, 5, 6, 7, 8, 9];

export class GridState {
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

    init(gridString) {
        let grid = this;
        const cells = this._parseGrid(gridString);
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

    isValueAvailable(value) {
        if (!this.availableValues) {
            const availableValues = new Set(AVAILABLE_VALUES);
            for (const v of this.values) {
                availableValues.delete(v);
            }
            this.availableValues = availableValues;
        }
        return this.availableValues.has(value);
    }
}

export class CellState {
    constructor(index, value, readOnly, errors, availableValues) {
        this.index = index;
        this.value = value;

        if (readOnly) {
            this.readOnly = true;
            if (errors || availableValues) {
                throw new Error(`readOnly cell should not have errors or availableValues ${errors} or ${availableValues}`);
            }
        } else {
            this.errors = errors !== undefined ? errors : {row: 0, column: 0, region: 0, total: 0};
            if (isNaN(this.value)) {
                this.availableValues = availableValues || new Set(AVAILABLE_VALUES);
            } else {
                this.availableValues = new Set();
            }
        }

        // Object.freeze(this.errors);
        // Object.freeze(this.availableValues);
        Object.freeze(this);
    }

    get row() {
        return Math.trunc(this.index / GRID_SIZE);
    }

    get column() {
        return this.index % GRID_SIZE;
    }

    get regionRow() {
        return Math.trunc(this.row / REGION_SIZE);
    }

    get regionColumn() {
        return Math.trunc(this.column / REGION_SIZE);
    }

    get region() {
        return (this.regionRow * REGION_SIZE) + this.regionColumn;
    }

    setError(segmentType) {
        if (this.errors[segmentType]) {
            throw new Error(`Error has already been set for segment type ${segmentType}`);
        }
        this.errors[segmentType] = 1;
        this.errors.total += 1;
    }

    clearError(segmentType) {
        if (!this.errors[segmentType]) {
            throw new Error(`Error is not set for segment type ${segmentType}`);
        }
        this.errors[segmentType] = 0;
        this.errors.total -= 1;
    }

    addAvailableValue(value) {
        this.availableValues.add(value);
    }

    removeAvailableValues(values) {
        for (const value of values) {
            this.availableValues.delete(value);
        }
    }
}
