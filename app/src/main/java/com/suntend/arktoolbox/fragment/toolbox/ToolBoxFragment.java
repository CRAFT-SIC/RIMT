package com.suntend.arktoolbox.fragment.toolbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.database.bean.ToolCategoryBean;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.fragment.toolbox.adapter.CategoryArrayAdapter;
import com.suntend.arktoolbox.fragment.toolbox.view.CustomSpinnerView;

import java.util.List;

/**
 * 工具箱页面的fragment
 */
public class ToolBoxFragment extends Fragment {
    private ImageView openNav;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CustomSpinnerView spinner;
    private ArkToolDatabaseHelper helper;
    private CategoryArrayAdapter categoryArrayAdapter;

    public ToolBoxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //初始化数据库辅助类并打开连接
        helper = ArkToolDatabaseHelper.getInstance(getContext());
        helper.openReadLink();
        View view = inflater.inflate(R.layout.fragment_tool_box, container, false);
        initView(view);
        initListener();

        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new ToolBoxRecyclerViewAdapter(PlaceholderContent.ITEMS));
        return view;
    }

    private void initView(View view) {
        openNav = view.findViewById(R.id.img_tool_open_nav);
        searchView = view.findViewById(R.id.search_view_toolbox);
        spinner = view.findViewById(R.id.spinner_category);

        //加载分类
        List<ToolCategoryBean> categoryList = helper.queryToolCategory("1=1");
        categoryArrayAdapter = new CategoryArrayAdapter(getContext(), categoryList, new String[categoryList.size()]);
        spinner.setAdapter(categoryArrayAdapter);
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
                //todo:搜索新数据并展示
                return false;
            }
        });
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //todo:刷新列表数据
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}