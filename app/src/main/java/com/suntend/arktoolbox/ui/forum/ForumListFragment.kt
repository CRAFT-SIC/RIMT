package com.suntend.arktoolbox.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R

class ForumListFragment(
    private var tag: String,
    private var nestedScrollingEnabled: Boolean = true
) : Fragment() {
    private lateinit var mRv: RecyclerView
    private var adapter: ForumMainListAdapter = ForumMainListAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forum_tag_forum, container, false)

        //查询本地存储的内容
        mRv = root.findViewById(R.id.rv)
        mRv.layoutManager = LinearLayoutManager(context)
        mRv.adapter = adapter
        var list = arrayListOf(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        list.add(ForumEntity("112", "12333"))
        adapter.setNewData(list)
        return root
    }


}