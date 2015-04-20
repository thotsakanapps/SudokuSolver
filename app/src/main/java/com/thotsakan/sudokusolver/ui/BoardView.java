package com.thotsakan.sudokusolver.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public final class BoardView extends View {

    private static final int BOARD_SIZE = 9;

    private static final int BLOCK_SIZE = 3;

    private static final int INVALID_CELL = -1;

    private Board board;

    private Paint cellBorderPaint;

    private Paint cellEditableBackgroundPaint;

    private Paint cellReadOnlyBackgroundPaint;

    private Paint cellHighlightBackgroundPaint;

    private Paint numberPaint;

    private int touchedBoardRow = INVALID_CELL;

    private int touchedBoardCol = INVALID_CELL;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        cellBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellBorderPaint.setColor(Color.BLACK);
        cellBorderPaint.setStyle(Paint.Style.STROKE);

        cellEditableBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellEditableBackgroundPaint.setColor(Color.WHITE);
        cellEditableBackgroundPaint.setStyle(Paint.Style.FILL);

        cellReadOnlyBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellReadOnlyBackgroundPaint.setColor(Color.LTGRAY);
        cellReadOnlyBackgroundPaint.setStyle(Paint.Style.FILL);

        cellHighlightBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellHighlightBackgroundPaint.setColor(Color.MAGENTA);
        cellHighlightBackgroundPaint.setAlpha(75);
        cellHighlightBackgroundPaint.setStyle(Paint.Style.FILL);

        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setStyle(Paint.Style.STROKE);

        board = new Board(BOARD_SIZE, BLOCK_SIZE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dimension = MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)), MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int boardSize = board.getBoardSize();
        float cellLen = Math.min(getWidth(), getHeight()) / boardSize;

        // draw cells background
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                float x = i * cellLen;
                float y = j * cellLen;
                canvas.drawRect(x, y, x + cellLen, y + cellLen, board.isReadOnly(i, j) ? cellReadOnlyBackgroundPaint : cellEditableBackgroundPaint);
            }
        }

        // draw current cell background
        if (board.isCellValid(touchedBoardRow, touchedBoardCol)) {
            float x = touchedBoardRow * cellLen;
            float y = touchedBoardCol * cellLen;
            canvas.drawRect(x, y, x + cellLen, y + cellLen, cellHighlightBackgroundPaint);
        }

        // draw numbers
        numberPaint.setTextSize(cellLen / 2);
        float offsetX = (cellLen - numberPaint.measureText("8")) / 2;
        float offsetY = (cellLen - numberPaint.getTextSize()) / 2 - numberPaint.ascent();
        int[][] cells = board.getCells();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j] != 0) {
                    float x = cellLen * i + offsetX;
                    float y = cellLen * j + offsetY;
                    canvas.drawText(Integer.toString(cells[i][j]), x, y, numberPaint);
                }
            }
        }

        // draw cell borders
        float boardLen = cellLen * boardSize;
        cellBorderPaint.setStrokeWidth(1);
        for (int i = 0; i <= boardSize; i++) {
            float cellEnd = i * cellLen;
            canvas.drawLine(cellEnd, 0, cellEnd, boardLen, cellBorderPaint);
            canvas.drawLine(0, cellEnd, boardLen, cellEnd, cellBorderPaint);
        }

        // draw block borders
        int blockSize = board.getBlockSize();
        int numBlocks = boardSize / blockSize;
        float blockLen = cellLen * blockSize;
        cellBorderPaint.setStrokeWidth(5);
        for (int i = 0; i <= numBlocks; i++) {
            float blockEnd = i * blockLen;
            canvas.drawLine(blockEnd, 0, blockEnd, boardLen, cellBorderPaint); // vertical
            canvas.drawLine(0, blockEnd, boardLen, blockEnd, cellBorderPaint); // horizontal
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int boardSize = board.getBoardSize();
        float cellLen = Math.min(getWidth(), getHeight()) / boardSize;
        int row = (int) (event.getX() / cellLen);
        int col = (int) (event.getY() / cellLen);

        if (board.isCellValid(row, col) && !board.isReadOnly(row, col)) {
            touchedBoardRow = row;
            touchedBoardCol = col;
            invalidate();
        }

        return super.onTouchEvent(event);
    }

    public void setTouchedCellValue(int value) {
        // check if touched cell
        if (touchedBoardRow == INVALID_CELL || touchedBoardCol == INVALID_CELL) {
            return;
        }

        // try to set value of touched cell
        if (board.setCellValue(touchedBoardRow, touchedBoardCol, value)) {
            invalidate();
        } else {
            Toast toast = Toast.makeText(getContext(), "Invalid assignment", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void solve() {
        touchedBoardRow = INVALID_CELL;
        touchedBoardCol = INVALID_CELL;
        board.solveBoard();
        invalidate();
    }

    public void reset() {
        touchedBoardRow = INVALID_CELL;
        touchedBoardCol = INVALID_CELL;
        board.resetBoard();
        invalidate();
    }

    /**
     * State of the view - rotation handling
     */

    private static final String STATE_SUPER_STATE = "superstate";

    private static final String STATE_TOUCHED_BOARD_ROW = "touchedBoardRow";

    private static final String STATE_TOUCHED_BOARD_COL = "touchedBoardCol";

    private static final String STATE_BOARD_CELLS = "boardCells";

    private static final String STATE_BOARD_READ_ONLY = "boardReadOnly";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(STATE_TOUCHED_BOARD_ROW, touchedBoardRow);
        bundle.putInt(STATE_TOUCHED_BOARD_COL, touchedBoardCol);
        bundle.putIntArray(STATE_BOARD_CELLS, board.getBundleCells());
        bundle.putBooleanArray(STATE_BOARD_READ_ONLY, board.getBundleReadOnly());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            touchedBoardRow = bundle.getInt(STATE_TOUCHED_BOARD_ROW);
            touchedBoardCol = bundle.getInt(STATE_TOUCHED_BOARD_COL);
            board.bundleRestore(bundle.getIntArray(STATE_BOARD_CELLS), bundle.getBooleanArray(STATE_BOARD_READ_ONLY));
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
