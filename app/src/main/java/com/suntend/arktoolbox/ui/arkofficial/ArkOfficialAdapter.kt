package com.suntend.arktoolbox.ui.arkofficial

import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.suntend.arktoolbox.MainActivity
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil


class ArkOfficialAdapter() :
    RecyclerView.Adapter<ArkOfficialAdapter.ViewHolder>() {
    private var data = ArrayList<ArkOfficialEntity>()


    fun setNewData(data: ArrayList<ArkOfficialEntity>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = data[position]
        if (item.cover.startsWith("http")) {
            Glide.with(holder.itemView.context).load(item.cover).into(holder.mIvCover)
        } else {
            holder.mIvCover.setImageResource(AttrUtil.getDrawableId(item.cover))
        }
        holder.mTvName.setText(item.name)
        holder.mTvTitleName.setText(item.name)
        holder.mTvSubName.setText(item.subName)
        holder.mTvDesc.setText(item.desc)
        if (holder.mRvPlatform.tag == null) {
            holder.mRvPlatform.layoutManager = LinearLayoutManager(
                holder.mRvPlatform.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            val adapter = ArkOfficialListPlatformAdapter()
            holder.mRvPlatform.adapter = adapter
            holder.mRvPlatform.tag = adapter
        }
        (holder.mRvPlatform.tag as ArkOfficialListPlatformAdapter).setNewData(item.links)


        //holder.mIvCover.transitionName="cover$position"
        //holder.mTvName.transitionName="name$position"
        holder.itemView.setOnClickListener {
            var context = holder.itemView.context
            val intent = Intent(holder.itemView.context, ArkOfficialInfoActivity::class.java)
            intent.putExtra("bean", item)


            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                context as MainActivity?,
                Pair.create(
                    holder.mCardCover,
                    "cardCover"
                ),
                Pair.create(
                    holder.mTvName,
                    "name"
                ),
                Pair.create(
                    holder.mTvTitleName,
                    "titleName"
                ),
                Pair.create(
                    holder.mTvSubName,
                    "subName"
                ),
                Pair.create(
                    holder.mTvDesc,
                    "desc"
                ),

                )

            context.startActivity(intent, transitionActivityOptions.toBundle())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ark_official, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvCover: ImageView
        var mCardCover: CardView
        var mTvName: TextView
        var mTvTitleName: TextView
        var mTvSubName: TextView
        var mTvDesc: TextView
        var mRvPlatform: RecyclerView

        init {
            mIvCover = itemView.findViewById(R.id.iv_cover)
            mCardCover = itemView.findViewById(R.id.card_cover)
            mTvName = itemView.findViewById(R.id.tv_name)
            mTvTitleName = itemView.findViewById(R.id.tv_title_name)
            mTvSubName = itemView.findViewById(R.id.tv_sub_name)
            mTvDesc = itemView.findViewById(R.id.tv_desc)
            mRvPlatform = itemView.findViewById(R.id.rv_platform)

        }

    }

}