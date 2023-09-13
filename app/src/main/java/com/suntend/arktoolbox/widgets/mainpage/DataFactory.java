package com.suntend.arktoolbox.widgets.mainpage;

import com.suntend.arktoolbox.widgets.mainpage.beans.Card;

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
        return Arrays.asList(new Card("塞壬唱片", "MonsterSirenRecords", "泰拉世界十世纪最大的音乐发行商之一。", ""));
    }
}
