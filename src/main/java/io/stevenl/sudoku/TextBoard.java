package io.stevenl.sudoku;

public class TextBoard extends Board {

    public static TextBoard fromString(String input) {
        TextBoard board = new TextBoard();

        int index = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Linebreaks do not increment the index
            if (c == '\n') {
                continue;
            }

            // Ignore the character if it is not a number between 1 and 9
            if (Character.isDigit(c)) {
                int value = Integer.parseInt(String.valueOf(c));

                if (0 < value && value <= SIZE) {
                    board.getCell(index).setValue(value);
                }
            }
            index++;
        }

        return board;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Top border
        appendColumnLabels(sb);
        appendHorizontalSeparator(sb);

        // Main body
        for (int row = 0; row < SIZE; row++) {
            if (row % SQUARE_SIZE == 0 && row > 0) {
                appendHorizontalSeparator(sb);
            }
            appendRow(sb, row);
        }

        // Bottom border
        appendHorizontalSeparator(sb);

        return sb.toString();
    }

    private void appendColumnLabels(StringBuilder sb) {
        sb.append("   ");
        for (int i = 0; i < SIZE; i++) {
            if (i % SQUARE_SIZE == 0 && i > 0) {
                sb.append("  ");
            }
            sb.append(String.format(" %d", i));
        }
        sb.append("\n");
    }

    private void appendRow(StringBuilder sb, int row) {
        sb.append(String.format("%d |", row));

        for (int col = 0; col < SIZE; col++) {
            // Square separator (vertical)
            if (col % SQUARE_SIZE == 0 && col > 0) {
                sb.append(" |");
            }

            int index = row * SIZE + col;
            int value = getCell(index).getValue();

            String valStr = value > 0
                    ? String.valueOf(value)
                    : " ";
            sb.append(String.format(" %s", valStr));
        }
        sb.append(" |\n");
    }

    private void appendHorizontalSeparator(StringBuilder sb) {
        sb.append("   ");
        for (int k = 0; k < SIZE; k++) {
            if (k % SQUARE_SIZE == 0 && k > 0) {
                sb.append("- ");
            }
            sb.append("--");
        }
        sb.append("-\n");
    }
}
