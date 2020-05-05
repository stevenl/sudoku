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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PuzzleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PuzzleController.class);

    @GetMapping("/puzzles")
    public String getPuzzle(@RequestParam(name = "level") String level, Model model) throws SudokuException {
        Grid grid = getGrid(level);
        model.addAttribute("grid", grid);

        Solver solver = new Solver(grid);
        model.addAttribute("solver", solver);
        LOGGER.info("solvable = {}", solver.getSolvableCells());

        return "puzzle";
    }

    @PostMapping("/puzzles")
    public String updatePuzzle(@ModelAttribute Grid grid, Model model) {
        LOGGER.info("puzzle = {}", new TextGrid(grid));

        Solver solver = new Solver(grid);
        model.addAttribute("solver", solver);
        LOGGER.info("solvable = {}", solver.getSolvableCells());

        return "puzzle";
    }

    private Grid getGrid(String level) throws SudokuException {
        String puzzle;
        switch (level) {
            case "easy":
                puzzle = "000483276600102580020000100006007000130809047000600900008000060057201008469578000";
                break;
            case "medium":
                puzzle = "070001000005009003103074000608000030901000207020000908000950602400300500000700080";
                break;
            case "hard":
                puzzle = "000090008000000010413706002004900003090040050600008400800509741020000000500010000";
                break;
            case "evil":
                puzzle = "000000080005073090000900300000200709900136004403009000001005000060840900070000000";
                break;
            default:
                throw new SudokuException("Unrecognised level");
        }
        return new Grid(puzzle);
    }
}