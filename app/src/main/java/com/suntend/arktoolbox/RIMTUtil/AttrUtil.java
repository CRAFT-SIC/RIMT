package com.suntend.arktoolbox.RIMTUtil;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.suntend.arktoolbox.APP;


/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class AttrUtil {
   public static int getColor(int attr){
        Resources.Theme theme = APP.currentActivity.getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(attr, value, true);
        return value.data;
    }
    public static int getResId(int attr){
        Resources.Theme theme = APP.currentActivity.getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(attr, value, true);
        return value.resourceId;
    }
    public static int getDrawableId( String defType) {
        return APP.currentActivity.getResources().getIdentifier(defType, "drawable", APP.currentActivity.getPackageName());
    }
}
