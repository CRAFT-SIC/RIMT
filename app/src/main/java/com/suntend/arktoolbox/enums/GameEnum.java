package com.suntend.arktoolbox.enums;

import java.io.Serializable;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
public enum GameEnum implements Serializable {
    ark("ark", "明日方舟"), back1919("1919", "重返1919");

    //程序代码使用
    public final String key;
    //对外展示
    public final String label;

    GameEnum(String key, String name) {
        this.key = key;
        this.label = name;
    }
}
