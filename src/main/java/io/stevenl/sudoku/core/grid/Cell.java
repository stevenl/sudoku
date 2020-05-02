package io.stevenl.sudoku.core.grid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Cell {
    public static final Set<Integer> POSSIBLE_VALUES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
    );

    private int index;
    private int rowIndex;
    private int columnIndex;
    private int regionIndex;
    private int indexInRegion;

    // Use Integer instead of int so we can have null values instead of 0
    // which will also get displayed as blank in html
    private Integer value;

    public Cell(int index) {
        if (index < 0 || index >= Grid.NR_CELLS) {
            throw new IllegalArgumentException("Invalid cell index: " + index);
        }

        this.index = index;
        this.rowIndex = index / Grid.SIZE;
        this.columnIndex = index % Grid.SIZE;

        int regionRow = rowIndex / Grid.REGION_SIZE;
        int regionCol = columnIndex / Grid.REGION_SIZE;
        this.regionIndex = regionRow * Grid.REGION_SIZE + regionCol;

        int rowInRegion = rowIndex - regionRow * Grid.REGION_SIZE;
        int columnInSquare = columnIndex - regionCol * Grid.REGION_SIZE;
        this.indexInRegion = rowInRegion * Grid.REGION_SIZE + columnInSquare;
    }

    public Cell(int index, Integer value) {
        this(index);
        setValue(value);
    }

    public int getIndex() {
        return index;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRegionIndex() {
        return regionIndex;
    }

    public int getIndexInRegion() {
        return indexInRegion;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if (value != null && (value < 1 || value > 9)) {
            throw new IllegalArgumentException("Invalid cell value: " + value);
        }
        this.value = value;
    }
}
