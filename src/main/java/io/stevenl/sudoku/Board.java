package io.stevenl.sudoku;

public class Board {
    public static final int SIZE = 9;
    public static final int SQUARE_SIZE = 3;

    private Cell[] cells;
    private CellGroup[] rows;
    private CellGroup[] columns;
    private CellGroup[] squares;

    public Board() {
        cells = new Cell[SIZE * SIZE];
        rows = new CellGroup[SIZE];
        columns = new CellGroup[SIZE];
        squares = new CellGroup[SIZE];

        for (int i = 0; i < SIZE; i++) {
            rows[i] = new CellGroup(SIZE);
            columns[i] = new CellGroup(SIZE);
            squares[i] = new CellGroup(SIZE);
        }
    }

    public void addCell(int row, int col, int value) throws Exception {
        int index = row * SIZE + col;
        if (cells[index] != null) {
            throw new Exception(String.format("Cell already exists at (%d, %d)", row, col));
        }

        Cell cell = new Cell(value);
        cells[index] = cell;

        rows[row].addCell(col, cell);
        columns[col].addCell(row, cell);

        int idxInSq = getCellIndexInSquare(index);
        getSquare(index).addCell(idxInSq, cell);
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    public Cell getCell(int row, int col) {
        int index = row * SIZE + col;
        return cells[index];
    }

    private int getSquareIndex(int index) {
        int row = index / SIZE;
        int col = index % SIZE;

        int sqRow = row / SQUARE_SIZE;
        int sqCol = col / SQUARE_SIZE;

        return sqRow * SQUARE_SIZE + sqCol;
    }

    public CellGroup getSquare(int index) {
        int sqIndex = getSquareIndex(index);
        return squares[sqIndex];
    }

    private int getCellIndexInSquare(int index) {
        int row = index / SIZE;
        int col = index % SIZE;

        int sqRow = row / SQUARE_SIZE;
        int sqCol = col / SQUARE_SIZE;

        int rowInSq = row - sqRow * SQUARE_SIZE;
        int colInSq = col - sqCol * SQUARE_SIZE;

        return rowInSq * SQUARE_SIZE + colInSq;
    }
}
