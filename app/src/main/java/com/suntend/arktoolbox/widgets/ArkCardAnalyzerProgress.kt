package com.suntend.arktoolbox.widgets

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils

class ArkCardAnalyzerProgress @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {
    private var progressBackground = -0x9090a //圆环背景
    private var colors = ArrayList<IntArray>() //从6星开始-至少4组颜色
    private var progress: ArrayList<Double>? = null //百分比,从6星开始
    private var animProgress = ArrayList<Double>() //百分比,从6星开始
    private var progressWidth = ConvertUtils.dp2px(12f) //进度条
    private var spacingWidth = ConvertUtils.dp2px(6f) //间距
    private val spacingScale = 0.05f //宽度缩放系数
    private val centerRadius = ConvertUtils.dp2px(40f)
    private var hintColor = -0x89898a //提示
    private var percentageColor = -0xfda901 //百分比文案颜色
    private var currentIndex = -1
    private var animCurrentIndex = 5
    private val paint = Paint()
    private val textPaint = TextPaint()
    private val circleRadius = ArrayList<Int>()
    private val objectAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        this.duration = 1000
        this.addUpdateListener {
            val value = it.animatedValue as Float
            for (i in animProgress.size - 1 downTo 0) {
                val data = animProgress.get(i)
                val ori = progress!!.get(i)
                if (ori == data) {
                    //一样的说明不用处理
                } else {
                    animCurrentIndex = i
                    animProgress.set(i, value * ori)
                    break
                }
            }

            invalidate()

        }
        this.doOnEnd {
            var hasNext = false
            for ((index, data) in animProgress.withIndex()) {
                val ori = progress!!.get(index)
                if (ori == data) {
                    //一样的说明不用处理
                } else {
                    hasNext = true
                }
            }
            if (hasNext) {
                startInitAnim()
            }
        }
    }

    fun startInitAnim() {
        objectAnimator.start()
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setProgressBackground(progressBackground: Int) {
        this.progressBackground = progressBackground
        invalidate()
    }

    fun setTextColor(percentageColor: Int, hintColor: Int) {
        this.percentageColor = percentageColor
        this.hintColor = hintColor
        invalidate()
    }

    fun setProgress(progress: ArrayList<Double>?, colors: ArrayList<IntArray>) {
        this.progress = progress
        animProgress.clear()
        this.progress!!.forEach {
            animProgress.add(0.0)
        }
        this.colors = colors
        currentIndex = -1
        animCurrentIndex = 5
        startInitAnim()
        invalidate()
    }

    fun setProgressWidth(progressWidth: Int, spacingWidth: Int) {
        this.spacingWidth = progressWidth
        this.progressWidth = spacingWidth
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (colors.size == 0) {
            return
        }
        val cw = width / 2
        val cy = height / 2
        paint.reset()
        paint.isAntiAlias = true
        //
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        var tempRadius = centerRadius
        circleRadius.clear()
        for (i in colors.indices) {
            paint.strokeWidth = progressWidth * (1 + spacingScale * i)
            paint.color = progressBackground
            paint.shader = null
            tempRadius += (progressWidth / 2 * (1 + spacingScale * i)).toInt()
            if (i > 0) {
                tempRadius += (spacingWidth * (1 + spacingScale * i * 2)).toInt()
            }
            canvas.drawCircle(cw.toFloat(), cy.toFloat(), tempRadius.toFloat(), paint)
            var positions: FloatArray? = null
            if (i == 0) {
                var startOffset = 1 - animProgress!![i].toFloat()
                if (startOffset > 0.8) {
                    startOffset -= 0.04f
                }
                val endOffset = 1f
                positions = FloatArray(colors[i].size)
                for (j in colors[i].indices) {
                    positions[j] =
                        startOffset + (endOffset - startOffset) / (colors[i].size - 1) * j
                }
            }
            val gradient = SweepGradient(cw.toFloat(), cy.toFloat(), colors[i], positions)
            if (i == 0) {
                val matrix = Matrix()
                matrix.postRotate(-80f, cw.toFloat(), cy.toFloat())
                gradient.setLocalMatrix(matrix)
            }
            paint.shader = gradient
            canvas.drawArc(
                (cw - tempRadius).toFloat(),
                (cy - tempRadius).toFloat(),
                (cw + tempRadius).toFloat(),
                (cy + tempRadius).toFloat(),
                270f,
                (-360 * animProgress!![i]).toFloat(),
                false,
                paint
            )
            tempRadius += (progressWidth / 2 * (1 + spacingScale * i)).toInt()
            circleRadius.add(tempRadius)
        }
        textPaint.reset()
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = ConvertUtils.sp2px(20f).toFloat()
        textPaint.color = percentageColor
        textPaint.typeface = Typeface.create("", Typeface.BOLD)
        val i = if (currentIndex == -1) animCurrentIndex else currentIndex
        val pro =
            if (animProgress!![i] * 3 > progress!![i] || currentIndex != -1) progress!![i] else animProgress!![i]
        val baifenbiStr = (pro * 100).toInt().toString() + "%"
        canvas.drawText(
            baifenbiStr,
            cw.toFloat(),
            (cy - ConvertUtils.dp2px(3f)).toFloat(),
            textPaint
        )
        textPaint.textSize = ConvertUtils.sp2px(12f).toFloat()
        textPaint.color = hintColor
        textPaint.typeface = Typeface.DEFAULT
        canvas.drawText(
            (6 - if (currentIndex == -1) animCurrentIndex else currentIndex).toString() + "★概率",
            cw.toFloat(),
            (cy + ConvertUtils.dp2px(12f)).toFloat(),
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        for (i in circleRadius.indices) {
            val radius = circleRadius[i]
            val distanceX = event.x - width / 2
            val distanceY = event.y - height / 2
            val isInside = distanceX * distanceX + distanceY * distanceY <= radius * radius
            if (isInside) {
                if (currentIndex != i) {
                    currentIndex = i
                    invalidate()
                }
                return true
            }
        }
        return true
    }
}