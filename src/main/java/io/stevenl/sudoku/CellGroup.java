package io.stevenl.sudoku;

import java.util.HashSet;
import java.util.Set;

public class CellGroup {
    private Cell[] cells;
    private Set<Integer> wantedValues;

    public CellGroup(int size) {
        cells = new Cell[size];

        wantedValues = new HashSet<>(size);
        for (int v = 1; v <= size; v++) {
            wantedValues.add(v);
        }
    }

    public void addCell(int index, Cell cell) {
        cells[index] = cell;

        int value = cell.getValue();
        if (value > 0) {
            wantedValues.remove(value);
        }
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    public void removeWantedValue(int value) {
        wantedValues.remove(value);

        // Update the cells in this group
        for (Cell c : cells) {
            if (c.getValue() > 0) {
                continue;
            }
            c.removePossibleValue(value);
        }
    }
}
