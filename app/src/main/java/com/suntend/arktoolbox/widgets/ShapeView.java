package com.suntend.arktoolbox.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;

import com.suntend.arktoolbox.R;


/**
 * Class:
 * Other:
 * Create by jsji on  2019/8/26.
 */
public class ShapeView extends androidx.appcompat.widget.AppCompatTextView {
    int solidColor, stroke_Color, touchColor, touchTextColor, normalTextColor, enableColor, enableTextColor;
    int cornesRadius, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, stroke_Width, strokeDashWidth, strokeDashGap, shapeType;
    int gradientStartColor, gradientCenterColor, gradientEndColor;
    GradientDrawable.Orientation gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
    GradientDrawable gradientDrawable = new GradientDrawable();
    private boolean drawableCenter = false;//是否居中显示

    public ShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }

    public ShapeView(Context context) {
        super(context);
    }

    private void initData(Context context, AttributeSet attrs) {
        setClickable(false);
        normalTextColor = getCurrentTextColor();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeView);
        touchTextColor = a.getColor(R.styleable.ShapeView_touchTextColor, getCurrentTextColor());
        solidColor = a.getColor(R.styleable.ShapeView_solidColor, Color.GRAY);
        stroke_Color = a.getColor(R.styleable.ShapeView_stroke_Color, Color.TRANSPARENT);
        touchColor = a.getColor(R.styleable.ShapeView_touchSolidColor, Color.TRANSPARENT);
        enableColor = a.getColor(R.styleable.ShapeView_enableColor, solidColor);
        drawableCenter = a.getBoolean(R.styleable.ShapeView_drawableCenter, false);
        gradientStartColor = a.getColor(R.styleable.ShapeView_gradient_startColor, -1);
        gradientCenterColor = a.getColor(R.styleable.ShapeView_gradient_centerColor, -1);
        gradientEndColor = a.getColor(R.styleable.ShapeView_gradient_endColor, -1);
        int gradientOri = a.getInt(R.styleable.ShapeView_gradient_Orientation, 0);
        enableTextColor = a.getColor(R.styleable.ShapeView_enableTextColor, getCurrentTextColor());
        cornesRadius = (int) a.getDimension(R.styleable.ShapeView_cornesRadius, 0);
        topLeftRadius = (int) a.getDimension(R.styleable.ShapeView_topLeftRadius, 0);
        topRightRadius = (int) a.getDimension(R.styleable.ShapeView_topRightRadius, 0);
        bottomLeftRadius = (int) a.getDimension(R.styleable.ShapeView_bottomLeftRadius, 0);
        bottomRightRadius = (int) a.getDimension(R.styleable.ShapeView_bottomRightRadius, 0);
        stroke_Width = (int) a.getDimension(R.styleable.ShapeView_stroke_Width, 0);
        strokeDashWidth = (int) a.getDimension(R.styleable.ShapeView_strokeDashWidth, 0);
        strokeDashGap = (int) a.getDimension(R.styleable.ShapeView_strokeDashGap, 0);
        shapeType = a.getInt(R.styleable.ShapeView_shapeType, -1);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth, strokeDashGap);

        if (touchColor != Color.TRANSPARENT) {
            setClickable(true);
        }
        if (gradientStartColor != -1 && gradientEndColor != -1) {
            //渐变色不为空，优先使用渐变色
            int[] colors;
            if (gradientCenterColor != -1) {
                colors = new int[]{gradientStartColor, gradientCenterColor, gradientEndColor};
            } else {
                colors = new int[]{gradientStartColor, gradientEndColor};
            }
            gradientDrawable.setColors(colors);
            gradientDrawable.setOrientation(GradientDrawable.Orientation.values()[gradientOri]);
        } else if (!isEnabled()) {
            gradientDrawable.setColor(enableColor);
            setTextColor(enableTextColor);
        } else {
            gradientDrawable.setColor(solidColor);
            setTextColor(normalTextColor);
        }
        if (shapeType != -1) {
            gradientDrawable.setShape(shapeType);
        }
        if (shapeType != GradientDrawable.OVAL) {
            if (cornesRadius != 0) {
                gradientDrawable.setCornerRadius(cornesRadius);
            } else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gradientDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
            }
        }
        setBackgroundDrawable(gradientDrawable);

        a.recycle();
    }

    /**
     * 设置绘制方式类型
     * 0：长方形
     * 1：椭圆
     * 2：线
     * 3：圆环
     */
    public ShapeView setShapeType(int shapeType) {
        gradientDrawable.setShape(shapeType);
        return this;
    }

    /**
     * 设置圆角
     *
     * @param radius
     */
    public ShapeView setRadius(float radius) {
        gradientDrawable.setCornerRadius(radius);
        return this;
    }

    public ShapeView setRadius(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        gradientDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
        return this;
    }

    /**
     * 设置颜色
     */
    public ShapeView setSolidColor(@ColorInt int solidColor) {
        gradientDrawable.setColor(solidColor);
        return this;
    }

    public ShapeView setColors(int... colors) {
        gradientDrawable.setColors(colors);
        return this;
    }

    public ShapeView setOrientation(GradientDrawable.Orientation orientation) {
        gradientDrawable.setOrientation(orientation);
        return this;
    }

    /**
     * 设置边线颜色
     */
    public ShapeView setStrokeColor(@ColorInt int strokeColor) {
        stroke_Color = strokeColor;
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth, strokeDashGap);
        return this;
    }

    public ShapeView setStrokeWidth(int strokeWidth) {
        this.stroke_Width = strokeWidth;
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth, strokeDashGap);
        return this;
    }

    public ShapeView setStrokeDash(int width, int gap) {
        strokeDashWidth = width;
        strokeDashGap = gap;
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth, strokeDashGap);
        return this;
    }


    /**
     * 进行绘制
     */
    public void show() {
        setBackgroundDrawable(gradientDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawableCenter) {
            Drawable[] drawables = getCompoundDrawables();
            if (null != drawables) {
                Drawable drawableLeft = drawables[0];
                Drawable drawableTop = drawables[1];
                Drawable drawableRight = drawables[2];
                Drawable drawableBottom = drawables[3];
                float textWidth = getPaint().measureText(getText().toString());
                if (null != drawableLeft) {
                    float contentWidth = textWidth + getCompoundDrawablePadding() + drawableLeft.getIntrinsicWidth();
                    if (getWidth() - contentWidth > 0) {
                        canvas.translate((getWidth() - contentWidth - getPaddingRight() - getPaddingLeft()) / 2, 0);
                    }
                }
                if (null != drawableTop) {
                    float contentheight = Math.abs(getPaint().getFontMetrics().top) + Math.abs(getPaint().getFontMetrics().bottom) + getCompoundDrawablePadding() + drawableTop.getIntrinsicHeight();
                    if (getHeight() - contentheight > 0) {
                        canvas.translate(0, (getHeight() - contentheight - getPaddingTop() - getPaddingBottom()) / 2);
                    }
                }
                if (null != drawableRight) {
                    float contentWidth = textWidth + getCompoundDrawablePadding() + drawableRight.getIntrinsicWidth();
                    if (getWidth() - contentWidth > 0) {
                        canvas.translate(-(getWidth() - contentWidth - getPaddingRight() - getPaddingLeft()) / 2, 0);
                    }
                }
                if (null != drawableBottom) {
                    float contentheight = Math.abs(getPaint().getFontMetrics().top) + Math.abs(getPaint().getFontMetrics().bottom) + getCompoundDrawablePadding() + drawableBottom.getIntrinsicHeight();
                    if (getHeight() - contentheight > 0) {
                        canvas.translate(0, -(getHeight() - contentheight - getPaddingTop() - getPaddingBottom()) / 2);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (gradientDrawable != null) {
            if (enabled) {
                gradientDrawable.setColor(solidColor);
                setTextColor(normalTextColor);
            } else {
                gradientDrawable.setColor(enableColor);
                setTextColor(enableTextColor);
            }
            setBackgroundDrawable(gradientDrawable);
        }

        super.setEnabled(enabled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (touchColor != Color.TRANSPARENT) {
                    gradientDrawable.setColor(touchColor);
                    setBackgroundDrawable(gradientDrawable);
                    setTextColor(touchTextColor);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (touchColor != Color.TRANSPARENT) {
                    gradientDrawable.setColor(solidColor);
                    setBackgroundDrawable(gradientDrawable);
                    setTextColor(normalTextColor);
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
