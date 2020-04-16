package io.stevenl.sudoku.board;

import io.stevenl.sudoku.Constants;

public class Cell {
    private int index;
    private int rowIndex;
    private int columnIndex;
    private int squareIndex;
    private int indexInSquare;
    private int value;

    public Cell(int index) {
        if (index < 0 || index >= Constants.NR_CELLS) {
            throw new IllegalArgumentException("Invalid cell index: " + index);
        }

        this.index = index;
        this.rowIndex = index / Constants.SIZE;
        this.columnIndex = index % Constants.SIZE;

        int squareRow = rowIndex / Constants.SQUARE_SIZE;
        int squareCol = columnIndex / Constants.SQUARE_SIZE;
        this.squareIndex = squareRow * Constants.SQUARE_SIZE + squareCol;

        int rowInSquare = rowIndex - squareRow * Constants.SQUARE_SIZE;
        int columnInSquare = columnIndex - squareCol * Constants.SQUARE_SIZE;
        this.indexInSquare = rowInSquare * Constants.SQUARE_SIZE + columnInSquare;
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

    public int getSquareIndex() {
        return squareIndex;
    }

    public int getIndexInSquare() {
        return indexInSquare;
    }

    public void setValue(int value) {
        if (value < 1 || value > 9) {
            throw new IllegalArgumentException("Invalid cell value: " + value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
