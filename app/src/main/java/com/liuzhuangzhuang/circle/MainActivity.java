package com.liuzhuangzhuang.circle;


import android.app.Activity;
import android.os.Bundle;

import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.utils.LogUtils;
import com.liuzhuangzhuang.circle.view.CircleLayout;

import java.util.ArrayList;

/**
 * Created by liuzhuang on 15/11/11.
 */
public class MainActivity extends Activity {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class.getSimpleName());

    private CircleLayout circleLayout;

    private OrderType orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleLayout = (CircleLayout) findViewById(R.id.circleLayout);
        fillData();
        initView();
    }

    // 填充虚拟数据
    private void fillData() {
        orderType = new OrderType();
        orderType.setParentTypeName("a");
        orderType.setOrderCategory("a");
        orderType.setTypeName("a");
        fillChildData(orderType);
    }

    private void fillChildData(OrderType type) {
        ArrayList<OrderType> orderTypes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            OrderType orderType = new OrderType();
            orderType.setParentTypeName(type.getTypeName());
            orderType.setTypeName(type.getTypeName() + "-" + i);
            orderTypes.add(orderType);
        }
        type.setChildren(orderTypes);
    }

    private void initView() {
        circleLayout.init(orderType, true);

        circleLayout.setCenterClickListener(new CircleLayout.CenterClickListener() {
            @Override
            public void onClick() {
                if (circleLayout.isShow()) {
                    LogUtils.LOGD(TAG, "隐藏");
                    circleLayout.hiddenWithAnimation();
                } else {
                    circleLayout.showWithAnimation();
                }
            }
        });
    }
}
