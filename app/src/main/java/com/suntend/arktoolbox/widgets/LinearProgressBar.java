package com.suntend.arktoolbox.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class LinearProgressBar extends View {
    //进度条0-1
    private float progress = 0;
    private int[] colors;
    private Paint mPaint = new Paint();

    public LinearProgressBar(Context context) {
        this(context, null);
    }

    public LinearProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LinearProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public LinearProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (colors == null) {
            return;
        }
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(getHeight());
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setShader(new LinearGradient(0, getHeight() / 2, (getWidth()-getHeight()) * progress, getHeight() / 2, colors, null, Shader.TileMode.CLAMP));
        canvas.drawLine(getHeight(), getHeight() / 2, (getWidth()-getHeight()) * progress, getHeight() / 2, mPaint);
    }
}
