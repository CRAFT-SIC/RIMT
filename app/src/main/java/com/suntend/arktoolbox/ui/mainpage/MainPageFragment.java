package com.suntend.arktoolbox.ui.mainpage;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suntend.arktoolbox.ArkToolBoxFragment;
import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.ThemeManager;

public class MainPageFragment extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_mainpage, container, false);

        ImageView openNav = root.findViewById(R.id.openNav);
        ImageView selectTheme = root.findViewById(R.id.selectTheme);
        View toTouchListenerLayout_f = root.findViewById(R.id.toTouchListenerLayout_f);
        View toTouchListenerLayout_s = root.findViewById(R.id.toTouchListenerLayout_s);
        View selectThemeRelativeLayout = root.findViewById(R.id.selectThemeRelativeLayout);
        ImageView themeChange_0 = root.findViewById(R.id.themeChange_0);
        ImageView themeChange_1 = root.findViewById(R.id.themeChange_1);
        ImageView themeChange_2 = root.findViewById(R.id.themeChange_2);
        ImageView themeChange_3 = root.findViewById(R.id.themeChange_3);
        selectThemeRelativeLayout.setVisibility(View.GONE);

        //设置打开侧边栏按钮
        openNav.setOnClickListener(new View.OnClickListener() {
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

        //设置打开主题选择按钮
        selectTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectThemeRelativeLayout.getVisibility() != View.VISIBLE){
                    selectThemeRelativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //设置关闭主题选择-FrameLayout
        toTouchListenerLayout_f.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(selectThemeRelativeLayout.getVisibility() == View.VISIBLE){
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    Rect rect = new Rect();
                    selectThemeRelativeLayout.getDrawingRect(rect);
                    if(!rect.contains(x, y)){
                        //点击事件不在selectThemeRelativeLayout里
                        selectThemeRelativeLayout.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        //设置关闭主题选择-ScrollView
        toTouchListenerLayout_s.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(selectThemeRelativeLayout.getVisibility() == View.VISIBLE){
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    Rect rect = new Rect();
                    selectThemeRelativeLayout.getDrawingRect(rect);
                    if(!rect.contains(x, y)){
                        //点击事件不在selectThemeRelativeLayout里
                        selectThemeRelativeLayout.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        int ThemeSelectedID = ThemeManager.getThemeID(getContext());
        //设置主题
        if (ThemeSelectedID == 0) {
            themeChange_0.setImageResource(R.drawable.drawer_sel_night_w);
            themeChange_1.setImageResource(R.drawable.drawer_unsel);
            themeChange_2.setImageResource(R.drawable.drawer_unsel);
            themeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 1) {
            themeChange_0.setImageResource(R.drawable.drawer_unsel);
            themeChange_1.setImageResource(R.drawable.drawer_sel_night_frostnova);
            themeChange_2.setImageResource(R.drawable.drawer_unsel);
            themeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 2) {
            themeChange_0.setImageResource(R.drawable.drawer_unsel);
            themeChange_1.setImageResource(R.drawable.drawer_unsel);
            themeChange_2.setImageResource(R.drawable.drawer_sel_night_amiya);
            themeChange_3.setImageResource(R.drawable.drawer_unsel);
        } else if (ThemeSelectedID == 3) {
            themeChange_0.setImageResource(R.drawable.drawer_unsel);
            themeChange_1.setImageResource(R.drawable.drawer_unsel);
            themeChange_2.setImageResource(R.drawable.drawer_unsel);
            themeChange_3.setImageResource(R.drawable.drawer_sel_night_kaltsit);
        }

        themeChange_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectThemeRelativeLayout.setVisibility(View.GONE);
                ThemeManager.setThemeID(getActivity(),0);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_W);
                ThemeManager.restartActivity(getActivity());
            }
        });

        themeChange_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectThemeRelativeLayout.setVisibility(View.GONE);
                ThemeManager.setThemeID(getActivity(),1);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Frostnova);
                ThemeManager.restartActivity(getActivity());
            }
        });

        themeChange_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectThemeRelativeLayout.setVisibility(View.GONE);
                ThemeManager.setThemeID(getActivity(),2);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Amiya);
                ThemeManager.restartActivity(getActivity());
            }
        });

        themeChange_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectThemeRelativeLayout.setVisibility(View.GONE);
                ThemeManager.setThemeID(getActivity(),3);
                ThemeManager.setTheme(getActivity(),R.style.Base_Theme_Ark_Kaltsit);
                ThemeManager.restartActivity(getActivity());
            }
        });

        return root;
    }
}