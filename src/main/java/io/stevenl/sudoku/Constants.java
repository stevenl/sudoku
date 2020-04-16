package io.stevenl.sudoku;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final int SIZE = 9;
    public static final int NR_CELLS = SIZE * SIZE;
    public static final int SQUARE_SIZE = 3;

    public static final Set<Integer> POSSIBLE_CELL_VALUES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
    );

    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}