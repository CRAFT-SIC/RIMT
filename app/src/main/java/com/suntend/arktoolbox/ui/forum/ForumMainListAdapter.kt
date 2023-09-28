package com.suntend.arktoolbox.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R

class ForumMainListAdapter : RecyclerView.Adapter<ForumMainListAdapter.ForumHolder>() {
    var list: ArrayList<ForumEntity>? = null
    fun setNewData(list: ArrayList<ForumEntity>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumHolder {
        return ForumHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_forum_tag_forum_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ForumHolder, position: Int) {}
    override fun getItemCount(): Int {
        return list!!.size
    }

    class ForumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvUserName: TextView
        var mTvForumTitle: TextView

        init {
            mTvUserName = itemView.findViewById(R.id.tv_user_name)
            mTvForumTitle = itemView.findViewById(R.id.tv_forum_title)
        }
    }
}