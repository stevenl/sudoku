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
                    board.setCellValue(index, value);
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
        appendHorizontalBorder(sb);

        for (int i = 0; i < SIZE; i++) {
            sb.append("|");
            for (int j = 0; j < SIZE; j++) {
                int index = i * SIZE + j;
                int value = getCell(index).getValue();

                String valStr = value > 0
                        ? String.valueOf(value)
                        : " ";
                sb.append(String.format(" %s", valStr));

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
    public void printPossibleValues() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            Cell c = getCell(i);
            System.out.println(String.format("%d: %s", i, c.getPossibleValues()));
        }
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
