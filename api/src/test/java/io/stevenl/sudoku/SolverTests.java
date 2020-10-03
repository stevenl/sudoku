package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.solver.Solver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class SolverTests {

    static Stream<Arguments> puzzlesToSolve() {
        return Stream.of(
                arguments("000483276600102580020000100006007000130809047000600900008000060057201008469578000",
                        "591483276673192584824756139946317852135829647782645913218934765357261498469578321"), // easy
                arguments("070001000005009003103074000608000030901000207020000908000950602400300500000700080",
                        "276831495845629713193574826658297134931485267724163958317958642482316579569742381"), // medium
                arguments("000090008000000010413706002004900003090040050600008400800509741020000000500010000",
                        "265194378978235614413786592784952163392641857651378429836529741127463985549817236"), // hard
                arguments("000000080005073090000900300000200709900136004403009000001005000060840900070000000",
                        "739562481185473296246918375618254739927136854453789162891325647562847913374691528")  // evil
        );
    }

    @ParameterizedTest
    @MethodSource("puzzlesToSolve")
    public void testSolver(String input, String expected) throws SudokuException {
        Grid grid = new Grid(input);
        Solver solver = new Solver(grid);
        solver.solve();
        assertEquals(expected, grid.toString());
    }
}
