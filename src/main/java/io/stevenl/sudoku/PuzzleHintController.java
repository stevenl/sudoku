package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.grid.Grid;
import io.stevenl.sudoku.core.solver.Solver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PuzzleHintController {

    @GetMapping("/puzzles/hints")
    public Solver getHint(@RequestParam(value = "values") String values) throws SudokuException {
        return new Solver(new Grid(values));
    }
}
