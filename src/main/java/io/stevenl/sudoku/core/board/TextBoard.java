package io.stevenl.sudoku.core.board;

public class TextBoard {
    private Board board;

    public TextBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Top border
        appendColumnLabels(sb);
        appendHorizontalSeparator(sb);

        // Main body
        for (int row = 0; row < Board.SIZE; row++) {
            if (row % Board.SQUARE_SIZE == 0 && row > 0) {
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
        for (int i = 0; i < Board.SIZE; i++) {
            if (i % Board.SQUARE_SIZE == 0 && i > 0) {
                sb.append("  ");
            }
            sb.append(String.format(" %d", i));
        }
        sb.append("\n");
    }

    private void appendRow(StringBuilder sb, int row) {
        sb.append(String.format("%d |", row));

        for (int col = 0; col < Board.SIZE; col++) {
            // Square separator (vertical)
            if (col % Board.SQUARE_SIZE == 0 && col > 0) {
                sb.append(" |");
            }

            int index = row * Board.SIZE + col;
            int value = board.getCell(index).getValue();

            String valStr = value > 0
                    ? String.valueOf(value)
                    : " ";
            sb.append(String.format(" %s", valStr));
        }
        sb.append(" |\n");
    }

    private void appendHorizontalSeparator(StringBuilder sb) {
        sb.append("   ");
        for (int k = 0; k < Board.SIZE; k++) {
            if (k % Board.SQUARE_SIZE == 0 && k > 0) {
                sb.append("- ");
            }
            sb.append("--");
        }
        sb.append("-\n");
    }
}
