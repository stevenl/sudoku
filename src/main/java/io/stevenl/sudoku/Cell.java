package io.stevenl.sudoku;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Cell {
    private static final Set<Integer> ALL_POSSIBLE_VALUES =
            new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private Set<CellGroup> observers = new HashSet<>();
    private Set<Integer> possibleValues = new HashSet<>(ALL_POSSIBLE_VALUES);
    private int value;

    public Cell() {
        super();
    }

    public void addObservers(CellGroup... observers) {
        this.observers.addAll(Arrays.asList(observers));
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

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
