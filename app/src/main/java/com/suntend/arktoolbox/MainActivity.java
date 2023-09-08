package com.suntend.arktoolbox;
/*
 * JamXi
 */

import static com.suntend.arktoolbox.RIMTUtil.FlarumUserUtil.getUser;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.suntend.arktoolbox.RIMTUtil.DataExchange;
import com.suntend.arktoolbox.RIMTUtil.FileUtil;
import com.suntend.arktoolbox.RIMTUtil.FlarumUserUtil;
import com.suntend.arktoolbox.RIMTUtil.NavController;
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil;
import com.suntend.arktoolbox.RIMTUtil.ThemeManager;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.fragment.toolbox.ToolBoxFragment;
import com.suntend.arktoolbox.ui.arklabel.ArkLabelFragment;
import com.suntend.arktoolbox.ui.arkofficial.ArkOfficialFragment;
import com.suntend.arktoolbox.widgets.mainpage.MainPageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout DrawerLayout;
    private ImageView nav_0, nav_1, nav_2, nav_3, nav_4;
    private ImageView nav_sel_0, nav_sel_1, nav_sel_2, nav_sel_3, nav_sel_4;
    DataExchange dataExchange = new DataExchange();
    List<String> gameList = new ArrayList<>();

    LinearLayout[] drawerGames;
    ImageView[] drawerGamesSel;
    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.checkTheme(getApplicationContext());
        setTheme(ThemeManager.getTheme(getApplicationContext()));
        setContentView(R.layout.activity_main);

        // 设置状态栏颜色
        setStatusBarColor();

        // 设置游戏列表
        InitGameList();

        // 设置导航栏列表
        InitNavList();

        // 设置导航栏
        InitNavController();

        // 设置用户
        SetUser();

        // 调用设置侧边栏的方法
        SetDrawer();

        //避免切换显示模式后fragment多次创建
        if (savedInstanceState != null) return;

        // 获取FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragments.add(new MainPageFragment());
        fragments.add(new ArkOfficialFragment());
        fragments.add(new ToolBoxFragment());
        fragments.add(new ArkLabelFragment());
        fragments.add(new ArkToolBoxFragment());


        // 使用FragmentManager将Fragment添加到FrameLayout中
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_controller, fragments.get(0))
                .commit();
    }

    // 设置游戏列表
    private void InitGameList() {
        drawerGames = new LinearLayout[]{findViewById(R.id.drawer_games_arktoolbox), findViewById(R.id.drawer_games_starrail)};
        drawerGamesSel = new ImageView[]{findViewById(R.id.drawer_games_arktoolbox_sel), findViewById(R.id.drawer_games_starrail_sel)};
        gameList.add("L0-P1.明日方舟工具箱");
        gameList.add("L1-P2.崩坏：星穹铁道");
    }

    // 设置导航栏列表-暂时无作用
    private void InitNavList() {
        NavController.resetNavList();
        NavController.setNavList("L0-P1.首页", "L1-P2", "L2-P3", "L3-P4", "L4-P5");
    }

    // 设置导航栏
    private void InitNavController() {
        nav_0 = findViewById(R.id.nav_0);
        nav_1 = findViewById(R.id.nav_1);
        nav_2 = findViewById(R.id.nav_2);
        nav_3 = findViewById(R.id.nav_3);
        nav_4 = findViewById(R.id.nav_4);
        nav_sel_0 = findViewById(R.id.nav_sel_0);
        nav_sel_1 = findViewById(R.id.nav_sel_1);
        nav_sel_2 = findViewById(R.id.nav_sel_2);
        nav_sel_3 = findViewById(R.id.nav_sel_3);
        nav_sel_4 = findViewById(R.id.nav_sel_4);
        // 创建TypedValue对象，用于获取主题中的属性值
        TypedValue typedValue = new TypedValue();
        // 获取当前Activity的主题
        Resources.Theme theme = this.getTheme();
        ImageView[] imageViews = {
                nav_0,
                nav_1,
                nav_2,
                nav_3,
                nav_4
        };

        ImageView[] imageViewsB = {
                nav_sel_0,
                nav_sel_1,
                nav_sel_2,
                nav_sel_3,
                nav_sel_4
        };

        int[] selAttributeResIds = {
                R.attr.nav_0_sel,
                R.attr.nav_1_sel,
                R.attr.nav_2_sel,
                R.attr.nav_3_sel,
                R.attr.nav_4_sel
        };

        int[] unselAttributeResIds = {
                R.attr.nav_0_unsel,
                R.attr.nav_1_unsel,
                R.attr.nav_2_unsel,
                R.attr.nav_3_unsel,
                R.attr.nav_4_unsel
        };

        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < imageViews.length; j++) {
                        theme.resolveAttribute(unselAttributeResIds[j], typedValue, true);
                        imageViews[j].setImageResource(typedValue.resourceId);
                    }

                    theme.resolveAttribute(selAttributeResIds[index], typedValue, true);
                    imageViews[index].setImageResource(typedValue.resourceId);
                    dataExchange.setNavSelected(index);
                    Log.e("Set", String.valueOf(dataExchange.getNavSelected()));
                    RIMTUtil.ShowToast(getApplicationContext(), "游戏名:" + gameList.get(dataExchange.getGameSelected()) + "\n导航名:" + (NavController.getNavList().get(dataExchange.getNavSelected())));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_controller, fragments.get(index))
                            .commit();

                    // 设置ImageView可见性
                    for (int k = 0; k < imageViewsB.length; k++) {
                        if (k == index) {
                            imageViewsB[k].setVisibility(View.VISIBLE);
                        } else {
                            imageViewsB[k].setVisibility(View.INVISIBLE);
                        }
                    }

                    // 根据导航索引执行对应的操作
                    switch (index) {
                        case 0:
                            // 第一个导航的点击
                            // 添加您想要执行的逻辑
                            break;
                        case 1:
                            // 第二个导航的点击
                            // 添加您想要执行的逻辑
                            break;
                        case 2:
                            // 第三个导航的点击
                            // 添加您想要执行的逻辑
                            break;
                        case 3:
                            // 第四个导航的点击
                            // 添加您想要执行的逻辑
                            break;
                        case 4:
                            // 第五个导航的点击
                            // 添加您想要执行的逻辑
                            break;
                    }
                }
            });
        }
    }

    private void SetUser() {
        ImageView drawer_user_avatar = findViewById(R.id.drawer_user_avatar);
        TextView drawer_user_username = findViewById(R.id.drawer_user_username);
        if (getUser(getApplicationContext(), "isLogined").equals("1")) {
            Bitmap Avatar = FileUtil.decodeSampleBitmapFromPath(FileUtil.getPackageDataDir(getApplicationContext()) + "/userdata/avatar.png", 128, 128);
            Glide.with(this).load(Avatar).into(drawer_user_avatar);
            drawer_user_username.setText(getUser(getApplicationContext(), "displayname"));
        } else {
            drawer_user_avatar.setImageDrawable(getDrawable(R.color.black));
            drawer_user_username.setText("登陆");
        }
    }

    // 设置侧边栏操作
    private void SetDrawer() {
        // 获取自动更新设置的布局、文本和状态文本
        View Drawer_Settings_AutoUpdate_Layout = findViewById(R.id.drawer_settings_autoupdate_layout);
        TextView Drawer_Settings_AutoUpdate_Text = findViewById(R.id.drawer_settings_autoupdate_text);
        TextView Drawer_Settings_AutoUpdate_Status = findViewById(R.id.drawer_settings_autoupdate_status);
        TextView Drawer_User_Logout = findViewById(R.id.drawer_user_logout);
        LinearLayout Drawer_User_Login_Layout = findViewById(R.id.drawer_user_login);
        TextView Drawer_User_Username = findViewById(R.id.drawer_user_username);

        // 根据自动更新设置的值，设置相应的布局、文本和状态文本视图
        toolboxCheck(dataExchange.getAutoUpdateStatus(getApplicationContext()), Drawer_Settings_AutoUpdate_Layout, Drawer_Settings_AutoUpdate_Text, Drawer_Settings_AutoUpdate_Status);

        // 给自动更新设置添加点击事件监听器
        Drawer_Settings_AutoUpdate_Layout.setOnClickListener(view -> {
            if (dataExchange.getAutoUpdateStatus(getApplicationContext()) == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("取消更新检测");
                builder.setMessage("自动更新检测可以让您获得最新的 app 版本，您真的要取消吗？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataExchange.toggleAutoUpdate(getApplicationContext());
                        // 根据更新后的自动更新设置的值，重新设置相应的布局、文本和状态文本视图
                        toolboxCheck(dataExchange.getAutoUpdateStatus(getApplicationContext()), Drawer_Settings_AutoUpdate_Layout, Drawer_Settings_AutoUpdate_Text, Drawer_Settings_AutoUpdate_Status);
                        // 在日志中输出自动更新设置的值
                        Log.e("S", "AutoUpdate=" + dataExchange.getAutoUpdateStatus(getApplicationContext()));
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 关闭提示窗
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                dataExchange.toggleAutoUpdate(getApplicationContext());
                // 根据更新后的自动更新设置的值，重新设置相应的布局、文本和状态文本视图
                toolboxCheck(dataExchange.getAutoUpdateStatus(getApplicationContext()), Drawer_Settings_AutoUpdate_Layout, Drawer_Settings_AutoUpdate_Text, Drawer_Settings_AutoUpdate_Status);
            }
        });

        Drawer_User_Username.setOnClickListener(view -> {
            if (!getUser(getApplicationContext(), "isLogined").equals("1")) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Drawer_User_Logout.setOnClickListener(view -> {
            if (getUser(getApplicationContext(), "isLogined").equals("1")) {
                FlarumUserUtil.Logout(getApplicationContext());
                SetUser();
            }
        });
        if (getUser(getApplicationContext(), "isLogined").equals("0")) {
            Drawer_User_Logout.setVisibility(View.GONE);
        }

        gameSelect();

        //暂时加个切换日间与夜间
        SwitchMaterial checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(view -> {
            if (checkbox.isChecked())
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void gameSelect() {
        View.OnClickListener gameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = view.getContext().getTheme();
                int clickedIndex = 1;
                for (int i = 0; i < drawerGames.length; i++) {
                    if (drawerGames[i] == view) {
                        clickedIndex = i;
                        break;
                    }
                }
                theme.resolveAttribute(R.attr.drawer_sel, typedValue, true);
                drawerGamesSel[clickedIndex].setImageResource(typedValue.resourceId);
                theme.resolveAttribute(R.attr.drawer_unsel, typedValue, true);
                drawerGamesSel[1 - clickedIndex].setImageResource(typedValue.resourceId);
                // 处理选择游戏后的代码
                handleGameClick(clickedIndex);
            }
        };
        for (LinearLayout drawerGame : drawerGames) {
            drawerGame.setOnClickListener(gameClickListener);
        }
    }

    // 处理选择游戏后的代码
    private void handleGameClick(int clickedIndex) {
        // 添加点击事件代码
        dataExchange.setGameSelected(clickedIndex);
        RIMTUtil.ShowToast(getApplicationContext(), "游戏名:" + gameList.get(dataExchange.getGameSelected()) + "\n导航名:" + (NavController.getNavList().get(dataExchange.getNavSelected())));
    }

    // 设置工具箱的开关颜色
    private void toolboxCheck(int status, View layout, TextView text, TextView statusText) {
        // 创建TypedValue对象，用于获取主题中的属性值
        TypedValue typedValue = new TypedValue();
        // 获取当前Activity的主题
        Resources.Theme theme = this.getTheme();
        // 根据选择状态不同设置背景色
        int colorBackgroundAttr = status == 0 ? R.attr.drawer_toolbox_unchecked_background : R.attr.drawer_toolbox_checked_background;
        int colorTextAttr = status == 0 ? R.attr.drawer_toolbox_unchecked_text_color : R.attr.drawer_toolbox_checked_text_color;
        // 解析主题中的属性值将结果存储在 typedValue 对象中
        theme.resolveAttribute(colorTextAttr, typedValue, true);
        // 从 typedValue 对象中获取文本颜色值
        int colorText = typedValue.data;
        // 根据选择状态不同设置字符串
        int statusResource = status == 0 ? R.string.general_off : R.string.general_on;
        // 解析主题中的属性值，并将结果存储在typedValue对象中
        theme.resolveAttribute(colorBackgroundAttr, typedValue, true);
        // 从typedValue中获取背景色值
        int colorBackground = typedValue.data;
        // 设置布局的背景色
        layout.setBackgroundColor(colorBackground);
        // 设置文本的颜色
        text.setTextColor(colorText);
        // 设置状态文本的文本值
        statusText.setText(statusResource);
    }

    // 打开侧边栏
    public void openDrawer() {
        // 找到 DrawerLayout 和自定义的侧滑栏布局
        DrawerLayout = findViewById(R.id.drawer_layout);
        // 设置侧滑栏打开和关闭的监听器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, DrawerLayout, null, 0, 0);
        DrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        DrawerLayout.openDrawer(GravityCompat.START);
    }

    // 设置状态栏颜色
    private void setStatusBarColor() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // 夜间模式已启用
            getWindow().setStatusBarColor(Color.parseColor("#1E1B28"));
        } else {
            // 日间模式已启用
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        SetUser();

    }

}
