package com.sunofbeach.customview.loginkeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sunofbeach.customview.App;
import com.sunofbeach.customview.R;

public class LoginActivity extends AppCompatActivity {

    private TextView mTitle;
    private final static String TAG = "LoginActivity";
    private LoginPageView mMyLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏，不要主题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mMyLogin = findViewById(R.id.my_login);
        mMyLogin.setOnLoginPageActionListener(new LoginPageView.LoginPageActionListener() {
            @Override
            public void onGetVerifyCodeClick(String phone) {
                Log.d(TAG, "需要获取验证码的手机号为："+phone);
            }

            @Override
            public void onOpenAgreementClick() {
                Log.d(TAG, "已经勾选协议");
            }

            @Override
            public void onConfirmClick(String VerifyCode, String phoneNum) {

                Log.d(TAG, "手机号码为："+phoneNum);
                Log.d(TAG, "验证码为："+VerifyCode);
                App.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        mMyLogin.onVerifyCodeError();
                    }
                }, 3000);

            }

        });

    }
}//        Drawable icon = getResources().getDrawable(R.mipmap.user);
//        mTitle = findViewById(R.id.title);
//setBounds(left,top,right,bottom)里的参数从左到右分别是
//drawable的左边到textview左边缘+padding的距离，drawable的上边离textview上边缘+padding的距离
//drawable的右边边离textview左边缘+padding的距离，drawable的下边离textview上边缘+padding的距离
//所以right-left = drawable的宽，top - bottom = drawable的高
//        icon.setBounds(0,0,40,44);
//        mTitle.setCompoundDrawables(icon, null, null, null);
//        int height = icon.getIntrinsicHeight();
//        int minimumHeight = icon.getMinimumHeight();
//        Log.d(TAG, "Height-->"+height);
//        Log.d(TAG, "minimumHeight-->"+minimumHeight);