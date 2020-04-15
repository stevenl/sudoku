package io.stevenl.sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) throws SudokuException {
        Board board = new Board("000000080005073090000900300000200709900136004403009000001005000060840900070000000");

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
