package io.stevenl.sudoku;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Solver {
    private static final Set<Integer> ALL_POSSIBLE_VALUES =
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private Board board;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    Queue<Integer> solvable = new LinkedList<>();

    public Solver(Board board) throws SudokuException {
        this.board = board;

        // Initialise for an empty board
        int size = Board.SIZE;
        int nrCells = size * size;
        unsolvedCells = new HashSet<>(nrCells);
        possibleValuesPerCell = new HashMap<>(nrCells);
        for (int index = 0; index < nrCells; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(ALL_POSSIBLE_VALUES));
        }

        // Add the cells that have already been set
        for (int index = 0; index < nrCells; index++) {
            Cell cell = board.getCell(index);
            int value = cell.getValue();

            if (value > 0) {
                setCellValue(index, value);
            }
        }
    }

    private void setCellValue(int index, int value) throws SudokuException {
        Cell cell = board.getCell(index);
        cell.setValue(value);

        unsolvedCells.remove(index);
        possibleValuesPerCell.get(index).clear();

        // Update the possible values in the affected cells
        // (in the same row, column and square)
        Set<Integer> affectedCells = new HashSet<>();
        Cell[][] cellGroups = {
                board.getCellRow(index),
                board.getCellColumn(index),
                board.getCellSquare(index)
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
    }

    public void solve() throws SudokuException {
        Iteration:
        while (!unsolvedCells.isEmpty()) {
            while (!solvable.isEmpty()) {
                int index = solvable.remove();

                if (!unsolvedCells.contains(index)) {
                    throw new SudokuException("Already solved: " + index);
                }

                solveIfSolePossibility(index);
            }

            for (int i = 0; i < Board.SIZE; i++) {
                Cell[][] cellGroups = {
                        board.getCellRow(i),
                        board.getCellColumn(i),
                        board.getCellSquare(i)
                };
                for (Cell[] cellGroup : cellGroups) {
                    if (solveIfSolePossibilityWithinGroup(cellGroup)) {
                        continue Iteration;
                    }
                }
            }

            if (!unsolvedCells.isEmpty()) {
                throw new SudokuException("Couldn't solve it");
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

    private boolean solveIfSolePossibility(int index) throws SudokuException {
        Set<Integer> possibleValues = possibleValuesPerCell.get(index);

        if (possibleValues.size() == 1) {
            int value = possibleValues.iterator().next();
            setCellValue(index, value);

            return true;
        }
        return false;
    }

    private boolean solveIfSolePossibilityWithinGroup(Cell[] cellGroup) throws SudokuException {
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
