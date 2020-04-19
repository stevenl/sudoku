package io.stevenl.sudoku.solver;

import io.stevenl.sudoku.SudokuException;
import io.stevenl.sudoku.board.Board;
import io.stevenl.sudoku.board.Cell;
import io.stevenl.sudoku.board.SegmentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Solver {
    private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

    private Board board;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    Set<Integer> solvable = new HashSet<>();

    public Solver(Board board) {
        this.board = board;

        // Initialise for an empty board
        unsolvedCells = new HashSet<>(Board.NR_CELLS);
        possibleValuesPerCell = new HashMap<>(Board.NR_CELLS);
        for (int index = 0; index < Board.NR_CELLS; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(Cell.POSSIBLE_VALUES));
        }

        // Add the cells that have already been set
        for (int index = 0; index < Board.NR_CELLS; index++) {
            Cell cell = board.getCell(index);
            int value = cell.getValue();

            if (value > 0) {
                setCellValue(index, value);
            }
        }
    }

    private void setCellValue(int index, int value) {
        Cell cell = board.getCell(index);
        cell.setValue(value);

        solvable.remove(index);
        unsolvedCells.remove(index);
        possibleValuesPerCell.get(index).clear();

        // We can remove this value from the possible values for the cells in
        // the same row, column, and square.
        removePossibleValueForAffectedCells(cell);

        //LOGGER.info(String.format("SET %d = %d (%d, %d) %s", index, value, cell.getRowIndex(), cell.getColumnIndex(), affectedCells));
        //try { System.in.read(); } catch (IOException e) { LOGGER.warning(e.toString()); }
    }

    private void removePossibleValueForAffectedCells(Cell cell) {
        int value = cell.getValue();

        Cell[][] affectedSegments = {
                board.getRow(cell.getRowIndex()),
                board.getColumn(cell.getColumnIndex()),
                board.getSquare(cell.getSquareIndex())
        };
        for (Cell[] segment : affectedSegments) {
            for (Cell affectedCell : segment) {
                if (affectedCell.getValue() > 0) {
                    // No need to update cells that have already been solved
                    continue;
                }

                int index = affectedCell.getIndex();
                removePossibleValue(index, value);
            }
        }
    }

    private void removePossibleValue(int index, int value) {
        Set<Integer> possibleValues = possibleValuesPerCell.get(index);
        if (possibleValues.contains(value)) {
            possibleValues.remove(value);

            // We can mark the cell as solvable if it only has 1 remaining possible value
            if (possibleValues.size() == 1) {
                solvable.add(index);
            }
        }
    }

    public void solve() throws SudokuException {
        while (!unsolvedCells.isEmpty()) {
            Cell hint = nextHintSolePossibility();
            if (hint != null) {
                setCellValue(hint.getIndex(), hint.getValue());
                continue;
            }

            hint = nextHintSolePossibilityWithinSegment();
            if (hint != null) {
                setCellValue(hint.getIndex(), hint.getValue());
                continue;
            }

            if (!unsolvedCells.isEmpty()) {
                throw new SudokuException(String.format("Couldn't solve it. There are still %d unsolved cells", unsolvedCells.size()));
            }
        }
    }

    private Cell nextHintSolePossibility() {
        if (!solvable.isEmpty()) {
            int index = solvable.iterator().next();

            Set<Integer> possibleValues = possibleValuesPerCell.get(index);
            if (possibleValues.size() != 1) {
                throw new AssertionError("Solvable cell has no solution");
            }
            int value = possibleValues.iterator().next();

            return new Cell(index, value);
        }
        return null;
    }

    private Cell nextHintSolePossibilityWithinSegment() {
        for (int i = 0; i < Board.SIZE; i++) {
            Cell[][] segments = {
                    board.getRow(i),
                    board.getColumn(i),
                    board.getSquare(i)
            };
            for (Cell[] segment : segments) {
                Cell hint = nextHintSolePossibilityWithinSegment(segment);
                if (hint != null) {
                    return hint;
                }
            }
        }
        return null;
    }

    private Map<Integer, Set<Integer>> getPossibleCellsPerValue(Cell[] segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = new HashMap<>();

        for (Cell cell : segment) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = possibleValuesPerCell.get(index);

            for (int value : possibleValues) {
                if (!possibleCellsPerValue.containsKey(value)) {
                    possibleCellsPerValue.put(value, new HashSet<>());
                }
                Set<Integer> possibleCells = possibleCellsPerValue.get(value);
                possibleCells.add(index);
            }
        }
        return possibleCellsPerValue;
    }

    private Cell nextHintSolePossibilityWithinSegment(Cell[] segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = getPossibleCellsPerValue(segment);

        // Find where a value is possible only in one cell within a segment (e.g. row, column or square)
        for (Map.Entry<Integer, Set<Integer>> e : possibleCellsPerValue.entrySet()) {
            int value = e.getKey();
            Set<Integer> possibleCells = e.getValue();

            if (possibleCells.size() == 1) {
                int index = possibleCells.iterator().next();
                return new Cell(index, value);
            }
        }
        return null;
    }

    public String debugPossibleValues(SegmentType segmentType) {
        StringBuilder sb = new StringBuilder();
        for (int segmentIndex = 0; segmentIndex < Board.SIZE; segmentIndex++) {
            Cell[] cells = board.getSegment(segmentType, segmentIndex);

            for (Cell cell : cells) {
                int index = cell.getIndex();
                Set<Integer> possibleValues = possibleValuesPerCell.get(index);
                sb.append(String.format("%s %d (%d, %d): %s%n", segmentType, segmentIndex,
                        cell.getRowIndex(), cell.getColumnIndex(), possibleValues));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String debugPossibleValues(SegmentType segmentType, int segmentIndex) {
        StringBuilder sb = new StringBuilder();
        Cell[] cells = board.getSegment(segmentType, segmentIndex);

        for (Cell cell : cells) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = possibleValuesPerCell.get(index);
            sb.append(String.format("%s %d (%d, %d): %s%n", segmentType, segmentIndex,
                    cell.getRowIndex(), cell.getColumnIndex(), possibleValues));
        }
        sb.append("\n");

        return sb.toString();
    }

}
