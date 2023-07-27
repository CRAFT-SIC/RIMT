package com.suntend.arktoolbox;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

public class ArkToolBoxFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_arktoolbox, container, false);
        ImageView OpenNav = root.findViewById(R.id.openNav);

        //设置打开侧边栏按钮
        OpenNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取 MainActivity 的引用
                MainActivity mainActivity = (MainActivity) getActivity();

                // 调用 MainActivity 的 openDrawer() 方法
                if (mainActivity != null) {
                    mainActivity.openDrawer();
                }
            }
        });
        return root;
    }
}