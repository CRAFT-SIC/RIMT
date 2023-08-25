package com.suntend.arktoolbox.ui.arkofficial

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.suntend.arktoolbox.MainActivity
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/1.
 */
open class ArkOfficialFragment : Fragment() {
    private lateinit var mRv: RecyclerView

    private val adapter = ArkOfficialAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ark_official, container, false)
        val OpenNav = root.findViewById<ImageView>(R.id.openNav)

        //设置打开侧边栏按钮
        OpenNav.setOnClickListener { // 获取 MainActivity 的引用
            val mainActivity = activity as MainActivity?

            // 调用 MainActivity 的 openDrawer() 方法
            mainActivity?.openDrawer()
        }

        //查询本地存储的内容
        mRv = root.findViewById(R.id.rv)
        mRv.layoutManager = LinearLayoutManager(context)
        mRv.addItemDecoration(object : ItemDecoration() {
            val paint = Paint()
            val dividerColor = AttrUtil.getColor(R.attr.ark_label_divider_color)
            val paddingLeft = ConvertUtils.dp2px(18f)
            val paddingRight = ConvertUtils.dp2px(22f)

            init {
                paint.color = dividerColor
                paint.strokeWidth = 1f
                paint.isAntiAlias = true
            }

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    val position = parent.getChildAdapterPosition(child)
                    if (position != adapter!!.itemCount - 1) {
                        c.drawLine(
                            (child.left + paddingLeft).toFloat(),
                            child.bottom.toFloat(),
                            (child.right - paddingRight).toFloat(),
                            child.bottom.toFloat(),
                            paint
                        )
                    }
                }
            }

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (state.itemCount != adapter!!.itemCount) {
                    outRect.bottom = 1
                }
            }
        })
        mRv.adapter = adapter
        MainScope().launch {
            val loadData = ArkOfficialManager.loadData()
            adapter.setNewData(loadData)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
    }
}