package com.suntend.arktoolbox.RIMTUtil;


import com.suntend.arktoolbox.APP;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ConvertUtils {
    public static int dp2px(float dpValue) {
        final float scale = APP.currentActivity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int sp2px(float spValue) {
        float scaledDensity = APP.currentActivity.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

}
