package io.stevenl.sudoku.core.grid;

public class TextGrid {
    private Grid grid;

    public TextGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Top border
        appendColumnLabels(sb);
        appendHorizontalSeparator(sb);

        // Main body
        for (int row = 0; row < Grid.SIZE; row++) {
            if (row % Grid.REGION_SIZE == 0 && row > 0) {
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
        for (int i = 0; i < Grid.SIZE; i++) {
            if (i % Grid.REGION_SIZE == 0 && i > 0) {
                sb.append("  ");
            }
            sb.append(String.format(" %d", i));
        }
        sb.append("\n");
    }

    private void appendRow(StringBuilder sb, int row) {
        sb.append(String.format("%d |", row));

        for (int col = 0; col < Grid.SIZE; col++) {
            // Square separator (vertical)
            if (col % Grid.REGION_SIZE == 0 && col > 0) {
                sb.append(" |");
            }

            int index = row * Grid.SIZE + col;
            Integer value = grid.getCell(index).getValue();

            String valStr = value != null
                    ? String.valueOf(value)
                    : " ";
            sb.append(String.format(" %s", valStr));
        }
        sb.append(" |\n");
    }

    private void appendHorizontalSeparator(StringBuilder sb) {
        sb.append("   ");
        for (int k = 0; k < Grid.SIZE; k++) {
            if (k % Grid.REGION_SIZE == 0 && k > 0) {
                sb.append("- ");
            }
            sb.append("--");
        }
        sb.append("-\n");
    }
}
