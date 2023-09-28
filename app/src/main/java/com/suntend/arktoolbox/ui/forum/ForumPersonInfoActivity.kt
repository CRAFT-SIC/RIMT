package com.suntend.arktoolbox.ui.forum

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.base.BasicActivity
import com.suntend.arktoolbox.widgets.BadgeLinearLayout

class ForumPersonInfoActivity : BasicActivity() {
    private var isMine = true
    private lateinit var linearBadge: BadgeLinearLayout
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2

    private var fragments = arrayListOf<Fragment>()
    private var tags = arrayListOf<String>()
    private lateinit var mediator: TabLayoutMediator
    private var changeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            var tabCount = mTabLayout.getTabCount()
            for (i in 0 until tabCount) {
                var tab = mTabLayout.getTabAt(i) as TabLayout.Tab
                var tabView = tab.customView as TextView
                if (tab.getPosition() == position) {
                    tabView.setTextColor(this@ForumPersonInfoActivity.resources.getColor(R.color.ark_forum_accent_text_color))
                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
                    tabView.invalidate()
                } else {
                    tabView.setTextColor(Color.parseColor("#FF3D3D3D"))
                    tabView.setTypeface(Typeface.DEFAULT);
                    tabView.invalidate()
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_person_info)
        linearBadge = findViewById<BadgeLinearLayout>(R.id.linear_badge)
        (linearBadge.parent as View).setOnClickListener {
            startActivity(Intent(this@ForumPersonInfoActivity, ForumPersonEditActivity::class.java))
        }
        updateUI()
    }

    private fun updateUI() {
        tags.clear()
        tags.add(getSubjectStr() + "的帖子")
        tags.add(getSubjectStr() + "的回复")
        if (isMine) {
            tags.add(getSubjectStr() + "的收藏")
        }
        fragments.clear()
        tags.forEach {
            if (it.contains("帖子") || it.contains("收藏")) {
                fragments.add(ForumListFragment(it, nestedScrollingEnabled = false))
            } else if (it.contains("回复")) {
                fragments.add(ForumListFragment(it))
            }
        }

        mTabLayout = findViewById(R.id.tab_layout)
        mViewPager = findViewById(R.id.viewpager)

        mTabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        mTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.ark_forum_accent_text_color))
        mViewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)
        mViewPager.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }


        }
        mViewPager.registerOnPageChangeCallback(changeCallback)
        mViewPager.isSaveEnabled = false
        mediator = TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            val tabView = TextView(this@ForumPersonInfoActivity)
            tabView.setText(tags[position]);
            tabView.gravity = Gravity.CENTER
            tabView.textSize = 14f
            tabView.setTextColor(Color.parseColor("#FF3D3D3D"))
            tab.setCustomView(tabView);
        };
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }

    private fun getSubjectStr(): String {
        return if (isMine) "我" else "TA"
    }

    override fun onResume() {
        super.onResume()
        //更新
    }
}