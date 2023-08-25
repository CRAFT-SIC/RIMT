package com.suntend.arktoolbox.ui.arklabel;

import com.suntend.arktoolbox.enums.GameEnum;
import com.suntend.arktoolbox.enums.PlatformIconEnum;

import java.io.Serializable;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
public class ArkLabelEntity implements Serializable {
    public int id;
    public int iconAttr = PlatformIconEnum.normal.iconAttr;
    public String name;
    public String url;
    public GameEnum game = GameEnum.values()[0];
}
