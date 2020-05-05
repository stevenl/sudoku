package io.stevenl.sudoku.core.solver;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.grid.Cell;
import io.stevenl.sudoku.core.grid.Segment;
import io.stevenl.sudoku.core.grid.SegmentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solver {
    private final Grid grid;
    private final Set<Integer> solvableCells;
    private final Set<Integer> unsolvedCells;
    private final Map<Integer, Set<Integer>> possibleValuesPerCell;

    public Solver(Grid grid) {
        this.grid = grid;

        // Initialise for an empty grid
        solvableCells = new HashSet<>(Grid.NR_CELLS);
        unsolvedCells = new HashSet<>(Grid.NR_CELLS);
        possibleValuesPerCell = new HashMap<>(Grid.NR_CELLS);
        for (int index = 0; index < Grid.NR_CELLS; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(Cell.POSSIBLE_VALUES));
        }

        // Add the cells that have already been set
        for (Cell cell : grid.getCells()) {
            int index = cell.getIndex();
            Integer value = cell.getValue();

            if (value != null) {
                setCellValue(index, value);
            }
        }

        SimpleSolver.optimisePossibleValues(this, grid);
        if (solvableCells.isEmpty()) {
            AdvancedSolver.optimisePossibleValues(this, grid);
        }
    }

    private void setCellValue(int index, int value) {
        Cell cell = grid.getCell(index);
        cell.setValue(value);

        solvableCells.remove(index);
        unsolvedCells.remove(index);
        getPossibleValues(index).clear();

        // We can remove this value from the possible values for the cells in
        // the same row, column, and region.
        Segment[] affectedSegments = {
                grid.getRow(cell.getRowIndex()),
                grid.getColumn(cell.getColumnIndex()),
                grid.getRegion(cell.getRegionIndex())
        };
        for (Segment segment : affectedSegments) {
            removePossibleValue(segment, value);
        }
    }

    public Set<Integer> getSolvableCells() {
        return solvableCells;
    }

    public boolean isCellSolvable(int index) {
        return solvableCells.contains(index);
    }

    public Map<Integer, Set<Integer>> getPossibleValuesPerCell() {
        return possibleValuesPerCell;
    }

    public Set<Integer> getPossibleValues(int index) {
        return possibleValuesPerCell.get(index);
    }

    void removePossibleValue(Segment segment, int value) {
        for (Cell cell : segment.getCells()) {
            if (cell.getValue() != null) {
                // No need to update cells that have already been solved
                continue;
            }

            int index = cell.getIndex();
            removePossibleValue(index, value);
        }
    }

    void removePossibleValue(int index, int value) {
        Set<Integer> possibleValues = getPossibleValues(index);
        if (possibleValues.contains(value)) {
            possibleValues.remove(value);

            // We can mark the cell as solvable if it only has 1 remaining possible value
            if (possibleValues.size() == 1) {
                solvableCells.add(index);
            }
        }
    }

    Map<Integer, Set<Integer>> getPossibleCellsPerValue(Segment segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = new HashMap<>();

        for (Cell cell : segment.getCells()) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = getPossibleValues(index);

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

    public void solve() throws SudokuException {
        while (!unsolvedCells.isEmpty()) {
            Cell hint = nextHint();
            setCellValue(hint.getIndex(), hint.getValue());
        }
    }

    private Cell nextHint() throws SudokuException {
        if (solvableCells.isEmpty()) {
            SimpleSolver.optimisePossibleValues(this, grid);

            if (solvableCells.isEmpty()) {
                AdvancedSolver.optimisePossibleValues(this, grid);
            }
            if (solvableCells.isEmpty()) {
                throw new SudokuException(String.format(
                        "Couldn't solve it. There are still %d unsolved cells", unsolvedCells.size()));
            }
        }

        int index = solvableCells.iterator().next();
        Set<Integer> possibleValues = getPossibleValues(index);
        if (possibleValues.size() != 1) {
            throw new AssertionError("Solvable cell has no solution");
        }
        int value = possibleValues.iterator().next();

        return new Cell(index, value);
    }

    public String debugPossibleValues(SegmentType segmentType) {
        StringBuilder sb = new StringBuilder();
        for (int segmentIndex = 0; segmentIndex < Grid.SIZE; segmentIndex++) {
            sb.append(debugPossibleValues(segmentType, segmentIndex));
        }
        return sb.toString();
    }

    public String debugPossibleValues(SegmentType segmentType, int segmentIndex) {
        StringBuilder sb = new StringBuilder();
        Segment segment = grid.getSegment(segmentType, segmentIndex);

        for (Cell cell : segment.getCells()) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = getPossibleValues(index);
            sb.append(String.format("%s %d (%d, %d): %s%n", segmentType, segmentIndex,
                    cell.getRowIndex(), cell.getColumnIndex(), possibleValues));
        }
        sb.append("\n");

        return sb.toString();
    }
}
