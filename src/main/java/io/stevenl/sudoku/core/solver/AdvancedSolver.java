package io.stevenl.sudoku.core.solver;

import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.grid.Segment;
import io.stevenl.sudoku.core.grid.SegmentType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class AdvancedSolver {

    private AdvancedSolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    static void optimisePossibleValues(Solver solver, Grid grid) {
        for (SegmentType segmentType : SegmentType.SEGMENT_TYPES) {
            for (Segment segment : grid.getSegments(segmentType)) {
                optimisePossibleValues(solver, segment);
            }
        }
    }

    private static void optimisePossibleValues(Solver solver, Segment segment) {
        Map<Integer, Set<Integer>> possibleCellsPerValue = solver.getPossibleCellsPerValue(segment);

        // Find the value combinations that have number of possible cells matching the number of values in the
        // combination. This means we can rules out other possible values from those cells.
        // E.g. if values 4 and 9 are only possible in cells 55 and 74, and cell 55 has possible values 3, 4, 8, 9,
        // then we can rule out values 3 and 8 from cell 55.

        // Group the values by the number of possible cells,
        // i.e. nrPossibleCells => list of values with that number of possible cells
        Map<Integer, List<Integer>> nrPossibleCells2Values = possibleCellsPerValue
                .keySet()
                .stream()
                .collect(Collectors.groupingBy(v -> possibleCellsPerValue.get(v).size()));

        // nrPossibleCells => combinations of values with that number of cells
        Map<Integer, List<List<Integer>>> nrPossibleCells2ValueCombinations = nrPossibleCells2Values
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            int nrPossibleCells = e.getKey();
                            List<Integer> values = e.getValue();
                            return Combinations.combinations(values, nrPossibleCells);
                        }
                ));

        for (Map.Entry<Integer, List<List<Integer>>> e : nrPossibleCells2ValueCombinations.entrySet()) {
            int nrPossibleCells = e.getKey();
            List<List<Integer>> valueCombinations = e.getValue();

            for (List<Integer> valueCombo : valueCombinations) {
                Set<Integer> possibleCellsCombined = valueCombo
                        .stream()
                        .map(possibleCellsPerValue::get)
                        .reduce((cells1, cells2) -> {
                            Set<Integer> union = new HashSet<>(cells1);
                            union.addAll(cells2);
                            return union;
                        })
                        .orElse(Set.of());
                if (possibleCellsCombined.size() == nrPossibleCells) {
                    for (int cellIndex : possibleCellsCombined) {
                        Set<Integer> possibleValues = solver.getPossibleValues(cellIndex);
                        Set<Integer> toRemove = possibleValues.stream()
                                .filter(v -> !valueCombo.contains(v))
                                .collect(Collectors.toSet());
                        toRemove.forEach(v -> solver.removePossibleValue(cellIndex, v));
                    }
                }
            }
        }
    }

    private static class Combinations {

        private Combinations() {
            throw new UnsupportedOperationException("Utility class");
        }

        static List<List<Integer>> combinations(List<Integer> list, int r) {
            List<List<Integer>> combinations = new ArrayList<>();
            combinations(list, r, 0, new ArrayDeque<>(), combinations);
            return combinations;
        }

        private static void combinations(List<Integer> list, int r,
                                         int start, Deque<Integer> stack, List<List<Integer>> combinations) {
            for (int i = start; i < list.size(); i++) {
                stack.push(list.get(i));

                if (stack.size() == r) {
                    combinations.add(new ArrayList<>(stack));
                }

                combinations(list, r, i + 1, stack, combinations);
                stack.pop();
            }
        }
    }
}
