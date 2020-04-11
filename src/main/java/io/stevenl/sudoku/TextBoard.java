package io.stevenl.sudoku;

public class TextBoard extends Board {

    public static TextBoard fromString(String input) throws Exception {
        TextBoard board = new TextBoard();

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Top border
        appendHorizontalBorder(sb);

        for (int i = 0; i < SIZE; i++) {
            sb.append("|");
            for (int j = 0; j < SIZE; j++) {
                int index = i * SIZE + j;
                Cell cell = getCell(index);

                String value = cell != null
                        ? String.valueOf(cell.getValue())
                        : " ";
                sb.append(String.format(" %s", value));

                // Square separator (vertical)
                if (j % SQUARE_SIZE == 2 && j < SIZE - 1) {
                    sb.append(" |");
                }
            }
            sb.append(" |\n");

            // Square separator (horizontal)
            if (i % SQUARE_SIZE == 2 && i < SIZE - 1) {
                appendHorizontalSeparator(sb);
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

    private void appendHorizontalSeparator(StringBuilder sb) {
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
