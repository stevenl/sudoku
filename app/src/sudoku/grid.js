export const GRID_SIZE = 9;
export const GRID_INDEXES = [0, 1, 2, 3, 4, 5, 6, 7, 8];
const REGION_SIZE = 3;
const REGION_INDEXES = [0, 1, 2];

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

    rowCells(row) {
        const startIdx = row * GRID_SIZE;
        return GRID_INDEXES.map((offset) => this.cells[startIdx + offset]);
    }
    columnCells(column) {
        return GRID_INDEXES.map((row) => this.cells[(row * GRID_SIZE) + column]);
    }
    regionCells(region) {
        const regionRow = Math.trunc(region / REGION_SIZE);
        const regionCol = region % REGION_SIZE;

        return REGION_INDEXES.flatMap((rowOffset) => {
            const row = (regionRow * REGION_SIZE) + rowOffset;
            return REGION_INDEXES.map((colOffset) => {
                const col = (regionCol * REGION_SIZE) + colOffset;
                const index = (row * GRID_SIZE) + col;
                return this.cells[index];
            });
        });
    }
    segmentCells(segment) {
        switch (segment.type) {
            case 'row':
                return this.rowCells(segment.index);
            case 'column':
                return this.columnCells(segment.index);
            case 'region':
                return this.regionCells(segment.index);
            default:
                throw new Error(`Unknown segment type '${segment.type}'`);
        }
    }
    getCellsGroupedByValue(segment) {
        const cells = this.segmentCells(segment);
        return cells.reduce((acc, cell) => {
            const value = cell.value;
            const groupedCells = acc[value] || [];
            return {
                ...acc,
                ...(!isNaN(value) ? {[value]: [...groupedCells, cell]} : {}),
            };
        }, {});
    }
}

export class CellState {
    constructor(index, value, readOnly, errors) {
        this.index = index;
        this.value = value;

        if (readOnly) {
            this.readOnly = true;
        } else {
            this.errors = errors !== undefined ? errors : {row: 0, column: 0, region: 0, total: 0};
        }
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
}
