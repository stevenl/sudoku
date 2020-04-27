package io.stevenl.sudoku.core.solver;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.board.Board;
import io.stevenl.sudoku.core.board.Cell;
import io.stevenl.sudoku.core.board.Segment;
import io.stevenl.sudoku.core.board.SegmentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Solver {
    private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

    private Board board;
    private Set<Integer> solvableCells;
    private Set<Integer> unsolvedCells;
    private Map<Integer, Set<Integer>> possibleValuesPerCell;

    public Solver(Board board) {
        this.board = board;

        // Initialise for an empty board
        solvableCells = new HashSet<>(Board.NR_CELLS);
        unsolvedCells = new HashSet<>(Board.NR_CELLS);
        possibleValuesPerCell = new HashMap<>(Board.NR_CELLS);
        for (int index = 0; index < Board.NR_CELLS; index++) {
            unsolvedCells.add(index);
            possibleValuesPerCell.put(index, new HashSet<>(Cell.POSSIBLE_VALUES));
        }

        // Add the cells that have already been set
        for (int index = 0; index < Board.NR_CELLS; index++) {
            Cell cell = board.getCell(index);
            Integer value = cell.getValue();

            if (value != null) {
                setCellValue(index, value);
            }
        }
    }

    public Set<Integer> getSolvableCells() {
        return solvableCells;
    }

    private void setCellValue(int index, int value) {
        Cell cell = board.getCell(index);
        cell.setValue(value);

        solvableCells.remove(index);
        unsolvedCells.remove(index);
        possibleValuesPerCell.get(index).clear();

        // We can remove this value from the possible values for the cells in
        // the same row, column, and region.
        removePossibleValueForAffectedCells(cell);

        //LOGGER.info(String.format("SET %d = %d (%d, %d) %s", index, value, cell.getRowIndex(), cell.getColumnIndex(), affectedCells));
    }

    private void removePossibleValueForAffectedCells(Cell cell) {
        Integer value = cell.getValue();

        Segment[] affectedSegments = {
                board.getRow(cell.getRowIndex()),
                board.getColumn(cell.getColumnIndex()),
                board.getRegion(cell.getRegionIndex())
        };
        for (Segment segment : affectedSegments) {
            for (Cell affectedCell : segment.getCells()) {
                if (affectedCell.getValue() != null) {
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
                solvableCells.add(index);
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
        if (!solvableCells.isEmpty()) {
            int index = solvableCells.iterator().next();

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
        for (SegmentType segmentType : SegmentType.SEGMENT_TYPES) {
            for (int segmentIndex = 0; segmentIndex < Board.SIZE; segmentIndex++) {
                Segment segment = board.getSegment(segmentType, segmentIndex);
                Cell hint = nextHintHard(segment);
                if (hint != null) {
                    return hint;
                }
            }
        }
        return null;
    }

    private Cell nextHintSolePossibilityWithinSegment(Segment segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = getPossibleCellsPerValue(segment);

        // Find where a value is possible only in one cell within a segment (e.g. row, column or region)
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

    public Cell nextHintHard(Segment segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = getPossibleCellsPerValue(segment);

        // Find the value combinations that have number of possible cells matching the number of values in the
        // combination. This means we can rules out other possible values from those cells.
        // E.g. if values 4 and 9 are only possible in cells 55 and 74, and cell 55 has possible values 3, 4, 8, 9,
        // then we can rule out values 3 and 8 from cell 55.

        // Group the values by the number of possible cells,
        // i.e. nrPossibleCells => list of values with that number of possible cells
        Map<Integer, List<Integer>> nrPossibleCells2Values = possibleCellsPerValue
                .keySet().stream().collect(
                        Collectors.groupingBy(v -> possibleCellsPerValue.get(v).size()));
        //LOGGER.info("HERE = " + nrPossibleCells2Values.toString());

        // nrPossibleCells => combinations of values with that number of cells
        Map<Integer, List<List<Integer>>> nrPossibleCells2ValueCombinations = nrPossibleCells2Values
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    int nrPossibleCells = e.getKey();
                    List<Integer> values = e.getValue();
                    return Combinations.combinations(values, nrPossibleCells);
                }));
        //LOGGER.info("COMBOS = " + nrPossibleCells2ValueCombinations);

        //
        for (Map.Entry<Integer, List<List<Integer>>> e : nrPossibleCells2ValueCombinations.entrySet()) {
            int nrPossibleCells = e.getKey();
            List<List<Integer>> valueCombinations = e.getValue();

            for (List<Integer> valueCombo : valueCombinations) {
                Set<Integer> possibleCellsCombined = valueCombo.stream()
                        .map(possibleCellsPerValue::get)
                        .reduce((cells1, cells2) -> {
                            Set<Integer> union = new HashSet<>(cells1);
                            union.addAll(cells2);
                            return union;
                        })
                        .orElse(Set.of());
                if (possibleCellsCombined.size() == nrPossibleCells) {
                    for (int cell : possibleCellsCombined) {
                        Set<Integer> possibleValues = possibleValuesPerCell.get(cell);
                        possibleValues.retainAll(valueCombo);
                    }

                    // Special case: This can be solved
                    if (nrPossibleCells == 1) {
                        int index = possibleCellsCombined.iterator().next();
                        int value = possibleValuesPerCell.get(index).iterator().next();
                        return new Cell(index, value);
                    }
                }
            }
        }
        return null;
    }

    private Map<Integer, Set<Integer>> getPossibleCellsPerValue(Segment segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = new HashMap<>();

        for (Cell cell : segment.getCells()) {
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

    public String debugPossibleValues(SegmentType segmentType) {
        StringBuilder sb = new StringBuilder();
        for (int segmentIndex = 0; segmentIndex < Board.SIZE; segmentIndex++) {
            sb.append(debugPossibleValues(segmentType, segmentIndex));
        }
        return sb.toString();
    }

    public String debugPossibleValues(SegmentType segmentType, int segmentIndex) {
        StringBuilder sb = new StringBuilder();
        Segment segment = board.getSegment(segmentType, segmentIndex);

        for (Cell cell : segment.getCells()) {
            int index = cell.getIndex();
            Set<Integer> possibleValues = possibleValuesPerCell.get(index);
            sb.append(String.format("%s %d (%d, %d): %s%n", segmentType, segmentIndex,
                    cell.getRowIndex(), cell.getColumnIndex(), possibleValues));
        }
        sb.append("\n");

        return sb.toString();
    }
}
