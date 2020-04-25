package io.stevenl.sudoku;

import io.stevenl.sudoku.core.SudokuException;
import io.stevenl.sudoku.core.board.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PuzzleController {

    @GetMapping("/puzzles/{id}")
    public String getPuzzle(@PathVariable(name="id", required=true) int id, Model model) throws SudokuException {
        Board board = new Board("000000080005073090000900300000200709900136004403009000001005000060840900070000000");
        model.addAttribute("board", board);
        model.addAttribute("name", "Steven");

        return "puzzle";
    }
}