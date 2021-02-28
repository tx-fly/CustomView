package com.sunofbeach.customview.loginkeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sunofbeach.customview.R;

import java.lang.reflect.Field;

public class LoginPageView extends FrameLayout {

    //手机号码的规则
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    private static final int SIZE_VERIFY_CODE_DEFAULT = 4;
    private int mColor;
    private int mVerifyCodeSize = SIZE_VERIFY_CODE_DEFAULT;
    private CheckBox mCheckBox;
    private EditText mEdSize;
    private LoginPageActionListener mActionListener = null;
    private final static String TAG = "LoginPageView";
    private LoginKeyBoardView.onKeyPressListener mOnKeyPressListener = null;
    private LoginKeyBoardView mMKeyBoardNumber = null;
    private EditText mPhoneNum;
    //状态变化
    private Boolean isPhoneNumOk = false;
    private Boolean isAgreementOk = false;
    private Boolean isVerifyCodeOk = false;
    private TextView mBtnVerifyCode;
    private TextView mBtnLogin;

    private int mTotalTimer;//属性
    private int dTime = 1000;
    private static final int total = 1000 * 60;
    private int restTime;
    private Handler mHandler = null;
    private Boolean isCountDown = false;
    private int TOTAL_DEFAULT = 60*1000;
    private CountDownTimer mCountDownTimer = null;

    //用Handler实现倒计时
/*    private void startCountDown() {
        mHandler = App.getHandler();
        if (mHandler == null) {
            Log.d(TAG, "mHandler-->null");
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    restTime -= dTime;
                    if (restTime > 0) {
                        if (!isCountDown) {
                            isCountDown = true;
                            updateAllBtnState();
                        }
                        Log.d(TAG, "restTime-->" + restTime);
                        mBtnVerifyCode.setText("(" + restTime / 1000 + ")");
                        mHandler.postDelayed(this, 1000);
                    } else {
                        restTime = total;
                        isCountDown = false;
                        mBtnVerifyCode.setText("获取验证码");
                        updateAllBtnState();
                    }
                } else {
                    Log.d(TAG, "mHandler-->null");

                }
            }
        });
    }*/

    //用countDownTimer实现倒计时
    private void beginCountDownTimer(){
        Log.d(TAG, "beginCountDownTimer-->");
        restTime = mTotalTimer;
        Log.d(TAG, "restTime-->" + restTime);
        mCountDownTimer = new CountDownTimer(mTotalTimer, dTime) {
            public void onTick(long millisUntilFinished) {
                restTime -= dTime;
                if (!isCountDown) {
                    isCountDown = true;
                    updateAllBtnState();
                }
                Log.d(TAG, "restTime-->" + restTime);
                mBtnVerifyCode.setText("(" + restTime / 1000 + ")");
                mBtnVerifyCode.setTextSize(30);
            }

            public void onFinish() {
                restTime = mTotalTimer;
                isCountDown = false;
                mBtnVerifyCode.setTextSize(20);
                mBtnVerifyCode.setText("获取验证码");
                updateAllBtnState();
            }
        }.start();
    }

    public void onVerifyCodeError(){
        //验证码错误则清空
        mEdSize.getText().clear();
        //停止倒计时
        if (mCountDownTimer!=null) {
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoginPageView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoginPageView(Context context, @Nullable AttributeSet attrs) {

        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoginPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        getAttrs(context, attrs);
        //初始化控件
        initView(context);
        //禁止键盘弹起
        disableEdtFocusKeypad();
        //处理事件
        initEvent();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void disableEdtFocusKeypad() {
        //默认是获取到焦点后会自动弹出键盘，需设置成false
        mEdSize.setShowSoftInputOnFocus(false);
        mPhoneNum.setShowSoftInputOnFocus(false);
    }

    /**
     * 状态更新
     */
    private void updateAllBtnState() {
        if (!isCountDown) {
            mBtnVerifyCode.setEnabled(isPhoneNumOk);
        } else {
            mBtnVerifyCode.setEnabled(false);
        }
        mBtnLogin.setEnabled(isPhoneNumOk && isAgreementOk && isVerifyCodeOk);
    }

    /**
     * 事件处理
     */
    private void initEvent() {
        ///////////////////数字键盘点击事件
        mOnKeyPressListener = new LoginKeyBoardView.onKeyPressListener() {
            @Override
            public void onNumberPress(int number) {
                //数字被点击
                //插入数字
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    Editable text = focusEdt.getText();
                    int index = focusEdt.getSelectionEnd();
                    text.insert(index, String.valueOf(number));
                }
                Log.d(TAG, "onNumberPress-->" + number);
            }

            @Override
            public void onBackPress() {
                //退格键被点击
                EditText focusEdt = getFocusEdt();
                if (focusEdt != null) {
                    Editable text = focusEdt.getText();
                    int index = focusEdt.getSelectionEnd();
                    if (index > 0) {
                        text.delete(index - 1, index);
                    }
                }
                Log.d(TAG, "onBackPress-->");
            }
        };

        if (mMKeyBoardNumber != null && mOnKeyPressListener != null) {
            mMKeyBoardNumber.setOnKeyPressListener(mOnKeyPressListener);
        } else {
            Log.d(TAG, "都是空-->null");
        }
        ///////////监听电话号码改变
        mPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNum = s.toString();
                boolean matches = phoneNum.matches(REGEX_MOBILE_EXACT);
                isPhoneNumOk = phoneNum.length() == 11 && matches;
                updateAllBtnState();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ///////////监听是否勾选协议
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mActionListener!=null&&isChecked) {
                    mActionListener.onOpenAgreementClick();
                }
                isAgreementOk = isChecked;
                updateAllBtnState();
            }
        });
        //////////监听验证码是否正确
        mEdSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String VerifyCodeNum = s.toString();
                isVerifyCodeOk = VerifyCodeNum.length() == 4;
                updateAllBtnState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //登录按钮点击事件
        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    String phone = mPhoneNum.getText().toString().trim();
                    String VerifyCode = mEdSize.getText().toString().trim();
                    mActionListener.onConfirmClick(VerifyCode,phone);
                }else {
                    Log.d(TAG, "mActionListener为空-->");
                }
                Log.d(TAG, "已点击登录-->");
            }
        });
        //获取验证码按钮点击事件
        mBtnVerifyCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneNum.getText().toString().trim();
                if (mActionListener != null) {
                    mActionListener.onGetVerifyCodeClick(phone);
                    beginCountDownTimer();
                } else {
                    Log.d(TAG, "mActionListener为空-->");
                }
            }
        });

    }

    private void getAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoginPageView);

        mColor = a.getColor(R.styleable.LoginPageView_mainColor, -1);
        mVerifyCodeSize = a.getInt(R.styleable.LoginPageView_verifyCodeSize, SIZE_VERIFY_CODE_DEFAULT);
        mTotalTimer = a.getInt(R.styleable.LoginPageView_total,TOTAL_DEFAULT );

        Log.d(TAG, "mTotalTimer-->"+mTotalTimer);
        a.recycle();
    }

    private void initView(Context context) {

        //填充布局，才能发现控件
        LayoutInflater.from(context).inflate(R.layout.login_page_view, this, true);
        //设置颜色
        if (mColor != -1) {
            mCheckBox.setTextColor(mColor);
        }
        //更新size
        if (SIZE_VERIFY_CODE_DEFAULT != mVerifyCodeSize) {
            //设置edit最大长度，Verify查证
            mEdSize.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mVerifyCodeSize)});
        }
        //数字键盘
        mMKeyBoardNumber = findViewById(R.id.keyboardnumber);
        //协议选择框
        mCheckBox = findViewById(R.id.report_check_box);
        //验证码编辑框
        mEdSize = findViewById(R.id.verify_code_size);
        //手机号编辑框
        mPhoneNum = findViewById(R.id.phone_number);

        //验证码点击按钮
        mBtnVerifyCode = findViewById(R.id.check_number);
        //登录按钮
        mBtnLogin = findViewById(R.id.btn_login);
        //禁止复制粘贴代码
        disableCopyAndPaste(mPhoneNum);
        disableCopyAndPaste(mEdSize);

        //让手机号编辑框具有，默认焦点
        mPhoneNum.requestFocus();

        updateAllBtnState();
    }

    public void setOnLoginPageActionListener(LoginPageActionListener listener) {
        mActionListener = listener;
    }


    /**
     * 暴露接口
     */
    public interface LoginPageActionListener {

        void onGetVerifyCodeClick(String phone);

        void onOpenAgreementClick();

        void onConfirmClick(String VerifyCode, String phoneNum);

    }

    /**
     * 获取当前有焦点的输入框
     * 注意判null
     *
     * @return
     */
    private EditText getFocusEdt() {
        View view = this.findFocus();
        if (view instanceof EditText) {
            return (EditText) view;
        }
        return null;
    }


    /**
     * 生成get和set方法
     */
    public int getMainColor() {
        return mColor;
    }

    public void setMainColor(int color) {
        mColor = color;
    }

    public int getVerifyCodeSize() {
        return mVerifyCodeSize;
    }

    public void setVerifyCodeSize(int verifyCodeSize) {
        mVerifyCodeSize = verifyCodeSize;
    }

    public int getTotalTimer() {
        return mTotalTimer;
    }

    public void setTotalTimer(int totalTimer) {
        mTotalTimer = totalTimer;
    }

    /**
     * 以下为禁止edittext复制粘贴代码
     *
     * @param editText
     */
    @SuppressLint("ClickableViewAccessibility")
    public void disableCopyAndPaste(final EditText editText) {
        try {
            if (editText == null) {
                return;
            }

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            editText.setLongClickable(false);
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // setInsertionDisabled when user touches the view
                        setInsertionDisabled(editText);
                    }

                    return false;
                }
            });
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInsertionDisabled(EditText editText) {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(editText);

            // if this view supports insertion handles
            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);

            // if this view supports selection handles
            Field mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled");
            mSelectionControllerEnabledField.setAccessible(true);
            mSelectionControllerEnabledField.set(editorObject, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
