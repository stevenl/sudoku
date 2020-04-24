package io.stevenl.sudoku.solver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Combinations {

    private Combinations() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<List<Integer>> combinations(List<Integer> list, int r) {
        List<List<Integer>> combinations = new ArrayList<>();
        combinations(list, r, 0, new ArrayDeque<>(), combinations);
        return combinations;
    }

    private static void combinations(List<Integer> list, int r,
                                     int start, Deque<Integer> stack, List<List<Integer>> combinations) {
        for (int i = start; i < list.size(); i++) {
            stack.push(list.get(i));

            if (stack.size() == r) {
                combinations.add(new ArrayList<>(stack));
            }

            combinations(list, r, i + 1, stack, combinations);
            stack.pop();
        }
    }
}