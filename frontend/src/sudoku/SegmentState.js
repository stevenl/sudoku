import {GRID_INDEXES, GRID_SIZE, REGION_INDEXES, REGION_SIZE} from "./Grid";

export default class SegmentState {
    static newFrom(gridCells, segmentIndex, segmentType) {
        switch (segmentType) {
            case 'row':
                return row(gridCells, segmentIndex);
            case 'column':
                return column(gridCells, segmentIndex);
            case 'region':
                return region(gridCells, segmentIndex);
            default:
                throw new Error(`Unknown segment type '${segmentType}'`);
        }
    }

    constructor(type, index, cells) {
        this.type = type;
        this.index = index;
        this.cells = cells;
    }

    get values() {
        return this.cells
            .map(cell => cell.value)
            .filter(value => !!value);
    }

    get cellsByAvailableValue() {
        return this._cellsByAvailableValue = this._cellsByAvailableValue
            || this.cells.reduce((acc, cell) => {
                if (!cell.value) {
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

function row(gridCells, rowIndex) {
    const startIdx = rowIndex * GRID_SIZE;
    const cells = GRID_INDEXES.map(offset => gridCells[startIdx + offset]);
    return new SegmentState('row', rowIndex, cells);
}

function column(gridCells, columnIndex) {
    const cells = GRID_INDEXES.map(row => gridCells[(row * GRID_SIZE) + columnIndex]);
    return new SegmentState('column', columnIndex, cells);
}

function region(gridCells, regionIndex) {
    const regionRow = Math.trunc(regionIndex / REGION_SIZE);
    const regionCol = regionIndex % REGION_SIZE;

    const cells = REGION_INDEXES.flatMap(rowOffset => {
        const row = (regionRow * REGION_SIZE) + rowOffset;
        return REGION_INDEXES.map(colOffset => {
            const col = (regionCol * REGION_SIZE) + colOffset;
            const index = (row * GRID_SIZE) + col;
            return gridCells[index];
        });
    });
    return new SegmentState('region', regionIndex, cells);
}
