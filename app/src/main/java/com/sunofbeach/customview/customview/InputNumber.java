package com.sunofbeach.customview.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sunofbeach.customview.R;

public class InputNumber extends RelativeLayout {

    private static final String TAG = "InputNumber";
    private View mTextLeft;
    private View mTextRight;
    private EditText mEditInput;
    private int mCurrentNumber = 0;
    private OnNumberChangListener mListener;

    private int mMax;
    private int mMin;
    private int mStep;
    private int mDefaultValue;
    private boolean mDisable;
    private int mBackground;
    private float mValueSize;

    public InputNumber(Context context) {
        this(context, null);
    }

    public InputNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 统一入口
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public InputNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取相关属性
        getAttribute(context, attrs);

        //初始化控件
        initView(context);

        //处理事件
        setUpEvent();

        Log.d(TAG, "mMax-->" + mMax);
        Log.d(TAG, "mMin-->" + mMin);
        Log.d(TAG, "mStep-->" + mStep);
        Log.d(TAG, "mDefaultValue-->" + mDefaultValue);
        Log.d(TAG, "mDisable-->" + mDisable);
        Log.d(TAG, "mBackground-->" + mBackground);
        Log.d(TAG, "mValueSize-->" + mValueSize);
    }

    /**
     * 获取相关属性
     *
     * @param context
     * @param attrs
     */
    private void getAttribute(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputNumberView);

        mMax = a.getInt(R.styleable.InputNumberView_max, 0);
        mMin = a.getInt(R.styleable.InputNumberView_min, 0);
        mStep = a.getInt(R.styleable.InputNumberView_step, 1);
        mDefaultValue = a.getInt(R.styleable.InputNumberView_defaultValue, 1);
        mDisable = a.getBoolean(R.styleable.InputNumberView_disable, false);
        mBackground = a.getResourceId(R.styleable.InputNumberView_Background, -1);//负一代表没有资源
        mValueSize = a.getDimension(R.styleable.InputNumberView_valueSize, 10);

        a.recycle();

    }

    /**
     * 处理事件
     */
    private void setUpEvent() {

        mTextLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mTextRight.setEnabled(!mDisable);
                //设置步数
                mCurrentNumber -= mStep;
                if (mListener != null) {
                    mListener.OnNumberChange(mCurrentNumber);//调用接口
                }
                //设置最小值
                if (mMin != 0 && mCurrentNumber <= mMin) {
                    mCurrentNumber = mMin;
                    mTextLeft.setEnabled(mDisable);//置为禁用
                }
                upDataText();
            }
        });

        mTextRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mTextLeft.setEnabled(!mDisable);
                //设置步数
                mCurrentNumber += mStep;
                if (mListener != null) {
                    mListener.OnNumberChange(mCurrentNumber);//调用接口
                }
                //设置最大值
                if (mMax != 0 && mCurrentNumber >= mMax) {
                    mCurrentNumber = mMax;
                    mTextRight.setEnabled(mDisable);//置为禁用
                }
                upDataText();
            }
        });
    }

    /**
     * 初始化控件
     *
     * @param context
     */
    private void initView(Context context) {
        /**
         *  以下代码的功能同等,都是把view添加到当前容器里
         */
        //1.LayoutInflater.from(context).inflate(R.layout.input_number_view,this,true);
        //2.LayoutInflater.from(context).inflate(R.layout.input_number_view,this);
        //3.
        View view = LayoutInflater.from(context).inflate(R.layout.input_number_view, this,
                false);
        addView(view);

        //找到控件
        mTextLeft = findViewById(R.id.text_left);
        mTextRight = findViewById(R.id.text_right);
        mEditInput = findViewById(R.id.input_edit);

        //初始化控件的值
        //默认值
        mCurrentNumber = mDefaultValue;
        //字体大小
        mEditInput.setTextSize(mValueSize);
        upDataText();

    }

    /**
     * 更新控件
     */
    private void upDataText() {
        mEditInput.setText(String.valueOf(mCurrentNumber));
    }

    /**
     * 获取Number的值
     *
     * @return
     */
    public int getCurrentNumber() {
        return mCurrentNumber;
    }

    /**
     * 设置Number的值
     *
     * @param currentNumber
     */
    public void setCurrentNumber(int currentNumber) {
        mCurrentNumber = currentNumber;
    }

    /**
     * 接口回调
     *
     * @param listener
     */
    public void setOnNumberChangListener(OnNumberChangListener listener) {
        this.mListener = listener;
    }

    /**
     * 定义接口，目的是“暴露接口”步骤
     */
    public interface OnNumberChangListener {
        void OnNumberChange(int value);
    }


    /**
     * Set和get方法
     */
    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public int getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        mDefaultValue = defaultValue;
    }

    public boolean isDisable() {
        return mDisable;
    }

    public void setDisable(boolean disable) {
        mDisable = disable;
    }

//    @Override
//    public int getBackground() {
//        return mBackground;
//    }

    public void setBackground(int background) {
        mBackground = background;
    }

    public float getValueSize() {
        return mValueSize;
    }

    public void setValueSize(float valueSize) {
        mValueSize = valueSize;
    }
}
