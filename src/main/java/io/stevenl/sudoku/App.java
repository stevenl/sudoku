package io.stevenl.sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) {
        TextBoard b = TextBoard.fromString(
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
        logger.log(Level.INFO, "\n{0}", b);

        Solver solver = new Solver(b);
        try {
            solver.solve();
        } catch (Exception e) {
            logger.info(e.toString());
            logger.log(Level.INFO, "\n{0}", solver.possibleValues());
        }
        logger.log(Level.INFO, "\n{0}", b);
    }
}
