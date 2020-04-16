package io.stevenl.sudoku.solver;

import io.stevenl.sudoku.SudokuException;
import io.stevenl.sudoku.board.Board;
import io.stevenl.sudoku.board.Cell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

public class Solver {
    private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());
    private static final Set<Integer> ALL_POSSIBLE_VALUES =
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private Board board;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    Queue<Integer> solvable = new LinkedList<>();

    public Solver(Board board) {
        this.board = board;

        // Initialise for an empty board
        unsolvedCells = new HashSet<>(Board.NR_CELLS);
        possibleValuesPerCell = new HashMap<>(Board.NR_CELLS);
        for (int index = 0; index < Board.NR_CELLS; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(ALL_POSSIBLE_VALUES));
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

        unsolvedCells.remove(index);
        possibleValuesPerCell.get(index).clear();

        // Update the possible values in the affected cells
        // (in the same row, column and square)
        Set<Integer> affectedCells = new HashSet<>();
        Cell[][] cellGroups = {
                board.getRow(cell.getRowIndex()),
                board.getColumn(cell.getColumnIndex()),
                board.getSquare(cell.getSquareIndex())
        };
        for (Cell[] cellGroup : cellGroups) {
            for (Cell affectedCell : cellGroup) {
                if (affectedCell.getValue() > 0) {
                    continue;
                }

                int idx = affectedCell.getIndex();
                Set<Integer> possibleValues = possibleValuesPerCell.get(idx);
                if (possibleValues.contains(value)) {
                    possibleValues.remove(value);
                    affectedCells.add(idx);
                }
            }
        }
        addSolveableCells(affectedCells);

        //LOGGER.info(String.format("SET %d = %d (%d, %d) %s", index, value, cell.getRowIndex(), cell.getColumnIndex(), affectedCells));
        //try { System.in.read(); } catch (IOException e) { LOGGER.warning(e.toString()); }
    }

    public void solve() throws SudokuException {
        Iteration:
        while (!unsolvedCells.isEmpty()) {
            while (!solvable.isEmpty()) {
                int index = solvable.remove();

                if (!unsolvedCells.contains(index)) {
                    throw new AssertionError("Already solved: " + index);
                }

                solveIfSolePossibility(index);
            }

            for (int i = 0; i < Board.SIZE; i++) {
                Cell[][] cellGroups = {
                        board.getRow(i),
                        board.getColumn(i),
                        board.getSquare(i)
                };
                for (Cell[] cellGroup : cellGroups) {
                    if (solveIfSolePossibilityWithinGroup(cellGroup)) {
                        continue Iteration;
                    }
                }
            }

            if (!unsolvedCells.isEmpty()) {
                throw new SudokuException(String.format("Couldn't solve it. There are still %d unsolved cells", unsolvedCells.size()));
            }
        }
    }

    private void addSolveableCells(Set<Integer> cells) {
        for (int index : cells) {
            int nrPossibleValues = possibleValuesPerCell.get(index).size();
            if (nrPossibleValues == 1) {
                solvable.add(index);
            }
        }
    }

    private boolean solveIfSolePossibility(int index) {
        Set<Integer> possibleValues = possibleValuesPerCell.get(index);

        if (possibleValues.size() == 1) {
            int value = possibleValues.iterator().next();
            setCellValue(index, value);

            return true;
        }
        return false;
    }

    private boolean solveIfSolePossibilityWithinGroup(Cell[] cellGroup) {
        Map<Integer, Integer> nrPossibleCellsPerValue = new HashMap<>();
        for (Cell cell : cellGroup) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = possibleValuesPerCell.get(index);

            for (int value : possibleValues) {
                int count = nrPossibleCellsPerValue.getOrDefault(value, 0);
                nrPossibleCellsPerValue.put(value, count + 1);
            }
        }

        // Find where a value is possible only in one cell within a group (e.g. row, column or square)
        for (Map.Entry<Integer, Integer> e : nrPossibleCellsPerValue.entrySet()) {
            if (e.getValue() == 1) {
                int value = e.getKey();

                for (Cell cell : cellGroup) {
                    int index = cell.getIndex();
                    Set<Integer> possibleValues = possibleValuesPerCell.get(index);

                    if (possibleValues.contains(value)) {
                        setCellValue(index, value);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String debugPossibleValues() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Set<Integer> possibleValues = possibleValuesPerCell.get(index);
                sb.append(String.format("%d (%d, %d): %s%n", index, i, j, possibleValues));
                index++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
