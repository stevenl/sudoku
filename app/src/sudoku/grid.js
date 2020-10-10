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

class CellState {
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
