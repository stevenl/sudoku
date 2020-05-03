package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.grid.TextGrid;
import io.stevenl.sudoku.core.solver.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PuzzleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PuzzleController.class);

    @GetMapping("/puzzles")
    public String getPuzzle(Model model) throws SudokuException {
        Grid grid = new Grid("000000080005073090000900300000200709900136004403009000001005000060840900070000000");
        model.addAttribute("grid", grid);

        Solver solver = new Solver(grid);
        model.addAttribute("solvableCells", solver.getSolvableCells());
        model.addAttribute("possibleValuesPerCell", solver.getPossibleValuesPerCell());
        LOGGER.info("possibleValues = {}", solver.getPossibleValuesPerCell());
        LOGGER.info("solvable = {}", solver.getSolvableCells());

        return "puzzle";
    }

    @PostMapping("/puzzles")
    public String updatePuzzle(@ModelAttribute Grid grid, Model model) {
        LOGGER.info("puzzle = {}", new TextGrid(grid));

        Solver solver = new Solver(grid);
        model.addAttribute("solvableCells", solver.getSolvableCells());
        model.addAttribute("possibleValuesPerCell", solver.getPossibleValuesPerCell());
        LOGGER.info("possibleValues = {}", solver.getPossibleValuesPerCell());

        return "puzzle";
    }
}