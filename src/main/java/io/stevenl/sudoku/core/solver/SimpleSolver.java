package io.stevenl.sudoku.core.solver;

import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.grid.Segment;
import io.stevenl.sudoku.core.grid.SegmentType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class SimpleSolver {

    private SimpleSolver() {
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

        // Find where a value is possible only in one cell within a segment (e.g. row, column or region)
        for (Map.Entry<Integer, Set<Integer>> e : possibleCellsPerValue.entrySet()) {
            int value = e.getKey();
            Set<Integer> possibleCells = e.getValue();

            if (possibleCells.size() == 1) {
                int index = possibleCells.iterator().next();
                Set<Integer> possibleValues = solver.getPossibleValues(index);
                Set<Integer> toRemove = possibleValues.stream()
                        .filter(v -> v != value)
                        .collect(Collectors.toSet());
                toRemove.forEach(v -> solver.removePossibleValue(index, v));
            }
        }
    }
}
