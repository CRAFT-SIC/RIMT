package com.suntend.arktoolbox.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils
import com.suntend.arktoolbox.enums.PlatformIconEnum
import com.suntend.arktoolbox.ui.arkofficial.ArkOfficialLinkEntity
import kotlin.math.floor

class OfficialInfoPlatformLinksLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var data = ArrayList<ArkOfficialLinkEntity>()
    private var childWidth = ConvertUtils.dp2px(36f)
    private var childPaddingVertical = ConvertUtils.dp2px(5f)
    private var childPaddingHorizontal = ConvertUtils.dp2px(12f)
    private var onChildClickListener: OnChildClickListener? = null

    private var centerChildCount = 0
    private var itemWidth = 0
    private var itemHeight = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val minHeight =
            (kotlin.math.ceil((data!!.size) / 5f) * (childWidth + childPaddingVertical * 2f)).toInt()
        itemWidth = widthSize / 5
        itemHeight = childWidth + childPaddingVertical * 2
        setMeasuredDimension(widthSize, minHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val leftIndex = i % 5
            val lines = floor((i / 5f).toDouble()).toInt()
            val childTop = lines * itemHeight + childPaddingVertical
            var childLeft = 0
            if (i < centerChildCount) {
                childLeft = itemWidth * leftIndex + itemWidth / 2 - childWidth / 2
            } else {
                val otherCount = childCount % 5
                childLeft = (width - otherCount * itemWidth) / 2 +itemWidth * leftIndex + itemWidth / 2 - childWidth / 2
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childWidth)
        }
    }

    fun setData(data: ArrayList<ArkOfficialLinkEntity>) {
        this.data = data
        centerChildCount = (kotlin.math.floor(data.size / 5f) * 5).toInt()
        removeAllViews()
        for (i in 0 until data.size) {
            val item = data[i]
            val mIv = createChildren(item)
            addView(mIv)
        }
        invalidate()
    }

    fun setOnChildClickListener(listener: OnChildClickListener) {
        this.onChildClickListener = listener
    }

    private fun createChildren(entity: ArkOfficialLinkEntity): ImageView {
        val iv = ImageView(context)
        iv.setImageResource(AttrUtil.getResId(entity.platform.iconAttr))
        iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
        val lp = MarginLayoutParams(childWidth, childWidth)
        iv.layoutParams = lp
        iv.setOnClickListener {
            onChildClickListener?.onClick(entity)
        }

        return iv
    }

    interface OnChildClickListener {
        fun onClick(entity: ArkOfficialLinkEntity)
    }
}