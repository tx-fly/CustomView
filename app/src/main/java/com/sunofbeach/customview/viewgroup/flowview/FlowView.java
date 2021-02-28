package com.sunofbeach.customview.viewgroup.flowview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunofbeach.customview.R;
import com.sunofbeach.customview.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class FlowView extends ViewGroup {

    //目前单位为px，不适配，需转换为dp
    private static final int DEFAULT_LINE = -1;
    private static final int DEFAULT_HORIZONTAL_MARGIN = SizeUtils.dip2px(5f);
    private static final int DEFAULT_VERTICAL_MARGIN = SizeUtils.dip2px(5f);
    private static final int DEFAULT_BOLDER_RADIUS = SizeUtils.dip2px(5f);
    private static final int DEFAULT_TEXT_MAX_LENGTH = -1;
    private static final String TAG = "flowView";
    private float mHorizontalMargin;
    private float mVerticalMargin;
    private int mMaxLine;
    private int mTextMaxLength;
    private int mTextColor;
    private int mBorderColor;
    private float mBorderRadius;

    private List<String> mData = new ArrayList<>();

    //用集合表示每一行。
    private List<List<View>> mLists = new ArrayList<>();
    private onItemClickListener mListener = null;
    private int mChildMeasureWidth;

    public FlowView(Context context) {
        this(context, null);
    }

    public FlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.flowView);
        // "itemHorizontalMargin" format="dimension",水平间距
        mHorizontalMargin = a.getDimension(R.styleable.flowView_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        // "itemVerticalMargin" format="dimension",垂直间距
        mVerticalMargin = a.getDimension(R.styleable.flowView_itemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        // "maxLine" format="integer"，最大行数
        mMaxLine = a.getInt(R.styleable.flowView_maxLine, DEFAULT_LINE);
        if (mMaxLine != -1 && mMaxLine < 1) {
            throw new IllegalArgumentException("mMaxLine can not less than 1.");
        }
        // "textMaxLine" format="integer"，最大文字个数
        mTextMaxLength = a.getInt(R.styleable.flowView_textMaxLength, DEFAULT_TEXT_MAX_LENGTH);
        if (mTextMaxLength != -1 && mTextMaxLength < 0) {
            throw new IllegalArgumentException("mTextMaxLength can not less than 0.");
        }
        // "textColor" format="color",文字颜色
        mTextColor = a.getColor(R.styleable.flowView_textColor, getResources().getColor(R.color.text_grey));
        // "bolderColor" format="color"，边框颜色
        mBorderColor = a.getColor(R.styleable.flowView_borderColor, getResources().getColor(R.color.text_grey));
        // "bolderRadius" format="dimension"/>，边框半径
        mBorderRadius = a.getDimension(R.styleable.flowView_borderRadius, DEFAULT_BOLDER_RADIUS);

        Log.d(TAG, "mHorizontalMargin-->" + mHorizontalMargin);
        Log.d(TAG, "mVerticalMargin-->" + mVerticalMargin);
        Log.d(TAG, "mMaxLine-->" + mMaxLine);
        Log.d(TAG, "mTextMaxLine-->" + mTextMaxLength);
        Log.d(TAG, "mTextColor-->" + mTextColor);
        Log.d(TAG, "mBorderColor-->" + mBorderColor);
        Log.d(TAG, "mBorderRadius-->" + mBorderRadius);
        //釋放資源
        a.recycle();
    }

    //测量View
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, "mode-->" + mode);
        Log.d(TAG, "size-->" + size);

        //获取子View
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        mChildMeasureWidth = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.UNSPECIFIED);
        int childMeasureHeight = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.UNSPECIFIED);

        //先清空
        mLists.clear();
        //创建默认行
        List<View> list = new ArrayList<>();
        mLists.add(list);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            //测量孩子
            measureChild(child, mChildMeasureWidth, childMeasureHeight);
            //测量自己，忽略间距情况，希望width是“match_parent",height是“wrap_content”，同时可以得到自己的高度。
            if (list.size() == 0) {
                //可以添加
                list.add(child);
            } else {
                if (!(checkChildCanBeAdd(list, child, parentWidthSize))) {
                    //mMaxLine属性设置
                    if (mMaxLine !=-1 && mMaxLine <=mLists.size()) {
                       break;
                    }
                    list = new ArrayList<>();
                    mLists.add(list);
                }
                list.add(child);
            }
        }
        //根据尺寸设置自己的高度
        View childAt = getChildAt(0);
        int childHeight = childAt.getMeasuredHeight();
        //这里只是计算总高度
        int parentTargetHeight = (int) ((childHeight + mVerticalMargin) * mLists.size() + mVerticalMargin
                + getPaddingTop() + getPaddingBottom());
        //设置高度
        setMeasuredDimension(parentWidthSize, parentTargetHeight);
        Log.d(TAG, "mLists.size()-->" + mLists.size());
    }

    /**
     * 判断是否还能添加view
     *
     * @param list
     * @param child
     * @param parentWidthSize
     * @return
     */
    private boolean checkChildCanBeAdd(List<View> list, View child, int parentWidthSize) {
        int childWidth = child.getMeasuredWidth();
        //处理padding问题
        int totalWidth = getPaddingLeft();


        //先测量已经存入的view的宽度
        for (View view : list) {
            totalWidth += view.getMeasuredWidth() + mHorizontalMargin;
        }
        //再加上这次想添加的view的宽度
        totalWidth += childWidth + mHorizontalMargin + getPaddingRight();
        Log.d(TAG, "totalWidth-->" + totalWidth);
        //如果没有超过则添加
        return totalWidth <= parentWidthSize;

    }

    public void setTestList(List<String> data) {


        //Removes all of the elements from this list
        mData.clear();
        mData = data;
        //把外部集合进行添加
        mData.addAll(data);
        //根据数据创建子View，并且添加进FlowLayout
        setUpChildren();

    }

    private void setUpChildren() {
        //先清空原来的内容
        removeAllViews();
        //添加子View
        for (String datum : mData) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_text, this, false);
            textView.setText(datum);
            //设置textView样式
            final String tempData = datum;
            //textView最大长度设置
            if (mTextMaxLength!=DEFAULT_TEXT_MAX_LENGTH) {
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLength)});
            }
            //设置点击事件
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v, tempData);
                    } else {
                        Log.d(TAG, "mListener-->null");
                    }
                }
            });
            //加入子View
            addView(textView);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);

        int firstChildHeight = child.getMeasuredHeight();

        int left = (int) (mHorizontalMargin + getPaddingLeft());
        int top = (int) mVerticalMargin + getPaddingTop();
        int right = (int) (mHorizontalMargin + getPaddingLeft());
        int bottom = (int) (firstChildHeight + mVerticalMargin + getPaddingTop());

        for (List<View> list : mLists) {

            for (View view : list) {
                int width = view.getMeasuredWidth();
                TextView v = (TextView) view;
                right += width;
                //解决单个View过长的问题
                if (right > mChildMeasureWidth - mHorizontalMargin - getPaddingLeft()) {
                    right = mChildMeasureWidth - (int) mHorizontalMargin;
                    v.setSingleLine();
                    v.setEllipsize(TextUtils.TruncateAt.END);
                }
                //mTextColor属性设置
                v.setTextColor(mTextColor);
                view.layout(left, top, right, bottom);
                //第一个结束
                left = (int) (right + mHorizontalMargin);
                right += mHorizontalMargin;
            }
            left = (int) (mHorizontalMargin + getPaddingLeft());
            right = (int) (mHorizontalMargin + getPaddingLeft());
            top = (int) (bottom + mVerticalMargin);
            bottom += (firstChildHeight + mVerticalMargin);
        }

    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    /**
     * 定义功能接口
     */
    public interface onItemClickListener {
        void onItemClick(View v, String s);
    }

    //Get和Set方法
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    public void setHorizontalMargin(float horizontalMargin) {
        mHorizontalMargin = horizontalMargin;
    }

    public float getVerticalMargin() {
        return mVerticalMargin;
    }

    public void setVerticalMargin(float verticalMargin) {
        mVerticalMargin = verticalMargin;
    }

    public int getMaxLine() {
        return mMaxLine;
    }

    public void setMaxLine(int maxLine) {
        mMaxLine = maxLine;
    }

    public int getTextMaxLine() {
        return mTextMaxLength;
    }

    public void setTextMaxLine(int textMaxLine) {
        mTextMaxLength = textMaxLine;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public float getBorderRadius() {
        return mBorderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        mBorderRadius = borderRadius;
    }
}
