package com.suntend.arktoolbox.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.suntend.arktoolbox.R


class RadiusCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val tlRadiu: Float
    private val trRadiu: Float
    private val brRadiu: Float
    private val blRadiu: Float

    init {
        radius = 0f
        val array = context.obtainStyledAttributes(attrs, R.styleable.RadiusCardView)
        tlRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topLeftRadiu, 0f)
        trRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topRightRadiu, 0f)
        brRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomRightRadiu, 0f)
        blRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomLeftRadiu, 0f)
        setBackground(ColorDrawable())
    }

    override fun onDraw(canvas: Canvas) {
        val path = Path()
        val paint = Paint()
        paint.color = cardBackgroundColor.defaultColor
        val rectF = rectF
        val readius =
            floatArrayOf(tlRadiu, tlRadiu, trRadiu, trRadiu, brRadiu, brRadiu, blRadiu, blRadiu)
        path.addRoundRect(rectF, readius, Path.Direction.CW)
        canvas.drawPath(path, paint);
        super.onDraw(canvas)
    }

    private val rectF: RectF
        private get() {
            val rect = Rect()
            getDrawingRect(rect)
            return RectF(rect)
        }
}