package io.stevenl.sudoku;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Solver {
    private static final Set<Integer> ALL_POSSIBLE_VALUES =
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private Board board;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    public Solver(Board board) {
        this.board = board;

        int size = Board.SIZE;
        int nrCells = size * size;
        unsolvedCells = new HashSet<>(nrCells);
        possibleValuesPerCell = new HashMap<>(nrCells);

        for (int index = 0; index < nrCells; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(ALL_POSSIBLE_VALUES));
        }

        // Update the observers with the cells that have already been set
        for (int index = 0; index < nrCells; index++) {
            Cell cell = board.getCell(index);
            int value = cell.getValue();

            if (value > 0) {
                setCellValue(index, value);
                unsolvedCells.remove(index);
            }
        }
    }

    private void setCellValue(int index, int value) {
        Cell cell = board.getCell(index);
        cell.setValue(value);

        possibleValuesPerCell.get(index).clear();

        // Update the possible values in the affected cells
        // (in the same row, column and square)
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
                possibleValuesPerCell.get(idx).remove(value);
            }
        }
    }

    public void solve() throws Exception {
        while (!unsolvedCells.isEmpty()) {
            boolean progress = false;
            Iterator<Integer> unsolvedIter = unsolvedCells.iterator();
            while (unsolvedIter.hasNext()) {
                int index = unsolvedIter.next();
                Set<Integer> possibleValues = possibleValuesPerCell.get(index);

                if (possibleValues.size() == 1) {
                    int value = possibleValues.iterator().next();
                    setCellValue(index, value);

                    unsolvedIter.remove();
                    progress = true;
                }
            }

            if (!progress) {
                throw new Exception("Couldn't solve it");
            }
        }
    }

    public String possibleValues() {
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
