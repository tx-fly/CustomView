package com.sunofbeach.customview.viewgroup.keypad;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sunofbeach.customview.R;

public class KeyPadActivity extends AppCompatActivity {

    private final static String TAG = "KeyPadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_pad);
        KeyPadView keyPadView = findViewById(R.id.myKeyPadView);
        keyPadView.setOnNumberClickListener(new KeyPadView.OnNumberClickListener() {
            @Override
            public void onNumberClick(View v, String number) {
                Log.d(TAG, "onNumberClick-->"+number);
            }
        });

    }
}