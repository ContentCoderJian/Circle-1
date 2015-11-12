package com.liuzhuangzhuang.circle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by liuzhuang on 15/11/12.
 */
public class CircleViewInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_view_info);
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText(getIntent().getStringExtra("typeName"));
    }
}
