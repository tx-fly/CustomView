package com.sunofbeach.customview.loginkeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunofbeach.customview.R;


public class LoginKeyBoardView extends LinearLayout implements View.OnClickListener {

    private final String TAG = "LoginKeyBoard";
    private onKeyPressListener mkeyPressListener = null;

    public LoginKeyBoardView(Context context) {
        this(context, null);
    }

    public LoginKeyBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 统一入口
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public LoginKeyBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.num_key_pad, this, true);

        initView();

    }

    private void initView() {

        findViewById(R.id.number0).setOnClickListener(this);
        findViewById(R.id.number1).setOnClickListener(this);
        findViewById(R.id.number2).setOnClickListener(this);
        findViewById(R.id.number3).setOnClickListener(this);
        findViewById(R.id.number4).setOnClickListener(this);
        findViewById(R.id.number5).setOnClickListener(this);
        findViewById(R.id.number6).setOnClickListener(this);
        findViewById(R.id.number7).setOnClickListener(this);
        findViewById(R.id.number8).setOnClickListener(this);
        findViewById(R.id.number9).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    /**
     * 键盘点击处理
     * @param v
     */
    @Override
    public void onClick(View v) {

        int viewId = v.getId();

        if (mkeyPressListener == null) {
            Log.d(TAG, "mkeyPressListener is null");
            return;
        }
        if (viewId == R.id.back) {
            mkeyPressListener.onBackPress();
        } else {
            String text = (String) ((TextView) v).getText();
            mkeyPressListener.onNumberPress(Integer.parseInt(text));
        }

    }

    /**
     * 方法回调
     *
     * @param listener
     */
    public void setOnKeyPressListener(onKeyPressListener listener) {
        Log.d(TAG,"调用了setOnKeyPressListener");
        this.mkeyPressListener = listener;
        Log.d(TAG,"调用了setOnKeyPressListener");
    }

    /**
     * 定义接口
     */
    public interface onKeyPressListener {
        void onNumberPress(int number);

        void onBackPress();
    }
}
