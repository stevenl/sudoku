package io.stevenl.sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) throws SudokuException {
        Board board = new Board(
                  "  7   9 8"
                + " 3 17   4"
                + "     6   "
                + "69874 3  "
                + "  3 1 4  "
                + "  1 39762"
                + "   4     "
                + "9   51 4 "
                + "4 5   1  "
        );
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
