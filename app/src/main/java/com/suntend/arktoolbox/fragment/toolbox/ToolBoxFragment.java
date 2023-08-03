package com.suntend.arktoolbox.fragment.toolbox;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;

/**
 * 工具箱页面的fragment
 */
public class ToolBoxFragment extends Fragment {
    private ImageView openNav;
    private RecyclerView recyclerView;

    public ToolBoxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_box, container, false);
        initView(view);
        initListener();

        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new ToolBoxRecyclerViewAdapter(PlaceholderContent.ITEMS));
        return view;
    }

    private void initView(View view) {
        openNav = view.findViewById(R.id.img_tool_open_nav);
    }

    private void initListener() {
        //打开导航窗口
        openNav.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openDrawer();
            }
        });
    }
}