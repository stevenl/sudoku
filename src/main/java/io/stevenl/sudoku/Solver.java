package io.stevenl.sudoku;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Solver {
    private static final Set<Integer> ALL_POSSIBLE_VALUES =
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private Board board;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    Set<Integer> solvable = new HashSet<>();

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

        // Initialise the queue of solvable cells
        solvable.addAll(unsolvedCells
                .stream()
                .filter(index -> {
                    Set<Integer> possibleValues = possibleValuesPerCell.get(index);
                    return possibleValues.size() == 1;
                })
                .collect(Collectors.toList())
        );
        //System.out.println("solvable = " + solvable);
    }

    private Set<Integer> setCellValue(int index, int value) {
        Cell cell = board.getCell(index);
        cell.setValue(value);

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
                affectedCells.add(idx);
                possibleValuesPerCell.get(idx).remove(value);
            }
        }
        return affectedCells;
    }

    public void solve() throws Exception {
        Iterator<Integer> solvableIter = solvable.iterator();

        while (!unsolvedCells.isEmpty()) {
            boolean progress = false;
            while (solvableIter.hasNext()) {
                int index = solvableIter.next();
                Set<Integer> possibleValues = possibleValuesPerCell.get(index);

                if (possibleValues.size() == 1) {
                    int value = possibleValues.iterator().next();
                    Set<Integer> affected = setCellValue(index, value);

                    solvableIter.remove();
                    unsolvedCells.remove(index);

                    for (int idx : affected) {
                        if (possibleValuesPerCell.get(idx).size() == 1) {
                            solvable.add(idx);
                        }
                    }

                    progress = true;
                }
            }

            if (!progress) {
                for (int i = 0; i < Board.SIZE; i++) {
                    progress = progress || solveNextByEliminationWithinGroup(board.getCellRow(i));
                    progress = progress || solveNextByEliminationWithinGroup(board.getCellColumn(i));
                    progress = progress || solveNextByEliminationWithinGroup(board.getCellSquare(i));
                }
            }

            if (!progress) {
                throw new Exception("Couldn't solve it");
            }
        }
    }

    private boolean solveNextByEliminationWithinGroup(Cell[] group) {
        boolean progress = false;
        Map<Integer, Integer> nrPossibleCellsPerNumber = new HashMap<>();
        for (Cell cell : group) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = possibleValuesPerCell.get(index);

            for (int value : possibleValues) {
                int count = nrPossibleCellsPerNumber.getOrDefault(value, 0);
                nrPossibleCellsPerNumber.put(value, count + 1);
            }
        }

        for (Map.Entry<Integer, Integer> e : nrPossibleCellsPerNumber.entrySet()) {
            if (e.getValue() == 1) {
                int value = e.getKey();
                for (Cell cell : group) {
                    int index = cell.getIndex();
                    Set<Integer> possibleValues = possibleValuesPerCell.get(index);
                    if (!possibleValues.contains(value)) {
                        continue;
                    }

                    setCellValue(index, value);
                    unsolvedCells.remove(index);
                    progress = true;
                }
            }
        }
        return progress;
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
