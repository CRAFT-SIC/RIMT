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

class BadgeListActivity : BasicActivity() {
    private lateinit var linearBadge: BadgeLinearLayout
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2
    private var fragments = arrayListOf<Fragment>()
    private var tags = arrayListOf<String>("未持有", "已持有", "惩罚徽章")
    private lateinit var mediator: TabLayoutMediator
    private var changeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            var tabCount = mTabLayout.getTabCount()
            for (i in 0 until tabCount) {
                var tab = mTabLayout.getTabAt(i) as TabLayout.Tab
                var tabView = tab.customView as TextView
                if (tab.getPosition() == position) {
                    tabView.setTextColor(this@BadgeListActivity.resources.getColor(R.color.ark_forum_accent_text_color))
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
        setContentView(R.layout.activity_badge_list)
        linearBadge = findViewById<BadgeLinearLayout>(R.id.linear_badge)

        mTabLayout = findViewById(R.id.tab_layout)
        mViewPager = findViewById(R.id.viewpager)
        fragments.clear()
        tags.forEach {
            fragments.add(BadgeListFragment(it))
        }
        mTabLayout.tabMode = TabLayout.MODE_FIXED
        mTabLayout.isInlineLabel = true
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
            val tabView = TextView(this@BadgeListActivity)
            tabView.setText(tags[position]);
            tabView.gravity = Gravity.CENTER
            tabView.textSize = 14f
            tabView.setTextColor(Color.parseColor("#FF3D3D3D"))
            tab.setCustomView(tabView);
        };
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }
}