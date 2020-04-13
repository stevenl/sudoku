package io.stevenl.sudoku;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    public static final int SIZE = 9;
    public static final int SQUARE_SIZE = 3;

    private Cell[] cells;
    private Cell[][] rows;
    private Cell[][] columns;
    private Cell[][] squares;

    public Board() {
        cells   = new Cell[SIZE * SIZE];
        rows    = new Cell[SIZE][SIZE];
        columns = new Cell[SIZE][SIZE];
        squares = new Cell[SIZE][SIZE];

        for (int i = 0; i < SIZE * SIZE; i++) {
            addCell(i);
        }
    }

    public Board(String input) throws SudokuException {
        this();

        int[] values = input.chars()
                .filter(c -> c != '\n')       // ignore linebreaks
                .map(c -> c == ' ' ? '0' : c) // convert space to 0
                .map(c -> Character.digit(c, 10))
                .toArray();

        int index = 0;
        for (int value : values) {
            getCell(index).setValue(value);
            index++;
        }
    }

    private void addCell(int index) {
        Cell cell = new Cell(index);
        cells[index] = cell;

        rows[cell.getRowIndex()][cell.getColumnIndex()] = cell;
        columns[cell.getColumnIndex()][cell.getRowIndex()] = cell;
        squares[cell.getSquareIndex()][cell.getIndexInSquare()] = cell;
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    public Cell[] getCellRow(int index) {
        int rowIdx = getCell(index).getRowIndex();
        return rows[rowIdx];
    }

    public Cell[] getCellColumn(int index) {
        int colIdx = getCell(index).getColumnIndex();
        return columns[colIdx];
    }

    public Cell[] getCellSquare(int index) {
        int sqIdx = getCell(index).getSquareIndex();
        return squares[sqIdx];
    }

    @Override
    public String toString() {
        return Stream.of(cells)
                .map(Cell::getValue)
                .map(v -> Character.forDigit(v, 10))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
