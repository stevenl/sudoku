package io.stevenl.sudoku.core.board;

import java.util.List;

public enum SegmentType {
    ROW,
    COLUMN,
    REGION;

    public static final List<SegmentType> SEGMENT_TYPES = List.of(ROW, COLUMN, REGION);
}
