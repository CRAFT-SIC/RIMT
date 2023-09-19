package com.suntend.arktoolbox.widgets.mainpage;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 自定义Drawable 实现圆形图片
 * Created by hust_twj on 2017/9/26.
 */

public class CircleDrawable extends Drawable {
    private final Paint mPaint;
    private final int mWidth; //宽/高，直径

    public CircleDrawable(Bitmap bitmap) {
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);//着色器 水平和竖直都需要填充满
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(bitmap.getWidth(), bitmap.getHeight()); //宽高的最小值作为直径
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle((float) mWidth /2, (float) mWidth /2, (float) mWidth /2, mPaint); //绘制圆形
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT; //设置系统默认，让drawable支持和窗口一样的透明度
    }

    //还需要从重写以下2个方法，返回drawable实际宽高
    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mWidth;
    }


}
