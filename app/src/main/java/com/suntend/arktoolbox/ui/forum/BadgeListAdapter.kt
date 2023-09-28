package com.suntend.arktoolbox.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R

class BadgeListAdapter : RecyclerView.Adapter<BadgeListAdapter.BadgeHolder>() {
    var list: ArrayList<BadgeEntity>? = null
    fun setNewData(list: ArrayList<BadgeEntity>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeHolder {
        return BadgeHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_badge_tag_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BadgeHolder, position: Int) {}
    override fun getItemCount(): Int {
        return list!!.size
    }

    class BadgeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvName: TextView
        var mTvTitle: TextView

        init {
            mTvName = itemView.findViewById(R.id.tv_title)
            mTvTitle = itemView.findViewById(R.id.tv_message)
        }
    }
}