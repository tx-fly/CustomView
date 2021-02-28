package com.sunofbeach.customview.viewgroup.flowview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sunofbeach.customview.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayoutActivity extends AppCompatActivity {

    private FlowView mFlowView;
    private final static String TAG = "FlowLayoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        mFlowView = findViewById(R.id.flowView);
        List<String> data = new ArrayList<>();
        data.add("亚瑟王");
        data.add("斯卡哈");
        data.add("冲田总司帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅帅");
        data.add("玉藻前");
        data.add("梅林");
        data.add("超人熊");
        data.add("伽摩");
        data.add("牛若丸");
        data.add("吉尔伽美什");
        data.add("佐佐木小次郎");
        data.add("BB");
        mFlowView.setTestList(data);
        mFlowView.setOnItemClickListener(new FlowView.onItemClickListener() {
            @Override
            public void onItemClick(View v, String s) {
                Log.d(TAG, "是谁？-->"+s);
                TextView textView = (TextView) v;
                textView.setTextColor(android.graphics.Color.RED);
            }
        });
    }
}