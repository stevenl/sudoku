package io.stevenl.sudoku;

public class Board {
    public static final int SIZE = 9;
    public static final int SQUARE_SIZE = 3;

    private Cell[] cells;
    private CellGroup[] rows;
    private CellGroup[] columns;
    private CellGroup[] squares;

    public Board() {
        cells = new Cell[SIZE * SIZE];
        rows = new CellGroup[SIZE];
        columns = new CellGroup[SIZE];
        squares = new CellGroup[SIZE];

        for (int i = 0; i < SIZE; i++) {
            rows[i] = new CellGroup(SIZE);
            columns[i] = new CellGroup(SIZE);
            squares[i] = new CellGroup(SIZE);
        }
    }

    public void addCell(int row, int col, int value) {
        int index = row * SIZE + col;
        Cell cell = new Cell(value);
        cells[index] = cell;

        rows[row].addCell(col, cell);
        columns[col].addCell(row, cell);
        //squares[]
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Top border
        appendHorizontalBorder(sb);

        for (int i = 0; i < SIZE; i++) {
            sb.append("|");
            for (int j = 0; j < SIZE; j++) {
                int index = i * SIZE + j;
                Cell cell = cells[index];

                String v = cell != null ? String.valueOf(cell.getValue()) : " ";
                sb.append(String.format(" %s", v));

                // Square separator (vertical)
                if (j % SQUARE_SIZE == 2 && j < SIZE - 1) {
                    sb.append(" |");
                }
            }
            sb.append(" |\n");

            // Square separator (horizontal)
            if (i % SQUARE_SIZE == 2 && i < SIZE - 1) {
                sb.append(" ");
                for (int k = 0; k < SIZE; k++) {
                    sb.append("--");
                    if (k % SQUARE_SIZE == 2 && k < SIZE - 1) {
                        sb.append("- ");
                    }
                }
                sb.append("-\n");
            }
        }

        // Bottom border
        appendHorizontalBorder(sb);

        return sb.toString();
    }

    private void appendHorizontalBorder(StringBuilder sb) {
        sb.append(" ");
        for (int k = 0; k < SIZE + 2; k++) {
            sb.append("--");
        }
        sb.append("-\n");
    }

    public static Board fromString(String input) {
        Board board = new Board();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                continue;
            }

            int row = i / SIZE;
            int col = i % SIZE;
            int cellValue = Integer.parseInt(String.valueOf(c));

            board.addCell(row, col, cellValue);
        }

        return board;
    }
}
