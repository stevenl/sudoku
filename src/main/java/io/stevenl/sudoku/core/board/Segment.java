package io.stevenl.sudoku.core.board;

public class Segment {
    private int index;
    private Cell[] cells;

    public Segment(int index) {
        this.index = index;
        this.cells = new Cell[Board.SIZE];
    }

    public int getIndex() {
        return index;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCell(int cellIndex, Cell cell) {
        cells[cellIndex] = cell;
    }
}
