package com.suntend.arktoolbox.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R

class ForumTopingListAdapter : RecyclerView.Adapter<ForumTopingListAdapter.ForumHolder>() {
    var list: ArrayList<ForumEntity>? = null
    fun setNewData(list: ArrayList<ForumEntity>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumHolder {
        return ForumHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_forum_top_forum, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ForumHolder, position: Int) {
        holder.mTvForumTitle.text = list!![position].title
        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class ForumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvForumTitle: TextView

        init {
            mTvForumTitle = itemView.findViewById(R.id.tv_forum_title)
        }
    }
}