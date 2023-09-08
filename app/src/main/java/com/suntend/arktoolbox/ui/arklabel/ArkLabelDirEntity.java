package com.suntend.arktoolbox.ui.arklabel;

import com.suntend.arktoolbox.enums.GameEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
public class ArkLabelDirEntity implements Serializable {
    public int id;
    public boolean isExpand = false;//是否展开
    public String name;
    public GameEnum game = GameEnum.values()[0];
    public List<ArkLabelEntity> labels = new ArrayList<>();
    public List<ArkLabelDirEntity> dirs = new ArrayList<>();// 子文件夹
}
