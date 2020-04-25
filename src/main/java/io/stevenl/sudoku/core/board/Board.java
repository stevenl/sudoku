package io.stevenl.sudoku.core.board;

import io.stevenl.sudoku.core.SudokuException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    public static final int SIZE = 9;
    public static final int NR_CELLS = SIZE * SIZE;
    public static final int SQUARE_SIZE = 3;

    private Cell[] cells;
    private Cell[][] rows;
    private Cell[][] columns;
    private Cell[][] squares;

    public Board() {
        cells   = new Cell[NR_CELLS];
        rows    = new Cell[SIZE][SIZE];
        columns = new Cell[SIZE][SIZE];
        squares = new Cell[SIZE][SIZE];

        for (int i = 0; i < NR_CELLS; i++) {
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

        if (values.length < NR_CELLS) {
            throw new SudokuException(String.format("Input is too short: %s", input));
        }
        if (values.length > NR_CELLS) {
            throw new SudokuException(String.format("Input is too long: %s", input));
        }

        int index = 0;
        for (int value : values) {
            if (value < 0 || value > SIZE) {
                throw new SudokuException(String.format("Invalid input value: %d", value));
            }
            if (value != 0) {
                getCell(index).setValue(value);
            }
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

    public Cell[] getCells() {
        return cells;
    }

    public Cell[][] getRows() {
        return rows;
    }

    public Cell[] getRow(int rowIndex) {
        return rows[rowIndex];
    }

    public Cell[] getColumn(int colIndex) {
        return columns[colIndex];
    }

    public Cell[] getSquare(int squareIndex) {
        return squares[squareIndex];
    }

    public Cell[] getSegment(SegmentType segmentType, int segmentIndex) {
        Cell[] segmentCells;
        switch (segmentType) {
            case ROW:
                segmentCells = getRow(segmentIndex);
                break;
            case COLUMN:
                segmentCells = getColumn(segmentIndex);
                break;
            case SQUARE:
                segmentCells = getSquare(segmentIndex);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + segmentType);
        }
        return segmentCells;
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
