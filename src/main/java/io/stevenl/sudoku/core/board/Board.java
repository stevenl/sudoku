package io.stevenl.sudoku.core.board;

import io.stevenl.sudoku.core.SudokuException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    public static final int SIZE        = 9;
    public static final int REGION_SIZE = 3;
    public static final int NR_CELLS    = SIZE * SIZE;

    private Cell[] cells      = new Cell[NR_CELLS];
    private Segment[] rows    = new Segment[SIZE];
    private Segment[] columns = new Segment[SIZE];
    private Segment[] regions = new Segment[SIZE];

    public Board() {
        for (int i = 0; i < SIZE; i++) {
            rows[i]    = new Segment(i);
            columns[i] = new Segment(i);
            regions[i] = new Segment(i);
        }

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

        rows[cell.getRowIndex()].setCell(cell.getColumnIndex(), cell);
        columns[cell.getColumnIndex()].setCell(cell.getRowIndex(), cell);
        regions[cell.getRegionIndex()].setCell(cell.getIndexInRegion(), cell);
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    public Cell[] getCells() {
        return cells;
    }

    public Segment[] getRows() {
        return rows;
    }

    public Segment[] getColumns() {
        return columns;
    }

    public Segment getRow(int rowIndex) {
        return rows[rowIndex];
    }

    public Segment getColumn(int colIndex) {
        return columns[colIndex];
    }

    public Segment getRegion(int regionIndex) {
        return regions[regionIndex];
    }

    public Segment getSegment(SegmentType segmentType, int segmentIndex) {
        Segment segment;
        switch (segmentType) {
            case ROW:
                segment = getRow(segmentIndex);
                break;
            case COLUMN:
                segment = getColumn(segmentIndex);
                break;
            case REGION:
                segment = getRegion(segmentIndex);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + segmentType);
        }
        return segment;
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
