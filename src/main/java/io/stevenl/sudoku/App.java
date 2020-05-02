package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.grid.SegmentType;
import io.stevenl.sudoku.core.grid.TextGrid;
import io.stevenl.sudoku.core.solver.Solver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) throws SudokuException {
        Grid grid = new Grid("000000080005073090000900300000200709900136004403009000001005000060840900070000000"); // evil

        TextGrid textGrid = new TextGrid(grid);
        logger.log(Level.INFO, "Start\n{0}", textGrid);
        logger.log(Level.INFO, "{0}", grid);

        Solver solver = new Solver(grid);
        try {
            solver.solve();
        } catch (Exception e) {
            logger.log(Level.WARNING, "\n{0}", solver.debugPossibleValues(SegmentType.COLUMN, 0));
            logger.log(Level.WARNING, "\n{0}", solver.debugPossibleValues(SegmentType.REGION, 6));
            logger.log(Level.WARNING, e.toString());
        }
        logger.log(Level.INFO, "Solved\n{0}", textGrid);
        logger.log(Level.INFO, "{0}", grid);
    }
}
