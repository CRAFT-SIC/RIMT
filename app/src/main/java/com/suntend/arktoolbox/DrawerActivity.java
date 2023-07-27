package com.suntend.arktoolbox;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DrawerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        //对应侧边栏的Java，在此修改没有任何作用，请到MainActivity中修改SetDrawer()函数
    }
}