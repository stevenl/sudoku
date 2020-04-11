package io.stevenl.sudoku;

public class CellGroup {
    private Cell[] cells;

    public CellGroup(int size) {
        cells = new Cell[size];
    }

    public void addCell(int index, Cell cell) {
        cells[index] = cell;
    }

    public Cell[] getCells() {
        return cells;
    }
}
