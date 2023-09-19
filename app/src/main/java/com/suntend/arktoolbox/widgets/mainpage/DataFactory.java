package com.suntend.arktoolbox.widgets.mainpage;

import android.graphics.Color;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.widgets.mainpage.beans.Card;
import com.suntend.arktoolbox.widgets.mainpage.beans.Theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created By nooly
 * class:DataFactory
 * package:com.suntend.arktoolbox.widgets.mainpage
 * 主页IP衍生的数据工厂
 * */
public class DataFactory {
    public static List<Card> getCardsData() {
        return Arrays.asList(
                new Card("塞壬唱片", "MonsterSirenRecords", "泰拉世界十世纪最大的音乐发行商之一。", R.drawable.ips1),
                new Card("塞壬唱片COPY", "MonsterSirenRecords", "泰拉世界十世纪最大的音乐发行商之一。", R.drawable.ips2));
    }

    public static List<Theme> getThemes() {
        return Arrays.asList(
                new Theme(Color.parseColor("#B4CD83"),"凯尔希",true, "null"),
                new Theme(Color.parseColor("#83DBEB"),"阿米娅",false, "null"),
                new Theme(Color.parseColor("#FF644C"),"W",false,"null"),
                new Theme(Color.parseColor("#FFAB50"),"霜星",false,"null")
        );
    }
}
