package com.liuzhuangzhuang.circle.utils;

/**
 * Created by liuzhuang on 15/11/11.
 * 计算位置
 */
public class ComputeUtils {


    /**
     * 根据角度计算X坐标
     **/
    public static double computeX(int centerX, int radius, double angle) {
        return centerX + radius * Math.cos(angle * Math.PI / 180);
    }

    /**
     * 根据角度计算Y坐标
     **/
    public static double computeY(int centerY, int radius, double angle) {
        return centerY + radius * Math.sin(angle * Math.PI / 180);
    }
}
