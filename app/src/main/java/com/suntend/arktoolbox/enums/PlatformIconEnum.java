package com.suntend.arktoolbox.enums;

import com.suntend.arktoolbox.R;

import java.io.Serializable;

/**
 * Class:标签图标配置 website为关键配置
 * Other:
 * Create by jsji on  2023/8/5.
 */
public enum PlatformIconEnum implements Serializable {
    facebook("facebook", "www.facebook.com", R.attr.icon_platform_facebook),
    normal("normal", "normal", R.attr.icon_platform_normal);
    //对外展示
    public final String label;
    //程序比对：规则-》 startsWith("http://"+website)|| startsWith("https://"+website) 时比对成功，将使用iconAttr素材
    public final String website;
    //无法直接使用，需要通过AttrUtil.getResId读取实时内容
    public final int iconAttr;

    PlatformIconEnum(String label, String website, int iconAttr) {
        this.label = label;
        this.website = website;
        this.iconAttr = iconAttr;

    }
}
