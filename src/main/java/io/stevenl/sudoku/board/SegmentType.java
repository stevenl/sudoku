package io.stevenl.sudoku.board;

import java.util.List;

public enum SegmentType {
    ROW,
    COLUMN,
    SQUARE;

    public static final List<SegmentType> SEGMENT_TYPES = List.of(ROW, COLUMN, SQUARE);
}
