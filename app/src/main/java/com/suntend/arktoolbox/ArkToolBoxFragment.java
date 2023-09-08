package com.suntend.arktoolbox;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.suntend.arktoolbox.RIMTUtil.ThemeManager;

public class ArkToolBoxFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_arktoolbox, container, false);
        ImageView OpenDrawer = root.findViewById(R.id.openDrawer);
        ImageView ThemeChange_0 = root.findViewById(R.id.themeChange_0);
        ImageView ThemeChange_1 = root.findViewById(R.id.themeChange_1);
        ImageView ThemeChange_2 = root.findViewById(R.id.themeChange_2);
        ImageView ThemeChange_3 = root.findViewById(R.id.themeChange_3);




        //设置打开侧边栏按钮
        OpenDrawer.setOnClickListener(new View.OnClickListener() {
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
        
        int ThemeSelectedID = ThemeManager.getThemeID(getContext());
        //设置主题
        if (ThemeSelectedID == 0) {
            ThemeChange_1.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_2.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 1) {
            ThemeChange_0.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_2.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 2) {
            ThemeChange_0.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_1.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 3) {
            ThemeChange_0.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_1.setImageResource(R.drawable.drawer_unsel);
            ThemeChange_2.setImageResource(R.drawable.drawer_unsel);
        }

        ThemeChange_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeManager.setThemeID(getActivity(),0);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Kaltsit);
                ThemeManager.restartActivity(getActivity());
            }
        });

        ThemeChange_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeManager.setThemeID(getActivity(),1);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Amiya);
                ThemeManager.restartActivity(getActivity());
            }
        });

        ThemeChange_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeManager.setThemeID(getActivity(),2);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_W);
                ThemeManager.restartActivity(getActivity());
            }
        });

        ThemeChange_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeManager.setThemeID(getActivity(),3);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Frostnova);
                ThemeManager.restartActivity(getActivity());
            }
        });

        return root;
    }
}