package io.stevenl.sudoku;

import io.stevenl.sudoku.board.Board;
import io.stevenl.sudoku.solver.Solver;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    public void testBoardConstructionFromInput() throws SudokuException {
        String[][] tests = {
                {"  7   9 8 3 17   4     6   69874 3    3 1 4    1 39762   4     9   51 4 4 5   1  ", "007000908030170004000006000698740300003010400001039762000400000900051040405000100"},
                {"  7   9 8\n 3 17   4\n     6   \n69874 3  \n  3 1 4  \n  1 39762\n   4     \n9   51 4 \n4 5   1  ", "007000908030170004000006000698740300003010400001039762000400000900051040405000100"}
        };
        for (String[] test : tests) {
            Board board = new Board(test[0]);
            assertEquals(board.toString(), test[1]);
        }
    }

    public void testSolver() throws SudokuException {
        String[][] tests = {
                {"000483276600102580020000100006007000130809047000600900008000060057201008469578000", "591483276673192584824756139946317852135829647782645913218934765357261498469578321"}, // easy
                {"070001000005009003103074000608000030901000207020000908000950602400300500000700080", "276831495845629713193574826658297134931485267724163958317958642482316579569742381"}, // medium
                {"000090008000000010413706002004900003090040050600008400800509741020000000500010000", "265194378978235614413786592784952163392641857651378429836529741127463985549817236"} // hard
                //{"000000080005073090000900300000200709900136004403009000001005000060840900070000000", "739562481185473296246918375618254739927136854453789162891325647562847913374691528"} // evil
        };
        for (String[] test : tests) {
            Board board = new Board(test[0]);
            Solver solver = new Solver(board);
            solver.solve();
            assertEquals(board.toString(), test[1]);
        }
    }
}
