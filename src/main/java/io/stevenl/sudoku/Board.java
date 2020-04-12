package io.stevenl.sudoku;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Board {
    public static final int SIZE = 9;
    public static final int SQUARE_SIZE = 3;

    private Cell[] cells;
    private CellGroup[] rows;
    private CellGroup[] columns;
    private CellGroup[] squares;

    private Set<Integer> unsolved;

    public Board() {
        cells = new Cell[SIZE * SIZE];
        rows = new CellGroup[SIZE];
        columns = new CellGroup[SIZE];
        squares = new CellGroup[SIZE];
        unsolved = new HashSet<>(SIZE * SIZE);

        for (int i = 0; i < SIZE; i++) {
            rows[i] = new CellGroup(SIZE);
            columns[i] = new CellGroup(SIZE);
            squares[i] = new CellGroup(SIZE);
        }

        for (int i = 0; i < SIZE * SIZE; i++) {
            addCell(i);
            unsolved.add(i);
        }
    }

    private void addCell(int index) {
        int rowIdx = index / SIZE;
        int colIdx = index % SIZE;
        int idxInSq = getCellIndexInSquare(index);

        CellGroup row = rows[rowIdx];
        CellGroup col = columns[colIdx];
        CellGroup square = getSquare(index);

        Cell cell = new Cell();
        cells[index] = cell;

        row.addCell(colIdx, cell);
        col.addCell(rowIdx, cell);
        square.addCell(idxInSq, cell);

        cell.addObservers(row, col, square);
    }

    public Cell getCell(int index) {
        return cells[index];
    }

    private int getSquareIndex(int index) {
        int row = index / SIZE;
        int col = index % SIZE;

        int sqRow = row / SQUARE_SIZE;
        int sqCol = col / SQUARE_SIZE;

        return sqRow * SQUARE_SIZE + sqCol;
    }

    public CellGroup getSquare(int index) {
        int sqIndex = getSquareIndex(index);
        return squares[sqIndex];
    }

    private int getCellIndexInSquare(int index) {
        int row = index / SIZE;
        int col = index % SIZE;

        int sqRow = row / SQUARE_SIZE;
        int sqCol = col / SQUARE_SIZE;

        int rowInSq = row - sqRow * SQUARE_SIZE;
        int colInSq = col - sqCol * SQUARE_SIZE;

        return rowInSq * SQUARE_SIZE + colInSq;
    }

    public void setCellValue(int index, int value) {
        Cell cell = getCell(index);
        cell.setValue(value);
        unsolved.remove(index);
    }

    public void solve() {
        while (true) {
            boolean progress = false;
            Iterator<Integer> iter = unsolved.iterator();
            while (iter.hasNext()) {
                int index = iter.next();
                Cell c = getCell(index);

                if (c.getNrPossibleValues() == 1) {
                    int value = (int) c.getPossibleValues().toArray()[0];
                    c.setValue(value);
                    iter.remove();
                    progress = true;
                }
            }

            if (!progress) {
                System.out.println("FAILED");
                break;
            }
        }
    }
}
