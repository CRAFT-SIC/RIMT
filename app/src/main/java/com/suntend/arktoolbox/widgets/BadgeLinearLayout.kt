package com.suntend.arktoolbox.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils

class BadgeLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    init {
        orientation=LinearLayout.HORIZONTAL
    }
    private var childWidth=ConvertUtils.dp2px(12f)
    fun setChildWidth( childWidth:Int){
        this.childWidth=childWidth
        invalidate()
    }

}