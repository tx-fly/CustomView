package com.sunofbeach.customview.viewgroup.keypad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sunofbeach.customview.R;
import com.sunofbeach.customview.utils.SizeUtils;

public class KeyPadView extends ViewGroup {

    private int mNumberColor;
    private int mNumberSize;
    private int mItemNormalBg;
    private int mItemPressBg;
    private final static String TAG = "KeyPadView";
    private final static int DEFAULT_MARGIN = SizeUtils.dip2px(5);
    private int row = 4;
    private int column = 3;
    private int mItemMargin;
    private OnNumberClickListener mListener = null;

    public KeyPadView(Context context) {
        this(context, null);
    }

    public KeyPadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyPadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        initAttrs(context, attrs);
        //添加子view，且设置样式
        setUpItem();
    }

    private void setUpItem() {
        //先移除所以View
        removeAllViews();
        for (int i = 0; i < 11; i++) {
            TextView item = new TextView(getContext());
            //内容
            if (i == 10) {
                item.setTag(true);
                item.setText("=");
            } else {
                item.setTag(false);
                if (i == 9) {
                    item.setText("0");
                } else {
                    item.setText(String.valueOf(i + 1));
                }
            }
            //大小
            item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNumberSize);
            //居中
            item.setGravity(Gravity.CENTER);
            //字体颜色
            item.setTextColor(mNumberColor);
            //设置背景
            item.setBackground(provideItemBg());
            int finalI = i;
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null) {
                        mListener.onNumberClick(v, ((TextView)v).getText().toString());
                    }else{
                        Log.d(TAG, "mListener-->null");
                    }
                }
            });
            //添加View
            addView(item);
        }
    }

    private Drawable provideItemBg() {
        //按下去的效果
        GradientDrawable pressDrawable = new GradientDrawable();
        pressDrawable.setColor(mItemPressBg);
        pressDrawable.setCornerRadius(SizeUtils.dip2px(10));

        //普通的效果
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(mItemNormalBg);
        normalDrawable.setCornerRadius(SizeUtils.dip2px(10));

        //StateListDrawable是Drawable的孙子,selector
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        bg.addState(new int[]{}, normalDrawable);

        return bg;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        //获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.keypadView);
        //numberColor
        mNumberColor = a.getColor(R.styleable.keypadView_numberColor, context.getResources().getColor(R.color.key_text_color));
        //numberSize
        mNumberSize = a.getDimensionPixelSize(R.styleable.keypadView_numberSize, 20);
        //itemNormalBg
        mItemNormalBg = a.getColor(R.styleable.keypadView_itemNormalBg, context.getResources().getColor(R.color.key_item_color));
        //itemPressBg
        mItemPressBg = a.getColor(R.styleable.keypadView_itemPressBg, context.getResources().getColor(R.color.key_item_press_color));
        //itemMargin
        mItemMargin = a.getDimensionPixelSize(R.styleable.keypadView_itemMargin, DEFAULT_MARGIN);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量孩子
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int childHeight = (parentHeight - (row + 1) * mItemMargin - getPaddingTop() - getPaddingBottom()) / row;
        int normalWidth = (parentWidth - (column + 1) * mItemMargin - (getPaddingLeft() + getPaddingRight())) / column;
        int perChildHeight = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
        int normalChildWidth = MeasureSpec.makeMeasureSpec(normalWidth, MeasureSpec.EXACTLY);
        int deleteChildWidth = MeasureSpec.makeMeasureSpec(normalWidth * 2 + mItemMargin, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            boolean isNormalItem = (boolean) item.getTag();
            item.measure(!isNormalItem ? normalChildWidth : deleteChildWidth,
                    perChildHeight);
        }
        //测量自己
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int left, right, top, bottom;
        left = mItemMargin + getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            int rowIndex = i / column;
            int columnIndex = i % column;
            if (columnIndex == 0) {
                left = mItemMargin + getPaddingLeft();
            }
            View item = getChildAt(i);
            top = rowIndex * item.getMeasuredHeight() + mItemMargin * (rowIndex + 1) + getPaddingTop();
            right = left + item.getMeasuredWidth();
            bottom = top + item.getMeasuredHeight();
            item.layout(left, top, right, bottom);
            left += item.getMeasuredWidth() + mItemMargin;
        }
    }

    public int getNumberColor() {
        return mNumberColor;
    }

    public void setNumberColor(int numberColor) {
        mNumberColor = numberColor;
    }

    public int getNumberSize() {
        return mNumberSize;
    }

    public void setNumberSize(int numberSize) {
        mNumberSize = numberSize;
    }

    public int getItemNormalBg() {
        return mItemNormalBg;
    }

    public void setItemNormalBg(int itemNormalBg) {
        mItemNormalBg = itemNormalBg;
    }

    public int getItemPressBg() {
        return mItemPressBg;
    }

    public void setItemPressBg(int itemPressBg) {
        mItemPressBg = itemPressBg;
    }

    public int getItemMargin() {
        return mItemMargin;
    }

    public void setItemMargin(int itemMargin) {
        mItemMargin = itemMargin;
    }

    /**
     * 定义接口
     */
    public interface OnNumberClickListener{
        void onNumberClick(View v, String number);
    }

    public void setOnNumberClickListener(OnNumberClickListener listener){
        mListener = listener;
    }


}
