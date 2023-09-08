package com.suntend.arktoolbox.ui.arkcardanalyzer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.suntend.arktoolbox.base.BasicBottomSheetDialog
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils
import com.suntend.arktoolbox.enums.ArkCardPoolTypeEnum
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections


class ArkCardAnalyzerPoolInfoDialog(context: Context) : BasicBottomSheetDialog(context) {
    private var onSelectPoolListener: OnSelectPoolListener? = null
    private lateinit var mRv: RecyclerView
    private var list = ArrayList<Any>()
    private var originList = ArrayList<ArkGanchaDirEntity>()
    private var allRollDayArray =
        ArrayList<ArkGanchaDayEntity>()
    private var selectIndex = 0
    private lateinit var leftLinear: LinearLayout
    private lateinit var rightLinear: LinearLayout

    private class DaySortItemDecoration(val item: ArkGanchaDayEntity) :
        RecyclerView.ItemDecoration() {
        val paint = Paint()
        val tenLinePaint = Paint()
        val dividerColor = 0xFFE8E8E8.toInt()
        val tenLineColor = 0xFF6781FF.toInt()
        val paddingLeft = ConvertUtils.dp2px(28.5f)
        val roundRadius = ConvertUtils.dp2px(3f).toFloat()

        init {
            paint.color = dividerColor
            paint.strokeWidth = 1f
            paint.isAntiAlias = true
            tenLinePaint.color = tenLineColor
            tenLinePaint.strokeWidth = ConvertUtils.dp2px(3f).toFloat()
            tenLinePaint.strokeCap = Paint.Cap.ROUND
            tenLinePaint.isAntiAlias = true
        }

        override fun onDrawOver(
            c: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.onDrawOver(c, parent, state)
            try {
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    val position = parent.getChildAdapterPosition(child)
                    if (position == 0) {
                        //第一个
                        if (item.list.size > 1) {
                            c.drawLine(
                                (child.left + paddingLeft).toFloat(),
                                child.top - (child.top - child.bottom) / 2.toFloat(),
                                (child.left + paddingLeft).toFloat(),
                                child.bottom.toFloat(),
                                paint
                            )
                        }
                    } else if (position == item.list.size - 1) {
                        //最后一个
                        c.drawLine(
                            (child.left + paddingLeft).toFloat(),
                            child.top.toFloat(),
                            (child.left + paddingLeft).toFloat(),
                            child.bottom + (child.top - child.bottom) / 2.toFloat(),
                            paint
                        )
                    } else {
                        //居中的
                        c.drawLine(
                            (child.left + paddingLeft).toFloat(),
                            child.top.toFloat(),
                            (child.left + paddingLeft).toFloat(),
                            child.bottom.toFloat(),
                            paint
                        )
                    }
                    val lostTs =
                        if (position - 1 >= 0) item.list.get(
                            position - 1
                        ).ts else 0
                    val curTs = item.list.get(position).ts
                    val nextTs =
                        if (item.list.size > position + 1) item.list.get(
                            position + 1
                        ).ts else 0
                    var startY =
                        child.bottom + (child.top - child.bottom) / 2.toFloat()
                    var endY =
                        child.bottom + (child.top - child.bottom) / 2.toFloat()
                    if (curTs == lostTs && curTs == nextTs) {
                        startY = child.top.toFloat()
                        endY = child.bottom.toFloat()
                    } else if (curTs == lostTs) {
                        startY = child.top.toFloat()
                        endY =
                            child.bottom + (child.top - child.bottom) / 2.toFloat()
                    } else if (curTs == nextTs) {
                        startY =
                            child.bottom + (child.top - child.bottom) / 2.toFloat()
                        endY = child.bottom.toFloat()
                    }

                    c.drawLine(
                        (child.left + paddingLeft).toFloat(),
                        startY,
                        (child.left + paddingLeft).toFloat(),
                        endY,
                        tenLinePaint
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    override val layoutId: Int
        get() = R.layout.dialog_ark_card_analyzer_pool_info

    override fun initView(contentView: View) {
        (contentView.parent as View).viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                (contentView.parent as View).viewTreeObserver.removeOnGlobalLayoutListener(this)

                val h: Int = (contentView.parent as View).getMeasuredHeight()
                (contentView.parent as View).layoutParams =
                    (contentView.parent as View).layoutParams.apply {
                        height = h
                    }
            }

        })
        mRv = contentView.findViewById(R.id.rv)
        mRv.layoutManager = LinearLayoutManager(context)
        leftLinear = contentView.findViewById<LinearLayout?>(R.id.linear_left).apply {
            this.setOnClickListener {
                if (selectIndex != 0) {
                    selectIndex = 0
                    updateSelectUI()
                }
            }
        }
        rightLinear = contentView.findViewById<LinearLayout?>(R.id.linear_right).apply {
            this.setOnClickListener {
                if (selectIndex != 1) {
                    selectIndex = 1
                    updateSelectUI()
                }
            }
        }
        updateSelectUI()
    }

    private fun updateSelectUI() {
        (leftLinear.getChildAt(0) as TextView).typeface =
            if (selectIndex == 0) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        (leftLinear.getChildAt(0) as TextView).setTextColor(AttrUtil.getColor(if (selectIndex == 0) R.attr.ark_label_general_text_color else R.attr.ark_label_tip_text_color))
        leftLinear.getChildAt(1)?.visibility =
            if (selectIndex == 0) View.VISIBLE else View.INVISIBLE

        (rightLinear.getChildAt(0) as TextView).typeface =
            if (selectIndex == 1) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        (rightLinear.getChildAt(0) as TextView).setTextColor(AttrUtil.getColor(if (selectIndex == 1) R.attr.ark_label_general_text_color else R.attr.ark_label_tip_text_color))
        rightLinear.getChildAt(1)?.visibility =
            if (selectIndex == 1) View.VISIBLE else View.INVISIBLE
        if (selectIndex == 0) {
            mRv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    return DirVH(
                        LayoutInflater.from(context)
                            .inflate(
                                R.layout.item_card_analyzer_pool_info_roll_group,
                                parent,
                                false
                            )
                    )
                }

                override fun getItemCount(): Int {
                    return originList.size
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    originList.get(position).let { item ->
                        val vh = holder as DirVH
                        vh.cardMore.setOnClickListener {
                            onSelectPoolListener?.onSelect(item.poolEntity)
                            dismiss()
                        }
                        vh.mTvPoolName.text =
                            item.poolEntity.pool + " (" + item.poolEntity.rollList.size + ")"
                        if (ArkCardPoolTypeEnum.isNormalPool(item.poolEntity.poolType)) {
                            vh.mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_normal)
                        } else if (ArkCardPoolTypeEnum.isClassicPool(item.poolEntity.poolType)) {
                            vh.mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_classic)
                        } else {
                            vh.mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_limited)
                        }
                        if (item.isExpand) {
                            vh.mIvExpand.setImageResource(R.mipmap.icon_ark_pool_arrow_bottom)
                        } else {
                            vh.mIvExpand.setImageResource(R.mipmap.icon_ark_pool_arrow_top)
                        }

                        if (item.isExpand) {
                            vh.rvItem.visibility = View.VISIBLE
//                            if (originList.size > 1) {
//                                if (item.poolEntity.rollList.size > 15) {
//                                    //限制高度
//                                    vh.rvItem.layoutParams = vh.rvItem.layoutParams.apply {
//                                        height = ConvertUtils.dp2px(275f)
//                                    }
//                                } else {
//                                    vh.rvItem.layoutParams = vh.rvItem.layoutParams.apply {
//                                        height = ViewGroup.LayoutParams.WRAP_CONTENT
//                                    }
//                                }
//                            }
                            vh.rvItem.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            vh.rvItem.adapter =
                                object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                    override fun onCreateViewHolder(
                                        parent: ViewGroup,
                                        viewType: Int
                                    ): RecyclerView.ViewHolder {
                                        return ChildVH(
                                            LayoutInflater.from(context)
                                                .inflate(
                                                    R.layout.item_card_analyzer_pool_info_roll_group_item,
                                                    parent,
                                                    false
                                                )
                                        )
                                    }

                                    override fun getItemCount(): Int {
                                        return item.poolEntity.rollList.size
                                    }

                                    override fun onBindViewHolder(
                                        itemVH: RecyclerView.ViewHolder,
                                        itemPosition: Int
                                    ) {
                                        item.poolEntity.rollList.get(itemPosition)
                                            .let { childItem ->
                                                val childVH = itemVH as ChildVH
                                                childVH.mTvPosition.text =
                                                    "[${item.poolEntity.rollList.size - itemPosition}]"
                                                childVH.mTvPoolName.text = childItem.name
                                                childVH.mTvPosition.setTextColor(if (childItem.level == 6)Color.parseColor( "#FFFF6658") else if (childItem.level == 5)Color.parseColor(  "#FFE7B735") else AttrUtil.getColor(R.attr.ark_label_tip_text_color))
                                                childVH.mTvPoolName.setTextColor(if (childItem.level == 6)Color.parseColor( "#FFFF6658") else if (childItem.level == 5)Color.parseColor(  "#FFE7B735")else if (childItem.level == 4)Color.parseColor(  "#FF8F55B6") else AttrUtil.getColor(R.attr.ark_label_tip_text_color))
                                                childVH.mTvPosition.typeface =
                                                    if (childItem.level >= 5) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                                childVH.mTvPoolName.typeface =
                                                    if (childItem.level >= 5) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                                val calendar = Calendar.getInstance()
                                                calendar.timeInMillis = childItem.ts * 1000L
                                                val startTime =
                                                    SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(
                                                        calendar.time
                                                    )
                                                childVH.mTvTime.text = startTime
                                                childVH.mViewNew.visibility =
                                                    if (childItem.isNew) View.VISIBLE else View.INVISIBLE
                                            }
                                    }

                                    inner class ChildVH(itemView: View) :
                                        RecyclerView.ViewHolder(itemView) {
                                        var mTvPoolName: TextView
                                        var mTvPosition: TextView
                                        var mTvTime: TextView
                                        var mViewNew: View

                                        init {
                                            mTvPoolName = itemView.findViewById(R.id.tv_pool_name)
                                            mTvPosition = itemView.findViewById(R.id.tv_position)
                                            mTvTime = itemView.findViewById(R.id.tv_create_time)
                                            mViewNew = itemView.findViewById(R.id.view_new)
                                        }

                                    }

                                }
                            val itemDecoration = object : RecyclerView.ItemDecoration() {
                                val paint = Paint()
                                val tenLinePaint = Paint()
                                val dividerColor = 0xFFE8E8E8.toInt()
                                val tenLineColor = 0xFF6781FF.toInt()
                                val paddingLeft = ConvertUtils.dp2px(28.5f)
                                val roundRadius = ConvertUtils.dp2px(3f).toFloat()
                                val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

                                init {
                                    paint.color = dividerColor
                                    paint.strokeWidth = 1f
                                    paint.isAntiAlias = true
                                    tenLinePaint.color = tenLineColor
                                    tenLinePaint.strokeWidth = ConvertUtils.dp2px(3f).toFloat()
                                    tenLinePaint.strokeCap = Paint.Cap.ROUND
                                    tenLinePaint.isAntiAlias = true
                                    adapter =
                                        vh.rvItem.adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
                                }

                                override fun onDrawOver(
                                    c: Canvas,
                                    parent: RecyclerView,
                                    state: RecyclerView.State
                                ) {
                                    super.onDrawOver(c, parent, state)
                                    try {
                                        for (i in 0 until parent.childCount) {
                                            val child = parent.getChildAt(i)
                                            val position = parent.getChildAdapterPosition(child)
                                            if (position == 0) {
                                                //第一个
                                                if (item.poolEntity.rollList.size > 1) {
                                                    c.drawLine(
                                                        (child.left + paddingLeft).toFloat(),
                                                        child.top - (child.top - child.bottom) / 2.toFloat(),
                                                        (child.left + paddingLeft).toFloat(),
                                                        child.bottom.toFloat(),
                                                        paint
                                                    )
                                                }
                                            } else if (position == adapter.itemCount - 1) {
                                                //最后一个
                                                c.drawLine(
                                                    (child.left + paddingLeft).toFloat(),
                                                    child.top.toFloat(),
                                                    (child.left + paddingLeft).toFloat(),
                                                    child.bottom + (child.top - child.bottom) / 2.toFloat(),
                                                    paint
                                                )
                                            } else {
                                                //居中的
                                                c.drawLine(
                                                    (child.left + paddingLeft).toFloat(),
                                                    child.top.toFloat(),
                                                    (child.left + paddingLeft).toFloat(),
                                                    child.bottom.toFloat(),
                                                    paint
                                                )
                                            }
                                            val lostTs =
                                                if (position - 1 >= 0) item.poolEntity.rollList.get(
                                                    position - 1
                                                ).ts else 0
                                            val curTs = item.poolEntity.rollList.get(position).ts
                                            val nextTs =
                                                if (item.poolEntity.rollList.size > position + 1) item.poolEntity.rollList.get(
                                                    position + 1
                                                ).ts else 0
                                            var startY =
                                                child.bottom + (child.top - child.bottom) / 2.toFloat()
                                            var endY =
                                                child.bottom + (child.top - child.bottom) / 2.toFloat()
                                            if (curTs == lostTs && curTs == nextTs) {
                                                startY = child.top.toFloat()
                                                endY = child.bottom.toFloat()
                                            } else if (curTs == lostTs) {
                                                startY = child.top.toFloat()
                                                endY =
                                                    child.bottom + (child.top - child.bottom) / 2.toFloat()
                                            } else if (curTs == nextTs) {
                                                startY =
                                                    child.bottom + (child.top - child.bottom) / 2.toFloat()
                                                endY = child.bottom.toFloat()
                                            }

                                            c.drawLine(
                                                (child.left + paddingLeft).toFloat(),
                                                startY,
                                                (child.left + paddingLeft).toFloat(),
                                                endY,
                                                tenLinePaint
                                            )
                                        }
                                    } catch (e: Exception) {
                                    }

                                }


                            }
                            if (vh.rvItem.tag != null) {
                                vh.rvItem.removeItemDecoration(vh.rvItem.tag as RecyclerView.ItemDecoration)
                            }
                            vh.rvItem.tag = itemDecoration
                            vh.rvItem.addItemDecoration(itemDecoration)
                        } else {
                            vh.rvItem.adapter = null
                            vh.rvItem.visibility = View.GONE
                        }
                        vh.itemView.setOnClickListener {
                            item.isExpand = !item.isExpand
                            notifyDataSetChanged()
                        }
                    }
                }

                inner class DirVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
                    var mTvPoolName: TextView
                    var mIvExpand: ImageView
                    var mIvPoolType: ImageView

                    var cardMore: View
                    var rvItem: RecyclerView

                    init {
                        mTvPoolName = itemView.findViewById(R.id.tv_pool_name)
                        mIvExpand = itemView.findViewById(R.id.iv_expand)
                        mIvPoolType = itemView.findViewById(R.id.iv_pool_type)
                        cardMore = itemView.findViewById(R.id.card_more)
                        rvItem = itemView.findViewById(R.id.rv_item)
                    }

                }
            }
            val itemDecoration = object : ItemDecoration() {
                var stackView: View? = null
                var paint = Paint().apply {
                    isAntiAlias = true
                    strokeWidth = ConvertUtils.dp2px(0.5f).toFloat()
                    color = AttrUtil.getColor(R.attr.ark_label_tip_background_color)
                }

                override fun onDrawOver(
                    c: Canvas,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.onDrawOver(c, parent, state)
                    for (i in 0 until parent.childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        if (position < originList.size - 1) {
                            //画分割线
                            c.drawLine(
                                ConvertUtils.dp2px(24f).toFloat(), child.bottom.toFloat(),
                                (child.right - ConvertUtils.dp2px(24f)).toFloat(),
                                child.bottom.toFloat(), paint
                            )
                        }
                    }
                    val childAt = parent.getChildAt(0)
                    val position = parent.getChildAdapterPosition(childAt)
                    if (position >= originList.size) {
                        return
                    }
                    val item = originList[position]
                    if (stackView == null) {
                        stackView = LayoutInflater.from(context)
                            .inflate(
                                R.layout.item_card_analyzer_pool_info_roll_group,
                                null,
                                false
                            )
                        val widthSpec =
                            View.MeasureSpec.makeMeasureSpec(
                                context.resources.displayMetrics.widthPixels,
                                View.MeasureSpec.EXACTLY
                            )
                        val heightSpec = View.MeasureSpec.makeMeasureSpec(
                            ConvertUtils.dp2px(48f),
                            View.MeasureSpec.EXACTLY
                        )
                        stackView!!.setBackgroundColor(Color.WHITE)
                        stackView!!.measure(widthSpec, heightSpec)
                        stackView!!.layout(
                            0,
                            0,
                            context.resources.displayMetrics.widthPixels,
                            ConvertUtils.dp2px(48f)
                        )

                    }
                    stackView!!.setOnClickListener {
                        item.isExpand = !item.isExpand
                        mRv.adapter!!.notifyDataSetChanged()
                    }
                    stackView!!.findViewById<TextView>(R.id.tv_pool_name).text =
                        item.poolEntity.pool + " (" + item.poolEntity.rollList.size + ")"

                    stackView!!.findViewById<View>(R.id.card_more).setOnClickListener {
                        onSelectPoolListener?.onSelect(item.poolEntity)
                        dismiss()
                    }
                    val mIvPoolType = stackView!!.findViewById<ImageView>(R.id.iv_pool_type)
                    if (ArkCardPoolTypeEnum.isNormalPool(item.poolEntity.poolType)) {
                        mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_normal)
                    } else if (ArkCardPoolTypeEnum.isClassicPool(item.poolEntity.poolType)) {
                        mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_classic)
                    } else {
                        mIvPoolType.setImageResource(R.mipmap.icon_ark_pool_type_limited)
                    }
                    val mIvExpand = stackView!!.findViewById<ImageView>(R.id.iv_expand)
                    if (item.isExpand) {
                        mIvExpand.setImageResource(R.mipmap.icon_ark_pool_arrow_bottom)
                    } else {
                        mIvExpand.setImageResource(R.mipmap.icon_ark_pool_arrow_top)
                    }
                    if (childAt.bottom < stackView!!.height) {
                        c.save()
                        c.translate(0f, (childAt.bottom - stackView!!.height).toFloat())
                        stackView!!.draw(c)
                        c.restore()
                    } else {
                        stackView!!.draw(c)
                    }
                    Log.e("eeeeee", "stackView!!" + stackView!!.height)
                    Log.e("eeeeee", "childAt!!" + childAt!!.bottom)
                }

            }
            if (mRv.tag != null) {
                mRv.removeItemDecoration(mRv.tag as ItemDecoration)
            }
            mRv.tag = itemDecoration
            mRv.addItemDecoration(itemDecoration)
        } else {
            if (mRv.tag != null) {
                mRv.removeItemDecoration(mRv.tag as ItemDecoration)
                mRv.tag = null
            }
            mRv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    return DirVH(
                        LayoutInflater.from(context)
                            .inflate(
                                R.layout.item_card_analyzer_pool_info_reverse_group,
                                parent,
                                false
                            )
                    )
                }

                override fun getItemCount(): Int {
                    return allRollDayArray.size
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    allRollDayArray.get(position).let { item ->
                        val vh = holder as DirVH
                        vh.mTvTime.text = item.day + "  (" + item.list.size + ")"
                        vh.rvItem.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        vh.rvItem.adapter =
                            object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                override fun onCreateViewHolder(
                                    parent: ViewGroup,
                                    viewType: Int
                                ): RecyclerView.ViewHolder {
                                    return ChildVH(
                                        LayoutInflater.from(context)
                                            .inflate(
                                                R.layout.item_card_analyzer_pool_info_reverse_group_item,
                                                parent,
                                                false
                                            )
                                    )
                                }

                                override fun getItemCount(): Int {
                                    return item.list.size
                                }

                                override fun onBindViewHolder(
                                    itemVH: RecyclerView.ViewHolder,
                                    itemPosition: Int
                                ) {
                                    item.list[itemPosition]
                                        .let { childItem ->
                                            val childVH = itemVH as ChildVH
                                            childVH.mTvPosition.text =
                                                "[${item.list.size - itemPosition}]"
                                            childVH.mTvWaifuName.text = childItem.name
                                            childVH.mTvPosition.setTextColor(if (childItem.level == 6)Color.parseColor( "#FFFF6658") else if (childItem.level == 5)Color.parseColor(  "#FFE7B735") else AttrUtil.getColor(R.attr.ark_label_tip_text_color))
                                            childVH.mTvWaifuName.setTextColor(if (childItem.level == 6)Color.parseColor( "#FFFF6658") else if (childItem.level == 5)Color.parseColor(  "#FFE7B735")else if (childItem.level == 4)Color.parseColor(  "#FF8F55B6") else AttrUtil.getColor(R.attr.ark_label_tip_text_color))

                                            childVH.mTvPosition.typeface =
                                                if (childItem.level >= 5) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                            childVH.mTvWaifuName.typeface =
                                                if (childItem.level >= 5) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                                            val calendar = Calendar.getInstance()
                                            calendar.timeInMillis = childItem.ts * 1000L
                                            val startTime =
                                                SimpleDateFormat("HH:mm:ss").format(
                                                    calendar.time
                                                )
                                            childVH.mTvTime.text = startTime
                                            childVH.mTvPoolName.text = childItem.pool
                                            childVH.mViewNew.visibility =
                                                if (childItem.isNew) View.VISIBLE else View.INVISIBLE
                                        }
                                }

                                inner class ChildVH(itemView: View) :
                                    RecyclerView.ViewHolder(itemView) {
                                    var mTvPoolName: TextView
                                    var mTvPosition: TextView
                                    var mTvTime: TextView
                                    var mTvWaifuName: TextView
                                    var mViewNew: View

                                    init {
                                        mTvPoolName = itemView.findViewById(R.id.tv_pool_name)
                                        mTvWaifuName = itemView.findViewById(R.id.tv_waifu_name)
                                        mTvPosition = itemView.findViewById(R.id.tv_position)
                                        mTvTime = itemView.findViewById(R.id.tv_create_time)
                                        mViewNew = itemView.findViewById(R.id.view_new)
                                    }

                                }

                            }
                        val itemDecoration = DaySortItemDecoration(item)

                        if (vh.rvItem.tag != null) {
                            vh.rvItem.removeItemDecoration(vh.rvItem.tag as DaySortItemDecoration)
                        }
                        vh.rvItem.tag = itemDecoration
                        vh.rvItem.addItemDecoration(itemDecoration)
                    }
                }

                inner class DirVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
                    var mTvTime: TextView
                    var divider: View
                    var rvItem: RecyclerView

                    init {
                        mTvTime = itemView.findViewById(R.id.tv_month_statistics)
                        divider = itemView.findViewById(R.id.divider)
                        rvItem = itemView.findViewById(R.id.rv_item)
                    }

                }
            }
        }
    }

    fun setData(
        list: List<ArkCardAnalyzerActivity.ArkGanchaShowEntity>,
        allRollArray: ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>
    ): ArkCardAnalyzerPoolInfoDialog {
        Collections.sort(list, Comparator<ArkCardAnalyzerActivity.ArkGanchaShowEntity> { o1, o2 ->
            val i: Int = o1.rollList.get(0).ts - o2.rollList.get(0).ts
            -i
        })
        this.allRollDayArray.clear()
        var lastDayEntity = ArkGanchaDayEntity()
        allRollArray.forEach {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.ts * 1000L
            val startTime =
                SimpleDateFormat("yyyy/MM/dd").format(
                    calendar.time
                )
            if (lastDayEntity.day == "" || lastDayEntity.day != startTime) {
                lastDayEntity = ArkGanchaDayEntity()
                this.allRollDayArray.add(lastDayEntity)
            }
            lastDayEntity.day = startTime
            lastDayEntity.list.add(it)
        }
        this.originList.clear()
        list.forEach { dir ->
            this.originList.add(ArkGanchaDirEntity().apply {
                poolEntity = dir
            })
        }
        parseList()

        return this
    }

    private fun parseList() {
        this.list.clear()
        this.originList.forEach { dir ->
            this.list.add(dir)
            if (dir.isExpand) {
                dir.poolEntity.rollList.forEach { child ->
                    this.list.add(child)
                }
            }
        }
    }

    fun setOnSelectPoolListener(listener: OnSelectPoolListener): ArkCardAnalyzerPoolInfoDialog {
        this.onSelectPoolListener = listener
        return this
    }

    class ArkGanchaDirEntity {
        var isExpand = true
        lateinit var poolEntity: ArkCardAnalyzerActivity.ArkGanchaShowEntity
    }

    class ArkGanchaDayEntity {
        var isExpand = true
        var day = ""
        var list =
            ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>()
    }
}
