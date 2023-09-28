package com.suntend.arktoolbox.ui.forum

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils
import com.suntend.arktoolbox.ui.arkofficial.ArkOfficialAdapter
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.PageStyle

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/1.
 */
open class ArkMainForumFragment : Fragment() {
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2

    private val adapter = ArkOfficialAdapter()
    private lateinit var mTopBanner: BannerViewPager<TopBannerEntity>
    private var fragments = arrayListOf<Fragment>()
    private var tags = arrayListOf<String>("推荐", "社区活动")
    private lateinit var mediator: TabLayoutMediator
    private var changeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            var tabCount = mTabLayout.getTabCount()
            for (i in 0 until tabCount) {
                var tab = mTabLayout.getTabAt(i) as TabLayout.Tab
                var tabView = tab.customView as TextView
                if (tab.getPosition() == position) {
                    tabView.setTextColor(context!!.resources.getColor(R.color.ark_forum_accent_text_color))
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
    private var topingForumAdapter = ForumTopingListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forum_main, container, false)

        //查询本地存储的内容
        val mRvTopingForum: RecyclerView = root.findViewById(R.id.rv_top)
        mRvTopingForum.layoutManager = LinearLayoutManager(context)
        mRvTopingForum.adapter = topingForumAdapter
        mTopBanner = root.findViewById(R.id.banner_view)
        mTopBanner.apply {
            setPageMargin(ConvertUtils.dp2px(57f))
            setRevealWidth(ConvertUtils.dp2px(0f))
            setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
            setScrollDuration(1000)
            adapter = TopBannerAdapter()
            registerLifecycleObserver(lifecycle)
        }.create()

        val array = arrayListOf<TopBannerEntity>()
        array.add(
            TopBannerEntity(
                url = "https://gd-hbimg.huaban.com/ab94efe90f5a9879e1fc5107bf373673308f51654e6df-2ffZyW_fw1200",
                ""
            )
        )
        array.add(
            TopBannerEntity(
                url = "https://gd-hbimg.huaban.com/e825396f7fa4c126d41fc82bf64503b446bab98c1663e-DZllHn_fw1200",
                ""
            )
        )
        array.add(
            TopBannerEntity(
                url = "https://gd-hbimg.huaban.com/f59514556de0a7304298b32ef4858fbbad57c044a68e4-Vgs9YI_fw1200",
                ""
            )
        )
        mTopBanner.refreshData(array)

        mTabLayout = root.findViewById(R.id.tab_layout)
        mViewPager = root.findViewById(R.id.viewpager)
        fragments.clear()
        tags.forEach {
            fragments.add(ForumListFragment(it))
        }
        mTabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        mTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.ark_forum_accent_text_color))
        mViewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)
        mViewPager.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }


        }
        mViewPager.registerOnPageChangeCallback(changeCallback)
        mViewPager.isSaveEnabled=false
        mediator = TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            val tabView = TextView(context)
            tabView.setText(tags[position]);
            tabView.gravity = Gravity.CENTER
            tabView.textSize = 14f
            tabView.setTextColor(Color.parseColor("#FF3D3D3D"))
            tab.setCustomView(tabView);
        };
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
        root.findViewById<View>(R.id.iv_float_person).setOnClickListener {
            startActivity(Intent(activity,ForumPersonInfoActivity::class.java))
        }
        root.findViewById<View>(R.id.iv_float_publish).setOnClickListener {
            startActivity(Intent(activity,ForumPublishActivity::class.java))
        }

        return root
    }
    private fun _loadData(){
        val topingList= arrayListOf<ForumEntity>()
        topingList.add(ForumEntity("aaaaa","aasdfasf"))
        topingList.add(ForumEntity("bbbbb","aasdfasf"))
        topingList.add(ForumEntity("cccc","aasdfasf"))
        topingForumAdapter.setNewData(topingList)
    }


    override fun onResume() {
        super.onResume()
        mTopBanner.startLoop();
        _loadData()
    }

    override fun onPause() {
        super.onPause()
        mTopBanner.stopLoop();
    }

    override fun onDestroy() {
        super.onDestroy()
        mTopBanner.stopLoop();
        mediator.detach();
        mViewPager.unregisterOnPageChangeCallback(changeCallback);

    }

}