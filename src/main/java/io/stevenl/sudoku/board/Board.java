package io.stevenl.sudoku.board;

import io.stevenl.sudoku.Constants;
import io.stevenl.sudoku.SudokuException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

    private Cell[] cells;
    private Cell[][] rows;
    private Cell[][] columns;
    private Cell[][] squares;

    public Board() {
        cells   = new Cell[Constants.NR_CELLS];
        rows    = new Cell[Constants.SIZE][Constants.SIZE];
        columns = new Cell[Constants.SIZE][Constants.SIZE];
        squares = new Cell[Constants.SIZE][Constants.SIZE];

        for (int i = 0; i < Constants.NR_CELLS; i++) {
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

        if (values.length < Constants.NR_CELLS) {
            throw new SudokuException(String.format("Input is too short: %s", input));
        }
        if (values.length > Constants.NR_CELLS) {
            throw new SudokuException(String.format("Input is too long: %s", input));
        }

        int index = 0;
        for (int value : values) {
            if (value < 0 || value > Constants.SIZE) {
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

    public Cell[] getRow(int rowIdx) {
        return rows[rowIdx];
    }

    public Cell[] getColumn(int colIdx) {
        return columns[colIdx];
    }

    public Cell[] getSquare(int squareIdx) {
        return squares[squareIdx];
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
