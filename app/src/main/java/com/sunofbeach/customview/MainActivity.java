package com.sunofbeach.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sunofbeach.customview.customview.InputNumberActivity;
import com.sunofbeach.customview.loginkeyboard.LoginActivity;
import com.sunofbeach.customview.loginkeyboard.LoginKeyBoardActivity;
import com.sunofbeach.customview.view.watchfaceview.WatchFaceActivity;
import com.sunofbeach.customview.viewgroup.flowview.FlowLayoutActivity;
import com.sunofbeach.customview.viewgroup.keypad.KeyPadActivity;
import com.sunofbeach.customview.viewgroup.slidemenu.SlideMenuViewActivity;

public class MainActivity extends AppCompatActivity{

    private View mInputNumber;
    private View mKeyBoard;
    private View mLogin;
    private Button mFlowLayout;
    private View mKeyPadView;
    private Button mSlideMenuView;
    private Button mWatchFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        onClickListener();

    }
    private void initView() {
        mInputNumber = findViewById(R.id.inputNumber);
        mKeyBoard = findViewById(R.id.number_key_board);
        mLogin = findViewById(R.id.login);
        mFlowLayout = findViewById(R.id.flowLayout);
        mKeyPadView = findViewById(R.id.KeyPadView);
        mSlideMenuView = findViewById(R.id.SlideMenuView);
        mWatchFace = findViewById(R.id.WatchFaceView);
    }


    private void onClickListener() {

        myOnClickListener myOnClickListener = new myOnClickListener();

        mInputNumber.setOnClickListener(myOnClickListener);
        mKeyBoard.setOnClickListener(myOnClickListener);
        mLogin.setOnClickListener(myOnClickListener);
        mFlowLayout.setOnClickListener(myOnClickListener);
        mKeyPadView.setOnClickListener(myOnClickListener);
        mSlideMenuView.setOnClickListener(myOnClickListener);
        mWatchFace.setOnClickListener(myOnClickListener);
    }

    private class myOnClickListener implements View.OnClickListener {

        private Intent mIntent = null;

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.inputNumber:
                    mIntent = new Intent(MainActivity.this, InputNumberActivity.class);
                    break;
                case R.id.number_key_board:
                    mIntent = new Intent(MainActivity.this, LoginKeyBoardActivity.class);
                    break;
                case R.id.login:
                    mIntent = new Intent(MainActivity.this, LoginActivity.class);
                    break;
                case R.id.flowLayout:
                    mIntent = new Intent(MainActivity.this, FlowLayoutActivity.class);
                    break;
                case R.id.KeyPadView:
                    mIntent = new Intent(MainActivity.this, KeyPadActivity.class);
                    break;
                case R.id.SlideMenuView:
                    mIntent = new Intent(MainActivity.this, SlideMenuViewActivity.class);
                    break;
                case R.id.WatchFaceView:
                    mIntent = new Intent(MainActivity.this, WatchFaceActivity.class);
                    break;
            }

            if (mIntent!=null) {
                startActivity(mIntent);
            }
        }
    }

}