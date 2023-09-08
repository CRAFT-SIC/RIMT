package com.suntend.arktoolbox.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly

open class BasicActivity : AppCompatActivity() {
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        val content = window.decorView.findViewById<FrameLayout>(android.R.id.content).getChildAt(0)
        if (content is LinearLayout) {
            val statusView = View(this)
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                val result = getResources().getDimensionPixelSize(resourceId);
                statusView.layoutParams =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, result)
               statusView.background= content.getChildAt(0).background
                content.addView(statusView, 0)

            }
        }
        resetDefaultStatusBar()
    }
    fun resetDefaultStatusBar(){
        statusBarOnly {
            color = Color.TRANSPARENT
            fitWindow = false
            light = true
        }
    }
}