package io.stevenl.sudoku;

import java.util.HashSet;
import java.util.Set;

public class CellGroup {
    private Cell[] cells;
    private Set<Integer> wantedValues;

    public CellGroup(int size) {
        cells = new Cell[size];

        wantedValues = new HashSet<>(size);
        for (int i = 1; i <= size; i++) {
            wantedValues.add(i);
        }
    }

    public void addCell(int index, Cell cell) {
        cells[index] = cell;

        int value = cell.getValue();
        if (value > 0) {
            wantedValues.remove(value);
        }
    }

    public Cell[] getCells() {
        return cells;
    }
}
