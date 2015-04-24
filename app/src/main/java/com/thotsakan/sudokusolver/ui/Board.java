package com.thotsakan.sudokusolver.ui;

import java.util.Random;

final class Board {

    private int[][] cells;

    private boolean[][] readOnly;

    private int boardSize;

    private int blockSize;

    private int EMPTY_CELL = 0;

    private static final int[] CELL_VALUES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

    private static void randomizeCellValues() {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 8; i > 0; i--) {
            int j = random.nextInt(i);
            int temp = CELL_VALUES[i];
            CELL_VALUES[i] = CELL_VALUES[j];
            CELL_VALUES[j] = temp;
        }
    }

    protected Board(int boardSize, int blockSize) {
        this.boardSize = boardSize;
        this.blockSize = blockSize;
        cells = new int[boardSize][boardSize];
        readOnly = new boolean[boardSize][boardSize];
    }

    private boolean solve() {
        int[] emptyCell = getEmptyCell();
        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];
        for (int value : CELL_VALUES) {
            if (isValidAssignment(row, col, value)) {
                cells[row][col] = value;
                if (solve()) {
                    return true;
                }
                cells[row][col] = EMPTY_CELL;
            }
        }
        return false;
    }

    private int[] getEmptyCell() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j] == EMPTY_CELL) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private boolean isValidAssignment(int row, int col, int value) {
        // validate row
        for (int i = 0; i < boardSize; i++) {
            if (cells[row][i] == value) {
                return false;
            }
        }
        // validate column
        for (int i = 0; i < boardSize; i++) {
            if (cells[i][col] == value) {
                return false;
            }
        }
        // validate block
        int blockRow = row - row % blockSize;
        int blockCol = col - col % blockSize;
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                int curRow = i + blockRow;
                int curCol = j + blockCol;
                if (cells[curRow][curCol] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCellValid(int row, int col) {
        return -1 < row && row < boardSize && -1 < col && col < boardSize;
    }

    private boolean isValueValid(int value) {
        return 0 <= value && value <= boardSize;
    }

    public boolean setCellValue(int row, int col, int value) {
        if (!isCellValid(row, col)) {
            return false;
        }
        if (!isValueValid(value)) {
            return false;
        }
        if (cells[row][col] == value) {
            return true;
        }
        if (value != EMPTY_CELL && !isValidAssignment(row, col, value)) {
            return false;
        }

        cells[row][col] = value;
        return true;
    }

    public boolean solveBoard() {
        // set cells read only
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j] > 0) {
                    readOnly[i][j] = true;
                }
            }
        }

        // solve the board
        return solve();
    }

    public void resetBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cells[i][j] = EMPTY_CELL;
                readOnly[i][j] = false;
            }
        }
        randomizeCellValues();
    }

    public int[] getBundleCells() {
        int[] bundleCells = new int[boardSize * boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(cells[i], 0, bundleCells, i * boardSize, boardSize);
        }
        return bundleCells;
    }

    public boolean[] getBundleReadOnly() {
        boolean[] bundleReadOnly = new boolean[boardSize * boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(readOnly[i], 0, bundleReadOnly, i * boardSize, boardSize);
        }
        return bundleReadOnly;
    }

    public void bundleRestore(int[] bundleCells, boolean[] bundleReadOnly) {
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(bundleCells, i * boardSize, cells[i], 0, boardSize);
            System.arraycopy(bundleReadOnly, i * boardSize, readOnly[i], 0, boardSize);
        }
    }

    public boolean isReadOnly(int x, int y) {
        return readOnly[x][y];
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int[][] getCells() {
        return cells;
    }
}
