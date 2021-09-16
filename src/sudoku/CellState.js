import {GRID_SIZE} from "./GridState";

export default class CellState {
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

    setAvailableValue(value) {
        this.availableValues.clear();
        this.availableValues.add(value);
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
