package com.suntend.arktoolbox.fragment.toolbox;

import static com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper.ATDG_ID;
import static com.suntend.arktoolbox.fragment.toolbox.bean.ToolboxBean.ToolInfoType.TYPE_INFO;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.DensityUtil;
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil;
import com.suntend.arktoolbox.database.bean.ToolCategoryBean;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.fragment.toolbox.adapter.CategoryArrayAdapter;
import com.suntend.arktoolbox.fragment.toolbox.adapter.RecommendGirdAdapter;
import com.suntend.arktoolbox.fragment.toolbox.adapter.ToolBoxRecyclerViewAdapter;
import com.suntend.arktoolbox.fragment.toolbox.bean.ToolboxBean;
import com.suntend.arktoolbox.fragment.toolbox.view.CustomSpinnerView;
import com.suntend.arktoolbox.ui.arkcardanalyzer.ArkCardAnalyzerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具箱页面的fragment
 */
public class ToolBoxFragment extends Fragment implements ToolBoxRecyclerViewAdapter.OnItemClickListener {
    private View thisView;//保留视图状态避免反复创建
    private ImageView openNav, imgListSwitch, imgToolboxDelete;//导航栏按钮,切换按钮,搜索历史删除
    private SearchView searchView;//搜索栏
    private CustomSpinnerView spinner;//分类下拉
    private CheckBox checkBoxFollow;//查看收藏按钮
    private View viewMask, viewDividerUnderSearch;//遮罩层视图
    private NestedScrollView scrollView;
    private RecyclerView recyclerView;//主要列表
    private LinearLayout layoutMention, layoutRecommend;//提示页面与推荐功能
    private RelativeLayout layoutCategory, relativeMask;//推荐部分
    private GridView gridView;//推荐列表和搜索历史
    private TextView textMention, textMentionButton, textRecommend, textTitle,
            textGuideNext, textGuideSkip;//提示页面的显示,标题,引导
    private ArkToolDatabaseHelper helper;//数据库辅助类
    private CategoryArrayAdapter categoryArrayAdapter;//分类列表适配器
    private ToolBoxRecyclerViewAdapter recyclerViewAdapter;
    private RecommendGirdAdapter girdAdapter;
    private Context mContext;//上下文
    private Boolean followChecked = false;//记录是否选中仅查看收藏
    private String searchContent = "";//记录当前的搜索框内容
    private WebView webView;
    public static final String LIST_TYPE_KEY = "tool_list_view_type";
    public static final String FIRST_GUIDE_KEY = "first_guide_key";
    private int guideProgress = 1;
    private String listType;
    private ImageView tempImg1, tempImg2;//引导使用的临时视图
    private TextView tempText1, tempText2;//引导使用的临时视图

    public ToolBoxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (thisView != null) return thisView;
        Log.d("shownow", "创建工具箱碎片化页面");
        mContext = getContext();
        //初始化数据库辅助类并打开连接
        helper = ArkToolDatabaseHelper.getInstance(mContext);
        helper.openReadLink();
        thisView = inflater.inflate(R.layout.fragment_tool_box, container, false);
        initView(thisView);
        initListener();
        return thisView;
    }

    private void initView(View view) {
        openNav = view.findViewById(R.id.img_tool_open_nav);
        searchView = view.findViewById(R.id.search_view_toolbox);
        spinner = view.findViewById(R.id.spinner_category);
        checkBoxFollow = view.findViewById(R.id.checkbox_follow);
        recyclerView = view.findViewById(R.id.recycle_view_tool_box);
        layoutMention = view.findViewById(R.id.linear_mention);
        textMention = view.findViewById(R.id.text_toolbox_mention);
        textMentionButton = view.findViewById(R.id.text_toolbox_button);
        layoutCategory = view.findViewById(R.id.relative_category);
        layoutRecommend = view.findViewById(R.id.linear_recommend);
        textRecommend = view.findViewById(R.id.text_recommend);
        imgToolboxDelete = view.findViewById(R.id.img_toolbox_delete);
        gridView = view.findViewById(R.id.grid_view_recommend);
        viewMask = view.findViewById(R.id.view_mask_transparent);
        webView = view.findViewById(R.id.webView_fragment_toolbox);
        textTitle = view.findViewById(R.id.text_toolbox_title);
        imgListSwitch = view.findViewById(R.id.img_tool_list_switch);
        scrollView = view.findViewById(R.id.scrollView_tool_box);
        viewDividerUnderSearch = view.findViewById(R.id.view_divider_under_search);
        relativeMask = view.findViewById(R.id.relative_mask_transparent);
        textGuideNext = view.findViewById(R.id.text_guide_next);
        textGuideSkip = view.findViewById(R.id.text_guide_skip);

        //初始化切换按钮的图像资源
        listType = helper.getConfigValue(LIST_TYPE_KEY);//初始化列表类型
        TypedValue drawable = new TypedValue();
        mContext.getTheme().resolveAttribute(listType.equals("list") ? R.attr.img_icon_grid : R.attr.img_icon_list, drawable, true);
        imgListSwitch.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
        //修改搜索栏字体大小
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        //加载分类
        List<ToolCategoryBean> categoryList = helper.queryToolCategory("1=1");
        categoryArrayAdapter = new CategoryArrayAdapter(mContext, categoryList, new String[categoryList.size()]);
        spinner.setAdapter(categoryArrayAdapter);
        //加载列表
        refreshRecyclerList();
        //加载推荐列表
        List<String> gridList = new ArrayList<>();
        gridList.add("抽卡分析");
        gridList.add("敌人一览");
        gridList.add("干员档案");
        gridList.add("掉落汇总");
        girdAdapter = new RecommendGirdAdapter(mContext, gridList);
        gridView.setAdapter(girdAdapter);

        //显示引导页
        if (helper.getConfigValue(FIRST_GUIDE_KEY).equals("no")) {
            guideProgress = 1;
            relativeMask.setVisibility(View.VISIBLE);
            helper.updateConfigKey(FIRST_GUIDE_KEY, "yes");

            //根据位置添加图像与文字
            tempImg1 = new ImageView(mContext);
            tempImg1.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tempImg1.getLayoutParams();
            params.leftMargin = DensityUtil.dpToPx(mContext, 20);
            params.topMargin = DensityUtil.dpToPx(mContext, 100);
            tempImg1.setImageResource(R.mipmap.img_guide_category);
            relativeMask.addView(tempImg1);

            tempText1 = new TextView(mContext);
            tempText1.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) tempText1.getLayoutParams();
            params2.leftMargin = DensityUtil.dpToPx(mContext, 160);
            params2.topMargin = DensityUtil.dpToPx(mContext, 125);
            tempText1.setText("在这切换筛选范围");
            tempText1.setTextColor(mContext.getColor(R.color.white));
            relativeMask.addView(tempText1);
        }
    }

    private void initListener() {
        //打开导航窗口
        openNav.setOnClickListener(view -> {
            if (searchView.hasFocus()) {
                searchView.clearFocus();
                searchView.setQuery("", false);
            } else {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null)
                    mainActivity.openDrawer();
            }
        });
        //列表模式切换
        imgListSwitch.setOnClickListener(view -> {
            //先切换类型
            listType = listType.equals("list") ? "grid" : "list";
            helper.updateConfigKey(LIST_TYPE_KEY, listType);
            //切换显示
            TypedValue drawable = new TypedValue();
            mContext.getTheme().resolveAttribute(listType.equals("list") ? R.attr.img_icon_grid : R.attr.img_icon_list, drawable, true);
            imgListSwitch.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
            refreshRecyclerList();
        });
        //搜索监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContent = newText;
                if (searchView.hasFocus() && searchContent.equals("")) {
                    //重新展示推荐
                    layoutCategory.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    layoutMention.setVisibility(View.GONE);
                    layoutRecommend.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    refreshRecyclerList();
                }
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener((view, b) -> {
            //当搜索框被选中，显示推荐
            layoutCategory.setVisibility(b ? View.GONE : View.VISIBLE);
            recyclerView.setVisibility(b ? View.GONE : View.VISIBLE);
            layoutMention.setVisibility(b ? View.GONE : (followChecked && recyclerViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE));
            layoutRecommend.setVisibility(b ? View.VISIBLE : View.GONE);
            gridView.setVisibility(b ? View.VISIBLE : View.GONE);
            textTitle.setText(mContext.getText(b ? R.string.text_toolbox_search : R.string.text_toolbox_title));
            if (b) {
                openNav.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_arrow_left_grey));
            } else {
                TypedValue drawable = new TypedValue();
                mContext.getTheme().resolveAttribute(R.attr.fragment_nav, drawable, true);
                openNav.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
                refreshRecyclerList();
            }
        });
        //打开关闭分类框监听
        spinner.setSpinnerPopStatusListener(new CustomSpinnerView.OnSpinnerPopStatusListener() {
            @Override
            public void onSpinnerOpened(int spinnerId) {
                categoryArrayAdapter.updateTriangleImg(true);
                viewMask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSpinnerClosed(int spinnerId) {
                categoryArrayAdapter.updateTriangleImg(false);
                viewMask.setVisibility(View.GONE);
            }
        });
        //选择分类监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshRecyclerList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //是否仅查看收藏监听
        checkBoxFollow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            followChecked = isChecked;
            TypedValue color = new TypedValue();
            mContext.getTheme().resolveAttribute(isChecked ?
                    R.attr.color_checkbox_text_select : R.attr.color_checkbox_text_unselect, color, true);
            buttonView.setTextColor(mContext.getColor(color.resourceId));
            recyclerViewAdapter.setFollowChecked(followChecked);
            refreshRecyclerList();
        });
        textMentionButton.setOnClickListener(view -> {
            if (categoryArrayAdapter.getSelectCategoryId() != ATDG_ID) {
                checkBoxFollow.setChecked(false);
            } else {
                //todo：去提需求的跳转逻辑
            }
        });
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            searchView.setQuery(girdAdapter.getItem(i), false);
        });
        //根据滑动高度显示视图
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (view, l, t, ol, ot) ->
                viewDividerUnderSearch.setVisibility(t > DensityUtil.dpToPx(mContext, 40) ? View.VISIBLE : View.INVISIBLE));
        textGuideSkip.setOnClickListener(view -> relativeMask.setVisibility(View.GONE));
        textGuideNext.setOnClickListener(view -> {
            switch (guideProgress) {
                case 1:
                    guideProgress = 2;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tempImg1.getLayoutParams();
                    params.rightMargin = DensityUtil.dpToPx(mContext, 6);
                    params.topMargin = DensityUtil.dpToPx(mContext, 212);
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    tempImg1.setImageResource(R.mipmap.img_guide_follow);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) tempText1.getLayoutParams();
                    params2.topMargin = DensityUtil.dpToPx(mContext, 225);
                    params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    tempText1.setText("单击圆框部分/长摁功能\n都可以将功能加入收藏夹");
                    break;
                case 2:
                    guideProgress = 3;
                    textGuideNext.setText("知道啦");
                    textGuideSkip.setVisibility(View.INVISIBLE);
                    textGuideSkip.setOnClickListener(null);

                    tempImg1.setImageResource(R.mipmap.img_guide_cancel);
                    RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) tempText1.getLayoutParams();
                    params3.rightMargin = DensityUtil.dpToPx(mContext, 85);
                    params3.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    params3.addRule(RelativeLayout.ALIGN_PARENT_END);
                    tempText1.setText("在只看收藏状态下\n可取消收藏的功能");

                    tempImg2 = new ImageView(mContext);
                    tempImg2.setLayoutParams(new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) tempImg2.getLayoutParams();
                    params4.rightMargin = DensityUtil.dpToPx(mContext, 6);
                    params4.topMargin = DensityUtil.dpToPx(mContext, 100);
                    params4.addRule(RelativeLayout.ALIGN_PARENT_END);
                    tempImg2.setImageResource(R.mipmap.img_guide_only_follow);
                    relativeMask.addView(tempImg2);

                    tempText2 = new TextView(mContext);
                    tempText2.setLayoutParams(new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) tempText2.getLayoutParams();
                    params5.leftMargin = DensityUtil.dpToPx(mContext, 60);
                    params5.topMargin = DensityUtil.dpToPx(mContext, 115);
                    tempText2.setText("勾选只看收藏\n可快速筛选已收藏功能");
                    tempText2.setTextColor(mContext.getColor(R.color.white));
                    relativeMask.addView(tempText2);
                    break;
                default:
                    relativeMask.setVisibility(View.GONE);
                    relativeMask.removeAllViews();
                    break;
            }
        });
    }

    /**
     * 点击列表项后的回调
     *
     * @param view     视图
     * @param position 位置
     */
    @Override
    public void onItemClick(View view, int position) {
        //todo:打开网页的逻辑
        ToolboxBean bean = recyclerViewAdapter.getItemByPosition(position);
        if (bean.getName().equals("抽卡分析")) {
            mContext.startActivity(new Intent(mContext, ArkCardAnalyzerActivity.class));
            return;
        }
        RIMTUtil.ShowToast(mContext, "正在前往网页(待完善)");
        //webView.setVisibility(View.VISIBLE);
        //webView.loadUrl(bean.getAddressUrl());
        Uri uri = Uri.parse(bean.getAddressUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * 刷新主列表数据,并判断页面展示什么
     */
    private void refreshRecyclerList() {
        //刷新列表
        List<ToolboxBean> beanList = helper.queryToolBoxInfo(searchContent, followChecked, categoryArrayAdapter.getSelectCategoryId());
        if (recyclerViewAdapter == null)
            recyclerViewAdapter = new ToolBoxRecyclerViewAdapter(mContext, new ArrayList<>(), helper, this);
        if (listType.equals("list")) {
            //列表显示
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            //添加底部提示
            if (beanList.size() != 0) {
                ToolboxBean bean = new ToolboxBean(-10, "已经到底了", ToolboxBean.ToolInfoType.TYPE_BOTTOM);
                beanList.add(bean);
            }
        } else {
            //网格显示
            List<Integer> singlePosition = new ArrayList<>();
            for (int i = 0; i < beanList.size(); i++)
                if (!beanList.get(i).getType().equals(TYPE_INFO)) singlePosition.add(i);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 5);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // position是第几列,显示的列数 = spanCount / spanSize ;
                    return singlePosition.contains(position) ? 5 : 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        recyclerViewAdapter.setNewData(beanList, searchContent, listType);
        recyclerView.setAdapter(recyclerViewAdapter);//重载列表

        layoutCategory.setVisibility(View.VISIBLE);
        layoutRecommend.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        //切换页面逻辑
        if (followChecked && beanList.size() == 0) {
            layoutMention.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textMention.setText(mContext.getText(R.string.text_toolbox_mention_follow));
            textMentionButton.setText(mContext.getText(R.string.text_toolbox_button_mention_follow));
        } else if (categoryArrayAdapter.getSelectCategoryId() == ATDG_ID || beanList.size() == 0) {
            layoutMention.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            textMention.setText(mContext.getText(R.string.text_toolbox_mention_advice));
            textMentionButton.setText(mContext.getText(R.string.text_toolbox_button_mention_advice));
        } else {
            layoutMention.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}