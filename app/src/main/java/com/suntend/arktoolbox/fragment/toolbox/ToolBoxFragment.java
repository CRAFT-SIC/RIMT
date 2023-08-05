package com.suntend.arktoolbox.fragment.toolbox;

import static com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper.ATDG_ID;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.database.bean.ToolCategoryBean;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.fragment.toolbox.adapter.CategoryArrayAdapter;
import com.suntend.arktoolbox.fragment.toolbox.adapter.RecommendGirdAdapter;
import com.suntend.arktoolbox.fragment.toolbox.adapter.ToolBoxRecyclerViewAdapter;
import com.suntend.arktoolbox.fragment.toolbox.bean.ToolboxBean;
import com.suntend.arktoolbox.fragment.toolbox.view.CustomSpinnerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具箱页面的fragment
 */
public class ToolBoxFragment extends Fragment {
    private ImageView openNav, imgToolboxDelete;//导航栏按钮,搜索历史删除
    private SearchView searchView;//搜索栏
    private CustomSpinnerView spinner;//分类下拉
    private CheckBox checkBoxFollow;//查看收藏按钮
    private RecyclerView recyclerView;//主要列表
    private LinearLayout layoutMention, layoutRecommend;//提示页面与推荐功能
    private RelativeLayout layoutCategory;//推荐部分
    private GridView gridView;//推荐列表和搜索历史
    private TextView textMention, textMentionButton, textRecommend;//提示页面的显示
    private ArkToolDatabaseHelper helper;//数据库辅助类
    private CategoryArrayAdapter categoryArrayAdapter;//分类列表适配器
    private ToolBoxRecyclerViewAdapter recyclerViewAdapter;
    private RecommendGirdAdapter girdAdapter;
    private Context mContext;//上下文
    private Boolean followChecked = false;//记录是否选中仅查看收藏
    private String searchContent = "";//记录当前的搜索框内容

    public ToolBoxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        //初始化数据库辅助类并打开连接
        helper = ArkToolDatabaseHelper.getInstance(mContext);
        helper.openReadLink();
        View view = inflater.inflate(R.layout.fragment_tool_box, container, false);
        initView(view);
        initListener();
        return view;
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

        //修改搜索栏字体大小
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        //加载分类
        List<ToolCategoryBean> categoryList = helper.queryToolCategory("1=1");
        categoryArrayAdapter = new CategoryArrayAdapter(mContext, categoryList, new String[categoryList.size()]);
        spinner.setAdapter(categoryArrayAdapter);
        //加载列表
        List<ToolboxBean> beanList = helper.queryToolBoxInfo(searchContent, followChecked, -1);
        recyclerViewAdapter = new ToolBoxRecyclerViewAdapter(mContext, beanList, helper);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(recyclerViewAdapter);
        //加载推荐列表
        List<String> gridList = new ArrayList<>();
        gridList.add("抽卡分析");
        gridList.add("敌人一览");
        gridList.add("干员档案");
        gridList.add("掉落汇总");
        girdAdapter = new RecommendGirdAdapter(mContext, gridList);
        gridView.setAdapter(girdAdapter);
    }

    private void initListener() {
        //打开导航窗口
        openNav.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null)
                mainActivity.openDrawer();
            searchView.clearFocus();
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
                refreshRecyclerList();
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
        });
        //打开关闭分类框监听
        spinner.setSpinnerPopStatusListener(new CustomSpinnerView.OnSpinnerPopStatusListener() {
            @Override
            public void onSpinnerOpened(int spinnerId) {
                categoryArrayAdapter.updateTriangleImg(true);
            }

            @Override
            public void onSpinnerClosed(int spinnerId) {
                categoryArrayAdapter.updateTriangleImg(false);
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
    }

    /**
     * 刷新主列表数据,并判断页面展示什么
     */
    private void refreshRecyclerList() {
        //刷新列表
        List<ToolboxBean> beanList = helper.queryToolBoxInfo(searchContent, followChecked, categoryArrayAdapter.getSelectCategoryId());
        recyclerViewAdapter.setNewData(beanList, searchContent);

        layoutCategory.setVisibility(View.VISIBLE);
        layoutRecommend.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        //切换页面逻辑
        if (followChecked && beanList.size() == 0) {
            layoutMention.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textMention.setText(mContext.getText(R.string.text_toolbox_mention_follow));
            textMentionButton.setText(mContext.getText(R.string.text_toolbox_button_mention_follow));
        } else if (categoryArrayAdapter.getSelectCategoryId() == ATDG_ID) {
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