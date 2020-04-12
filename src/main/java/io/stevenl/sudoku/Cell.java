package io.stevenl.sudoku;

public class Cell {
    private int index;
    private int rowIndex;
    private int columnIndex;
    private int squareIndex;
    private int indexInSquare;
    private int value;

    public Cell(int index) {
        this.index = index;
        this.rowIndex = index / Board.SIZE;
        this.columnIndex = index % Board.SIZE;

        int squareRow = rowIndex / Board.SQUARE_SIZE;
        int squareCol = columnIndex / Board.SQUARE_SIZE;
        this.squareIndex = squareRow * Board.SQUARE_SIZE + squareCol;

        int rowInSquare = rowIndex - squareRow * Board.SQUARE_SIZE;
        int columnInSquare = columnIndex - squareCol * Board.SQUARE_SIZE;
        this.indexInSquare = rowInSquare * Board.SQUARE_SIZE + columnInSquare;
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
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
