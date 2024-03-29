import assert from "assert";
import {GRID_SIZE, REGION_SIZE} from "./Grid";

export const AVAILABLE_VALUES = [1, 2, 3, 4, 5, 6, 7, 8, 9];

export default class CellState {
    constructor(index, value, readOnly, errors, availableValues) {
        this.index = index;
        this.value = value;

        if (readOnly) {
            this.readOnly = true;
            assert(!errors, `readOnly cell should not have errors '${errors}'`);
            assert(!availableValues, `readOnly cell should not have availableValues '${availableValues}'`);
        } else {
            this.errors = errors !== undefined ? errors : {row: 0, column: 0, region: 0, total: 0};
            if (!this.value) {
                this.availableValues = availableValues || new Set(AVAILABLE_VALUES);
            } else {
                this.availableValues = new Set();
            }
        }

        Object.freeze(this.errors);
        Object.freeze(this.availableValues);
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

    segment(segmentType) {
        switch (segmentType) {
            case 'row':
                return this.row;
            case 'column':
                return this.column;
            case 'region':
                return this.region;
            default:
                throw new Error(`Unknown segment type '${segmentType}'`);
        }
    }
}
