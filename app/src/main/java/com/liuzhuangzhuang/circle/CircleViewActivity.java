package com.liuzhuangzhuang.circle;

import android.app.Activity;
import android.os.Bundle;

import com.liuzhuangzhuang.circle.pojo.OrderType;
import com.liuzhuangzhuang.circle.view.CircleView;

import java.util.ArrayList;

public class CircleViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_view);
        CircleView circleView = (CircleView) findViewById(R.id.circleview);
        CircleView circleView2 = (CircleView) findViewById(R.id.circleview2);

        ArrayList<OrderType> orderTypes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            OrderType orderType = new OrderType();
            orderType.setParentTypeName("a" + i);
            orderType.setTypeName("b" + i);
            orderTypes.add(orderType);
        }

        circleView.init(this, orderTypes, CircleView.Mode.SINGLE);
        circleView2.init(this, orderTypes, CircleView.Mode.DOUBLE);
    }
}
