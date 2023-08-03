package com.suntend.arktoolbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.database.bean.ToolCategoryBean;
import com.suntend.arktoolbox.database.bean.ToolInfoBean;
import com.suntend.arktoolbox.fragment.toolbox.ToolBoxFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout DrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetDrawer();

        // 获取FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // 创建Fragment实例
        ArkToolBoxFragment fragmentArkToolBox = new ArkToolBoxFragment();
        ToolBoxFragment fragment = new ToolBoxFragment();

        // 使用FragmentManager将Fragment添加到FrameLayout中
        fragmentManager.beginTransaction()
                .add(R.id.fragment_controller, fragment)
                .commit();

        ArkToolDatabaseHelper helper = ArkToolDatabaseHelper.getInstance(this);
        helper.openReadLink();
        List<ToolCategoryBean> list = helper.queryToolCategory("1=1");
        List<ToolInfoBean> list1 = helper.queryToolInfo("1=1");
        int i = 1;
    }

    // 设置侧边栏操作
    private void SetDrawer() {
        SharedPreferences Settings_Preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Log.e("S", "Clicked");

        View Drawer_Settings_AutoUpdate_Layout = findViewById(R.id.drawer_settings_autoupdate_layout);
        TextView Drawer_Settings_AutoUpdate_Text = findViewById(R.id.drawer_settings_autoupdate_text);
        TextView Drawer_Settings_AutoUpdate_Status = findViewById(R.id.drawer_settings_autoupdate_status);

        toolboxCheck(Settings_Preferences.getInt("AutoUpdate", 0), Drawer_Settings_AutoUpdate_Layout, Drawer_Settings_AutoUpdate_Text, Drawer_Settings_AutoUpdate_Status);

        Drawer_Settings_AutoUpdate_Layout.setOnClickListener(view -> {
            int currentStatus = Settings_Preferences.getInt("AutoUpdate", 0);
            Settings_Preferences.edit().putInt("AutoUpdate", currentStatus == 0 ? 1 : 0).apply();
            currentStatus = Settings_Preferences.getInt("AutoUpdate", 0);
            toolboxCheck(currentStatus, Drawer_Settings_AutoUpdate_Layout, Drawer_Settings_AutoUpdate_Text, Drawer_Settings_AutoUpdate_Status);
            Log.e("S", "AutoUpdate=" + Settings_Preferences.getInt("AutoUpdate", 0));
        });

        SwitchMaterial checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(view -> {
            if (checkbox.isChecked())
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void toolboxCheck(int status, View layout, TextView text, TextView statusText) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();

        int colorBackgroundAttr = status == 0 ? R.attr.drawer_toolbox_unchecked_background : R.attr.drawer_toolbox_checked_background;
        theme.resolveAttribute(colorBackgroundAttr, typedValue, true);
        int colorBackground = typedValue.data;

        int colorTextAttr = status == 0 ? R.attr.drawer_toolbox_unchecked_text_color : R.attr.drawer_toolbox_checked_text_color;
        theme.resolveAttribute(colorTextAttr, typedValue, true);
        int colorText = typedValue.data;

        int statusResource = status == 0 ? R.string.general_off : R.string.general_on;

        layout.setBackgroundColor(colorBackground);
        text.setTextColor(colorText);
        statusText.setText(statusResource);
    }

    public void openDrawer() {
        // 找到 DrawerLayout 和自定义的侧滑栏布局
        DrawerLayout = findViewById(R.id.drawer_layout);
        // 设置侧滑栏打开和关闭的监听器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, DrawerLayout, null, 0, 0);
        DrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        DrawerLayout.openDrawer(GravityCompat.START);
    }
    //设置侧边栏结束
}