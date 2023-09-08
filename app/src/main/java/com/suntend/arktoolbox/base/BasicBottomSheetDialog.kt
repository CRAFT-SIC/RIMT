package com.suntend.arktoolbox.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.StableState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX.statusBarOnly

open abstract class BasicBottomSheetDialog private constructor(context: Context, theme: Int) :
    BottomSheetDialog(context, theme) {
    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    constructor(context: Context) : this(context, R.style.BottomSheetDialog)

    init {
        if (context is BasicActivity) {
            setOwnerActivity(context)
        }
    }

    abstract val layoutId: Int
    abstract fun initView(contentView: View)
    fun setPeekHeight(peekHeight: Int) {
        sheetBehavior.peekHeight = peekHeight
    }

    fun setMaxHeight(maxHeight: Int) {
        sheetBehavior.maxHeight = maxHeight
    }

    fun setState(@StableState state: Int) {
        sheetBehavior.state = state
    }


//    override fun dismiss() {
//        super.dismiss()
//        (ownerActivity as BasicActivity).resetDefaultStatusBar()
//    }
//
//    override fun show() {
//        super.show()
//        statusBarOnly(ownerActivity as BasicActivity).apply {
//            this.color(AttrUtil.getColor(R.attr.ark_card_analyzer_dialog_dim_color))
//            this.fitWindow(false)
//            this.light(true)
//        }.apply()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root =
            LayoutInflater.from(context).inflate(R.layout.basic_bottom_sheet_dialog, null, false)
        val cardView = root.findViewById<CardView>(R.id.card_root)
        val contentView = LayoutInflater.from(context).inflate(layoutId, null, false)
        cardView.addView(contentView)
        setContentView(root)
        initView(contentView!!)
        sheetBehavior = BottomSheetBehavior.from(root.parent as View)
        sheetBehavior.setMaxHeight(
            context.resources.displayMetrics.heightPixels - ConvertUtils.dp2px(
                48f
            )
        )
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            context.resources.displayMetrics.heightPixels
        )
        window?.getDecorView()?.setPadding(0, 0, 0, 0)
    }
}