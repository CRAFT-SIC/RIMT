package com.suntend.arktoolbox.RIMTUtil;
/*
 * JamXi
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.suntend.arktoolbox.MainActivity;

public class RIMTUtil {
    private static Toast toast;
    public static void ShowToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.cancel();
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        toast.show();
    }
}