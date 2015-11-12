package com.liuzhuangzhuang.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuzhuangzhuang.circle.R;
import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liuzhuang on 15/11/11.
 * 中间圆
 */
public class CenterCircle extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(CenterCircle.class.getSimpleName());

    @Bind(R.id.circle_little_ll)
    LinearLayout circleLittleLl;
    @Bind(R.id.circle_little_icon)
    ImageView circleLittleIcon;
    @Bind(R.id.circle_little_tv)
    TextView circleLittleTv;

    private boolean isShow;

    public CenterCircle(Context context) {
        super(context);
        init();
    }

    public CenterCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenterCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.circle_center, this, true);
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

    public void bindListener() {
        circleLittleLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (centerClickListener == null) {
                    LogUtils.LOGD(TAG, "centerClickListener == null");
                    return;
                }
                centerClickListener.onClick();
            }
        });
    }

    private CircleLayout.CenterClickListener centerClickListener;

    public void setCenterClickListener(CircleLayout.CenterClickListener centerClickListener) {
        this.centerClickListener = centerClickListener;
    }
}
