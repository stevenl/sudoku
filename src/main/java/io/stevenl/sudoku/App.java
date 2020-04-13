package io.stevenl.sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) throws SudokuException {
        //Board board = new Board("000080000095002030023000007060009000500476008000200090400000150030500760000010000");
        Board board = new Board("007000908030170004000006000698740300003010400001039762000400000900051040405000100");

        TextBoard textBoard = new TextBoard(board);
        logger.log(Level.INFO, "\n{0}", textBoard);
        logger.log(Level.INFO, "{0}", board);

        Solver solver = new Solver(board);
        try {
            solver.solve();
        } catch (Exception e) {
            logger.log(Level.WARNING, "{0}", e);
            logger.log(Level.INFO, "\n{0}", solver.debugPossibleValues());
        }
        logger.log(Level.INFO, "\n{0}", textBoard);
        logger.log(Level.INFO, "{0}", board);
    }
}
