package com.sunofbeach.customview.loginkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sunofbeach.customview.R;

public class LoginKeyBoardActivity extends AppCompatActivity implements LoginKeyBoardView.onKeyPressListener {

    private final static String TAG = "LoginKeyBoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_key_board);

        LoginKeyBoardView loginKeyBoardView = findViewById(R.id.key_board);

        loginKeyBoardView.setOnKeyPressListener(this);

    }

    @Override
    public void onNumberPress(int number) {
        Log.d(TAG, "onNumberPress-->" + number);
    }

    @Override
    public void onBackPress() {
        Log.d(TAG, "onBackPress");
    }
}