package com.liuzhuangzhuang.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.liuzhuangzhuang.circle.utils.LogUtils;

/**
 * 解决滑动冲突
 */
public class CustomLinearLayout extends LinearLayout {
    private static final String TAG = LogUtils.makeLogTag(CustomLinearLayout.class.getSimpleName());

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 是否在父控件内
    private boolean isInBox(float currentX, float currentY, View v) {
        if (currentX > v.getRight() || currentX < v.getLeft()) {
            return false;
        }
        if (currentY < v.getTop() || currentY > v.getBottom()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSpecial) {
            /** 这个event有可能是从上面的父容器传来的 **/
            LogUtils.LOGD(TAG, "从父容器传来");
            float currentX = event.getX();
            float currentY = event.getY();

            View v = (View) getParent().getParent();

            if (!isInBox(currentX, currentY, v)) {
                // 在控件外
                LogUtils.LOGD(TAG, "在控件外");
                resetSelect();
                return false;
            }

            boolean result = super.onTouchEvent(event);
            LogUtils.LOGD(TAG, "return " + result);
            return result;
        } else {
            // 不处理事件，把事件交给父控件「CircleLayout」处理
            LogUtils.LOGD(TAG, "不处理事件，把事件交给父控件「CircleLayout」处理");
            setSelected(false);
            return false;
        }
    }

    public void resetSelect() {
        setSelected(false);
        setPressed(false);
        setSpecial(false);
    }

    private boolean isSpecial;

    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

}
