package com.suntend.arktoolbox.ui.arkofficial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil

class ArkOfficialListPlatformAdapter :
RecyclerView.Adapter<ArkOfficialListPlatformAdapter.ViewHolder>() {
    private var data = ArrayList<ArkOfficialLinkEntity>()


    fun setNewData(data: ArrayList<ArkOfficialLinkEntity>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.mIvIcon.setImageResource(AttrUtil.getResId(item.platform.iconAttr))
        holder.itemView.setOnClickListener {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ark_official_list_platform, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvIcon: ImageView


        init {
            mIvIcon = itemView.findViewById(R.id.iv_icon)
        }

    }

}