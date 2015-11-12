package com.liuzhuangzhuang.circle.utils;

import android.content.Context;

/**
 * Created by liuzhuang on 15/11/11.
 */
public class Utils {
    /**
     * 根据图片名称获得资源id
     **/
    public static int getDrawIdByName(Context context, String drawName, String defaultName) {
        try {
            return context.getResources().getIdentifier(drawName, "drawable", context.getPackageName());
        } catch (Exception e) {
            return context.getResources().getIdentifier(defaultName, "drawable", context.getPackageName());
        }
    }
}
