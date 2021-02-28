package com.sunofbeach.customview.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sunofbeach.customview.R;
import com.sunofbeach.customview.customview.InputNumber;

public class InputNumberActivity extends AppCompatActivity implements InputNumber.OnNumberChangListener {

    private static final String TAG = "MainActivity";
    private InputNumber mInputNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_number);

        mInputNumber = findViewById(R.id.input_number);
        mInputNumber.setOnNumberChangListener(this);//接口实现

        int max = mInputNumber.getMax();
        float valueSize = mInputNumber.getValueSize();

        Log.d(TAG, "max-->"+max);
        Log.d(TAG, "valueSize-->"+valueSize);

    }

    @Override
    public void OnNumberChange(int value) {
        Log.d(TAG, "OnNumberChange-->"+value);
    }
}