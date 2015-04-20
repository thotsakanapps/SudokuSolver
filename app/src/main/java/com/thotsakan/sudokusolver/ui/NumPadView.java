package com.thotsakan.sudokusolver.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thotsakan.sudokusolver.MainActivity;
import com.thotsakan.sudokusolver.R;

public final class NumPadView extends LinearLayout {

    private Context context;

    private BoardView boardView;

    private int touchedValue;

    public NumPadView(Context context) {
        super(context);
        this.context = context;
    }

    public NumPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public NumPadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(final BoardView boardView) {

        // set the board view
        this.boardView = boardView;

        // define the on click listener for numeric buttons
        OnClickListener buttonClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.numpad_0:
                        touchedValue = 0;
                        break;
                    case R.id.numpad_1:
                        touchedValue = 1;
                        break;
                    case R.id.numpad_2:
                        touchedValue = 2;
                        break;
                    case R.id.numpad_3:
                        touchedValue = 3;
                        break;
                    case R.id.numpad_4:
                        touchedValue = 4;
                        break;
                    case R.id.numpad_5:
                        touchedValue = 5;
                        break;
                    case R.id.numpad_6:
                        touchedValue = 6;
                        break;
                    case R.id.numpad_7:
                        touchedValue = 7;
                        break;
                    case R.id.numpad_8:
                        touchedValue = 8;
                        break;
                    case R.id.numpad_9:
                        touchedValue = 9;
                        break;
                    default:
                        touchedValue = -1;
                }

                boardView.setTouchedCellValue(touchedValue);
            }
        };
        Resources resources = context.getResources();
        for (int i = 0; i <= 9; i++) {
            int resourceId = resources.getIdentifier("numpad_" + i, "id", MainActivity.class.getPackage().getName());
            Button button = (Button) findViewById(resourceId);
            button.setOnClickListener(buttonClickListener);
        }

        // on click listener for solve button
        Button solve = (Button) findViewById(R.id.solve);
        solve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.solve();
            }
        });

        // on click listener for reset button
        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.reset();
            }
        });
    }
}
