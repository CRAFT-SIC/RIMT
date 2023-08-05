package com.suntend.arktoolbox.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suntend.arktoolbox.RIMTUtil.ConvertUtils;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ArkLabelAddShadowFrameLayout extends FrameLayout {
    private int backgroundShadowColor = -1;
    private Paint paint = new Paint();
    private int shadowPadding = ConvertUtils.dp2px(10);

    public ArkLabelAddShadowFrameLayout(@NonNull Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public ArkLabelAddShadowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public ArkLabelAddShadowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public ArkLabelAddShadowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (backgroundShadowColor == -1) {
            backgroundShadowColor = ((ColorDrawable) getBackground()).getColor();
            setBackgroundColor(Color.TRANSPARENT);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(20);
            paint.setColor(backgroundShadowColor);
            paint.setShadowLayer(shadowPadding, 0, 0, backgroundShadowColor);
            setPadding(shadowPadding,shadowPadding,shadowPadding,shadowPadding);
        }
        canvas.drawCircle(getWidth()/2,getHeight()/2, getWidth()/2-shadowPadding,  paint);

    }
}
