package io.stevenl.sudoku;

public class App {
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
        System.out.println(b);

        b.solve();
        System.out.println(b);
        b.printPossibleValues();
    }
}
