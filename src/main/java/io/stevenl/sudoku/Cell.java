package io.stevenl.sudoku;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Cell {
    private static final int[] VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    private Set<Integer> possibleValues = new HashSet<>();
    private int value;

    public Cell(int value) {
        this.value = value;

        for (int v : VALUES) {
            possibleValues.add(v);
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void removePossibleValue(int value) {
        possibleValues.remove(value);
    }

    public Set<Integer> getPossibleValues() {
        return Collections.unmodifiableSet(possibleValues);
    }

    public int getNrPossibleValues() {
        return possibleValues.size();
    }
}
