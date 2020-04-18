package io.stevenl.sudoku;

import io.stevenl.sudoku.board.Board;
import io.stevenl.sudoku.board.TextBoard;
import io.stevenl.sudoku.solver.Solver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) throws SudokuException {
        Board board = new Board("000000080005073090000900300000200709900136004403009000001005000060840900070000000"); // evil

        TextBoard textBoard = new TextBoard(board);
        logger.log(Level.INFO, "\n{0}", textBoard);
        logger.log(Level.INFO, "{0}", board);

        Solver solver = new Solver(board);
        try {
            solver.solve();
        } catch (Exception e) {

            logger.log(Level.WARNING, "\n{0}", solver.debugPossibleValues());
            logger.log(Level.WARNING, "{0}", e);
        }
        logger.log(Level.INFO, "\n{0}", textBoard);
        logger.log(Level.INFO, "{0}", board);
    }
}
