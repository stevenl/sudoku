export const GRID_SIZE = 9;

export function emptyGrid() {
    return Array(GRID_SIZE ** 2)
        .map((v, i) => {
            return {index: i, value: NaN};
        });
}

export function parseGrid(gridString) {
    const values = (gridString).split('');
    return values.map((val, idx) => {
        if (!val || val < 1) {
            val = NaN;
        }
        const cell = {index: idx, value: val};
        if (!isNaN(val)) {
            cell.readOnly = true;
        }
        return cell;
    });
}

export function gridReducer(grid, action) {
    const cell = grid[action.index];
    if (cell.readOnly || !action.value) {
        return grid;
    }

    const newCell = Object.create(cell);
    newCell.value = action.value;

    const newGrid = grid.slice();
    newGrid[action.index] = newCell;
    return newGrid;
}

// export { emptyGrid, parseGrid, gridReducer };
