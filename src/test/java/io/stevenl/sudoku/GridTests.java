package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class GridTests {

    static Stream<Arguments> puzzlesToConstruct() {
        return Stream.of(
                arguments("007000908030170004000006000698740300003010400001039762000400000900051040405000100",
                        "007000908030170004000006000698740300003010400001039762000400000900051040405000100"),
                arguments("  7   9 8 3 17   4     6   69874 3    3 1 4    1 39762   4     9   51 4 4 5   1  ",
                        "007000908030170004000006000698740300003010400001039762000400000900051040405000100"),
                arguments("  7   9 8\n 3 17   4\n     6   \n69874 3  \n  3 1 4  \n  1 39762\n   4     \n9   51 4 \n4 5   1  ",
                        "007000908030170004000006000698740300003010400001039762000400000900051040405000100")
        );
    }

    @ParameterizedTest
    @MethodSource("puzzlesToConstruct")
    public void testBoardConstructionFromInput(String input, String expected) throws SudokuException {
        Grid grid = new Grid(input);
        assertEquals(expected, grid.toString());
    }
}
