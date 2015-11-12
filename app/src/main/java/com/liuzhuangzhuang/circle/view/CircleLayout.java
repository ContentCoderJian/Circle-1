package com.liuzhuangzhuang.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.liuzhuangzhuang.circle.R;
import com.liuzhuangzhuang.circle.utils.ComputeUtils;
import com.liuzhuangzhuang.circle.utils.LogUtils;
import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.utils.ActivityUtils;
import com.liuzhuangzhuang.circle.utils.Calculate;
import com.liuzhuangzhuang.circle.utils.Utils;

import java.util.List;

/**
 * Created by liuzhuang on 15/11/11.
 */
public class CircleLayout extends ViewGroup {
    private static final String TAG = LogUtils.makeLogTag(CircleLayout.class.getSimpleName());

    private OrderType mOrderType;
    private List<OrderType> childOrderTypes;
    private boolean isShow;
    private Calculate calculate = new Calculate(this);


    private int centerX;
    private int centerY;
    private float degree;// 角度
    private int radius; // 二级分类圆占整体半径

    public CircleLayout(Context context) {
        super(context);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 初始化
    public void init(OrderType orderType, boolean isShow) {
        // 大圆数据源
        this.mOrderType = orderType;
        // 小圆数据源
        this.childOrderTypes = orderType.getChildren();
        this.isShow = isShow;
        this.radius = (ActivityUtils.dip2px(getContext(), 300) - ActivityUtils.dip2px(getContext(), 99)) / 2; // TODO 这是什么意思?
        initResData();
        addChildView();
    }

    // 设置图片资源Id
    private void initResData() {
        mOrderType.setImageResId(Utils.getDrawIdByName(getContext(), mOrderType.getImageName(), "ic_launcher"));
        for (int i = 0; i < childOrderTypes.size(); i++) {
            childOrderTypes.get(i).setImageResId(Utils.getDrawIdByName(getContext(), childOrderTypes.get(i).getImageName(), "ic_launcher"));
        }
    }

    // 添加子类控件
    private void addChildView() {
        // 添加中间圆
        CenterCircle centerCircle = new CenterCircle(getContext());
        centerCircle.setInterface(mOrderType);
        centerCircle.setTag(mOrderType);
        addView(centerCircle);

        // 添加小圆
        for (int i = 0; i < childOrderTypes.size(); i++) {
            LittleCircle littleCircle = new LittleCircle(getContext());
            littleCircle.setInterface(childOrderTypes.get(i));
            littleCircle.setTag(childOrderTypes.get(i));
            addView(littleCircle);
        }
    }


    /**
     * 计算子控件的位置
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // LogUtils.LOGD(TAG, "onMeasure");
        int layoutWidth = getMeasuredWidth();
        int layoutHeight = getMeasuredHeight();

        centerX = layoutWidth / 2;
        centerY = layoutHeight / 2;

        final int count = getChildCount();

        double eachAngel = 360 / (count - 1); // 除去中间的圆

        double angel = degree;

        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child instanceof CenterCircle) {
                lp.x = centerX - child.getMeasuredWidth() / 2;
                lp.y = centerY - child.getMeasuredHeight() / 2;
            } else if (child instanceof LittleCircle) {
                double x = ComputeUtils.computeX(centerX, radius, angel);
                double y = ComputeUtils.computeY(centerY, radius, angel);
                angel = angel + eachAngel;

                lp.x = (int) (x - child.getMeasuredWidth() / 2);
                lp.y = (int) (y - child.getMeasuredHeight() / 2);
            }
        }

        setMeasuredDimension(resolveSize(layoutWidth, widthMeasureSpec), resolveSize(layoutHeight, heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // LogUtils.LOGD(TAG, "onTouchEvent -- isShow : " + isShow);
        if (!isShow) {
            return false;
        }
        return calculate.onTouchEvent(event, this);
    }

    /**
     * 在这里设置中间大圆的位置，和四周小圆的位置
     **/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // LogUtils.LOGD(TAG, "onLayout");
        // 调整子控件的位置
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }

        if (isShow) {
            showWithOutAnimation();
            setBackgroundResource(R.drawable.layer_list_circle_bg);
        } else {
            hiddenWithOutAnimation();
            setBackgroundResource(0);
        }
    }

    public void requestLayout(float degree) {
        this.degree = degree;
        requestLayout();
    }

    /**
     * 保存圆的位置
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        int x; //子控件x坐标
        int y; //子控件y坐标

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        // LogUtils.LOGD(TAG, "checkLayoutParams");
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        // LogUtils.LOGD(TAG, "generateDefaultLayoutParams");
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        // LogUtils.LOGD(TAG, "generateLayoutParams(AttributeSet attrs)");
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        // LogUtils.LOGD(TAG, "generateLayoutParams(ViewGroup.LayoutParams p)");
        return new LayoutParams(p.width, p.height);
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public boolean isShow() {
        return isShow;
    }

    public interface CenterClickListener {
        void onClick();
    }

    // 设置中间大圆点击事件
    public void setCenterClickListener(CenterClickListener centerClickListener) {
        CenterCircle centerCircle;
        if ((centerCircle = getCenterView()) != null) {
            centerCircle.setCenterClickListener(centerClickListener);
        }
    }

    private CenterCircle getCenterView() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof CenterCircle) {
                CenterCircle centerCircle = (CenterCircle) child;
                return centerCircle;
            }
        }
        return null;
    }

    // 展开缩放动画
    public void showWithAnimation() {
        show(true);
    }

    public void showWithOutAnimation() {
        show(false);
    }

    // 显示
    private void show(boolean isNeedAnimation) {
        View centerView = getCenterView();
        if (centerView == null) {
            return;
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof LittleCircle) {
                child.setVisibility(View.VISIBLE);
                if (isNeedAnimation) {
                    animationOut(child, centerView);
                }
            }
        }
        if (isNeedAnimation) {
            if (getBackground() == null) {
                animationbgOut(centerView);
            }
        }
        isShow = true;
    }

    public void hiddenWithAnimation() {
        hidden(true);
    }

    public void hiddenWithOutAnimation() {
        hidden(false);
    }

    // 隐藏
    private void hidden(boolean isNeedAnimation) {
        View centerView = getCenterView();
        if (centerView == null) {
            return;
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof CenterCircle) {
                child.setVisibility(View.VISIBLE);
            } else {
                if (child.getVisibility() == View.VISIBLE) {
                    child.setVisibility(View.GONE);
                    if (isNeedAnimation) {
                        LogUtils.LOGD(TAG, "开始小圆动画>>>>>>");
                        animationIn(child, centerView);
                    }
                }
            }
        }

        if (isNeedAnimation) {
            if (getBackground() != null) {
                LogUtils.LOGD(TAG, "开始大圆背景动画>>>>>>");
                animationbgIn(centerView);
            }
        }
        isShow = false;
    }

    // 背景动画
    private void animationbgOut(View centerView) {
        // 0.0表示收缩到没有
        // 1.0表示正常无伸缩
        // 值小于1.0表示收缩
        // 值大于1.0表示放大

        int width = getWidth();
        int centerWidth = centerView.getRight() - centerView.getLeft();

        float fromX = (1.0f - (float) centerWidth / (float) width) / 2;
        float toX = 1.0f;
        float fromY = (1.0f - (float) centerWidth / (float) width) / 2; // x y 轴 缩放比例一样
        float toY = 1.0f;

        // LogUtils.LOGD(TAG, "toX " + toX);
        // LogUtils.LOGD(TAG, "toY " + toY);

        int pivotXType = Animation.RELATIVE_TO_SELF;
        float pivotXValue = 0.5f;
        int pivotYType = Animation.RELATIVE_TO_SELF;
        float pivotYValue = 0.5f;
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
        scaleAnimation.setDuration(300);
        this.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setBackgroundResource(R.drawable.layer_list_circle_bg);
            }
        });

    }

    // 背景动画
    private void animationbgIn(View centerView) {
        // 0.0表示收缩到没有
        // 1.0表示正常无伸缩
        // 值小于1.0表示收缩
        // 值大于1.0表示放大

        int width = getWidth();
        int centerWidth = centerView.getRight() - centerView.getLeft();

        float fromX = 1.0f;
        float toX = (1.0f - (float) centerWidth / (float) width) / 2;
        float fromY = 1.0f;
        float toY = (1.0f - (float) centerWidth / (float) width) / 2; // x y 轴 缩放比例一样

        // LogUtils.LOGD(TAG, "toX " + toX);
        // LogUtils.LOGD(TAG, "toY " + toY);

        int pivotXType = Animation.RELATIVE_TO_SELF;
        float pivotXValue = 0.5f;
        int pivotYType = Animation.RELATIVE_TO_SELF;
        float pivotYValue = 0.5f;
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
        scaleAnimation.setDuration(300);
        this.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setBackgroundResource(0);
                setSelected(true);
            }
        });

    }

    // 小圆往外移
    private void animationOut(View view, View centerView) {
        if (view == null || centerView == null) {
            return;
        }

        int width = view.getRight() - view.getLeft();
        int height = view.getBottom() - view.getTop();

        float centerX = (centerView.getRight() - centerView.getLeft()) / 2 + centerView.getLeft() - width / 2;
        float centerY = (centerView.getBottom() - centerView.getTop()) / 2 + centerView.getTop() - height / 2;

        // LogUtils.LOGD(TAG, "-----------------");
        // LogUtils.LOGD(TAG, "centerView " + centerView.getTag());
        // LogUtils.LOGD(TAG, "width : " + width);
        // LogUtils.LOGD(TAG, "height : " + height);
        // LogUtils.LOGD(TAG, "centerX : " + centerX);
        // LogUtils.LOGD(TAG, "centerY : " + centerY);

        int fromXType = Animation.RELATIVE_TO_SELF;
        float fromXValue = (centerX - view.getLeft()) / width;
        int toXType = Animation.RELATIVE_TO_SELF;
        float toXValue = 0;
        int fromYType = Animation.RELATIVE_TO_SELF;
        float fromYValue = (centerY - view.getTop()) / height;
        int toYType = Animation.RELATIVE_TO_SELF;
        float toYValue = 0;

        // LogUtils.LOGD(TAG, "fromXValue : " + fromXValue);
        // LogUtils.LOGD(TAG, "toXValue : " + toXValue);
        // LogUtils.LOGD(TAG, "fromYValue : " + fromYValue);
        // LogUtils.LOGD(TAG, "toYValue : " + toYValue);

        TranslateAnimation translateAnimation = new TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);
    }

    // 小圆往里移
    private void animationIn(View view, View centerView) {
        if (view == null || centerView == null) {
            return;
        }

        int width = view.getRight() - view.getLeft();
        int height = view.getBottom() - view.getTop();

        float centerX = (centerView.getRight() - centerView.getLeft()) / 2 + centerView.getLeft() - width / 2;
        float centerY = (centerView.getBottom() - centerView.getTop()) / 2 + centerView.getTop() - height / 2;

        // LogUtils.LOGD(TAG, "-----------------");
        // LogUtils.LOGD(TAG, "centerView " + centerView.getTag());
        // LogUtils.LOGD(TAG, "width : " + width);
        // LogUtils.LOGD(TAG, "height : " + height);
        // LogUtils.LOGD(TAG, "centerX : " + centerX);
        // LogUtils.LOGD(TAG, "centerY : " + centerY);

        int fromXType = Animation.RELATIVE_TO_SELF;
        float fromXValue = 0;
        int toXType = Animation.RELATIVE_TO_SELF;
        float toXValue = (centerX - view.getLeft()) / width;
        int fromYType = Animation.RELATIVE_TO_SELF;
        float fromYValue = 0;
        int toYType = Animation.RELATIVE_TO_SELF;
        float toYValue = (centerY - view.getTop()) / height;

        // LogUtils.LOGD(TAG, "fromXValue : " + fromXValue);
        // LogUtils.LOGD(TAG, "toXValue : " + toXValue);
        // LogUtils.LOGD(TAG, "fromYValue : " + fromYValue);
        // LogUtils.LOGD(TAG, "toYValue : " + toYValue);

        TranslateAnimation translateAnimation = new TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);
    }
}
