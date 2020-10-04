export const CELL_RANGE = [0, 1, 2, 3, 4, 5, 6, 7, 8];
export const GRID_SIZE = 9;

export class GridState {
    constructor(array) {
        if (!array) {
            array = this.emptyGrid;
        } else if (typeof array === 'string') {
            array = this.parseGrid(array);
        }
        return Object.assign({}, array);
    }

    emptyGrid() {
        return Array(GRID_SIZE ** 2)
            .map((v, i) => new CellState(i, NaN));
    }

    parseGrid(gridString) {
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
    const cell = grid[action.index];
    if (cell.readOnly || action.value < 1) {
        return grid;
    }

    // Clone the grid
    const newGrid = {...grid};
    // Update the cell according to the action
    newGrid[action.index] = cellReducer(cell, action);

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
