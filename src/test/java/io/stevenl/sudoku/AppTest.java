package io.stevenl.sudoku;

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
                {"007000908030170004000006000698740300003010400001039762000400000900051040405000100", "167524938239178654854396271698742315723615489541839762312487596976251843485963127"}, // simple
                {"000090008000000010413706002004900003090040050600008400800509741020000000500010000", "265194378978235614413786592784952163392641857651378429836529741127463985549817236"} // hard
        };
        for (String[] test : tests) {
            Board board = new Board(test[0]);
            Solver solver = new Solver(board);
            solver.solve();
            assertEquals(board.toString(), test[1]);
        }
    }
}
