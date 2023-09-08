package com.suntend.arktoolbox.ui.arkcardanalyzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.base.BasicBottomSheetDialog
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.widgets.LinearProgressBar
import com.suntend.arktoolbox.enums.ArkCardPoolTypeEnum
import java.util.Collections


class ArkCardAnalyzerChangePoolDialog(context: Context) : BasicBottomSheetDialog(context) {
    private var onSelectPoolListener: OnSelectPoolListener? = null
    private lateinit var mRv: RecyclerView
    private lateinit var list: List<ArkCardAnalyzerActivity.ArkGanchaShowEntity>
    private lateinit var currentPool: String
    override val layoutId: Int
        get() = R.layout.dialog_ark_card_analyzer_change_pool

    override fun initView(contentView: View) {
        mRv = contentView.findViewById(R.id.rv)
        mRv.layoutManager = LinearLayoutManager(context)
        mRv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): VH {
                return VH(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_card_analyzer_change_pool, parent, false)
                )
            }

            override fun getItemCount(): Int {
                return list.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val vh = holder as VH
                list.get(position).let {
                    vh.mLinearProgressBar.setProgress(it.rollList.size.toFloat() / list[0].rollList.size.toFloat())
                    var progressColors = intArrayOf(
                        0xFF0256FF.toInt(),
                        0xFF0256FF.toInt()
                    )
                    if (it.poolType == ArkCardPoolTypeEnum.ALL) {
                        progressColors = intArrayOf(
                            0xFF0256FF.toInt(),
                            0xFF0256FF.toInt()
                        )
                    } else if (ArkCardPoolTypeEnum.isSimpleNormal(it.poolType)) {
                        progressColors = intArrayOf(
                            0xFF006CDF.toInt(),
                            0xFF8EB8FF.toInt(),
                            0xFF8DB7FF.toInt()
                        )
                    } else {
                        progressColors = intArrayOf(
                            0xFF5659FF.toInt(),
                            0xFF8DDBFF.toInt(),
                            0xFFFFF4DA.toInt()
                        )
                    }
                    vh.mLinearProgressBar.setColors(progressColors)
                    vh.mTvPoolName.text = it.pool + " (" + it.rollList.size + ")"
                    vh.itemView.setBackgroundColor(
                        if (currentPool == it.pool) AttrUtil.getColor(R.attr.ark_label_tip_background_color) else AttrUtil.getColor(R.attr.ark_card_analyzer_dialog_background_color)
                    )
                }
                vh.divider.visibility =
                    if (position == list.size - 1) View.GONE else View.VISIBLE

                vh.itemView.setOnClickListener {
                    onSelectPoolListener?.onSelect(list[position])
                    dismiss()
                }
            }

            inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
                var mTvPoolName: TextView
                var mLinearProgressBar: LinearProgressBar
                var divider: View

                init {
                    mTvPoolName = itemView.findViewById(R.id.tv_pool_name)
                    mLinearProgressBar = itemView.findViewById(R.id.linear_progressbar)
                    divider = itemView.findViewById(R.id.divider)
                }

            }

        }
    }

    fun setOnSelectPoolListener(listener: OnSelectPoolListener): ArkCardAnalyzerChangePoolDialog {
        this.onSelectPoolListener = listener
        return this
    }

    fun setData(
        list: List<ArkCardAnalyzerActivity.ArkGanchaShowEntity>,
        currentPool: String
    ): ArkCardAnalyzerChangePoolDialog {
        this.currentPool = currentPool
        Collections.sort(list, Comparator<ArkCardAnalyzerActivity.ArkGanchaShowEntity> { o1, o2 ->
            val i: Int = o1.rollList.size - o2.rollList.size
            -i
        })
        this.list = list
        return this
    }
}

interface OnSelectPoolListener {
    fun onSelect(entry: ArkCardAnalyzerActivity.ArkGanchaShowEntity)
}
