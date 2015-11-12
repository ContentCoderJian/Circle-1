package com.liuzhuangzhuang.circle.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuzhuangzhuang.circle.R;
import com.liuzhuangzhuang.circle.CircleViewActivity;
import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liuzhuang on 15/11/11.
 * 小圆
 */
public class LittleCircle extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(LittleCircle.class.getSimpleName());

    @Bind(R.id.circle_little_ll)
    CustomLinearLayout circleLittleLl;
    @Bind(R.id.circle_little_tv)
    TextView circleLittleTv;
    @Bind(R.id.circle_little_icon)
    ImageView circleLittleIcon;

    public LittleCircle(Context context) {
        super(context);
        init();
    }

    public LittleCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LittleCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 添加子控件
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.circle_littel, this, true);
        ButterKnife.bind(this);
        bindListener();
    }

    // 设置文字
    public void setCircleLittleTv(String string) {
        this.circleLittleTv.setText(String.valueOf(string));
    }

    // 设置图标
    public void setCircleLittleIcon(int resId) {
        this.circleLittleIcon.setImageResource(resId);
    }

    // 设置界面显示数据
    public void setInterface(OrderType orderType) {
        setCircleLittleTv(orderType.getTypeName());
        setCircleLittleIcon(orderType.getImageResId());
    }

    /**
     * 设置点击事件
     */
    public void bindListener() {
        circleLittleLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoved) {
                    LogUtils.LOGD(TAG, "位置已经移动,不响应点击事件");
                    return;
                }
                dispatchIntent((OrderType) LittleCircle.this.getTag());
                LogUtils.LOGD(TAG, "LittleCircle Click");
            }
        });
    }

    // 控件是否移动过位置
    private boolean isMoved = false;

    public void setActionDown() {
        this.isMoved = false;
    }

    public void setMoved() {
        this.isMoved = true;
    }

    /**
     * 分发跳转
     */
    private void dispatchIntent(OrderType orderType) {
        if (orderType == null) {
            LogUtils.LOGD(TAG, "orderType == null");
            return;
        }
        Intent intent  = new Intent(getContext(), CircleViewActivity.class);
        getContext().startActivity(intent);
    }

    // 分发事件
    public boolean dispatchChildTouchEvent(MotionEvent event) {
        LogUtils.LOGD(TAG, "指派给子View执行");
        circleLittleLl.setSpecial(true);
        return circleLittleLl.onTouchEvent(event);
    }

    public void resetSelect() {
        circleLittleLl.resetSelect();
    }

}
