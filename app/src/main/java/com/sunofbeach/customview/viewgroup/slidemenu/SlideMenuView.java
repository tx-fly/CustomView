package com.sunofbeach.customview.viewgroup.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.sunofbeach.customview.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class SlideMenuView extends ViewGroup implements View.OnClickListener {

    private int mFunction;
    private final static String TAG = "SlideMenuView";
    private View mEditView;
    private View mContentView;
    private OnEditClickListener mListener = null;
    private View mInflateView;
    private TextView mEdit_top;
    private TextView mEdit_delete;
    private TextView mEdit_read;
    private int mLeft = 0;
    private float mDownX;
    private float mDownY;
    private Scroller mScroller = null;
    private float mInterceptDownX;
    private float mInterceptDownY;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        initAttr(context, attrs);

        mScroller = new Scroller(context);
    }

    /**
     * 获取属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuView);
        //默认至少有个置顶功能
        mFunction = a.getInt(R.styleable.SlideMenuView_function, 0x30);
        Log.d(TAG, "SlideMenuView-->" + mFunction);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        //根据判断只能有一个View，这个View是内容
        if (childCount > 1) {
            throw new IllegalArgumentException("childCount no more than 1");
        }
        Log.d(TAG, "childCount-->" + childCount);
        //得到内容的View，方便onMeasure用
        mContentView = getChildAt(0);
        mInflateView = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_menu, this, false);
        initEditView();
        addView(mInflateView);
        //得到编辑的View，方便onMeasure用
        mEditView = getChildAt(1);
    }

    /**
     * 初始化编辑View，同时找到编辑View的那些子View
     */
    private void initEditView() {


        mEdit_delete = mInflateView.findViewById(R.id.text_View_delete);
        mEdit_read = mInflateView.findViewById(R.id.text_View_readed);
        mEdit_top = mInflateView.findViewById(R.id.text_View_top1);

    }

    private void editOnClick() {
        if (mListener == null) {
            Log.d(TAG, "mListener-->null");
            return;
        }

        mEdit_top.setOnClickListener(this);
        mEdit_delete.setOnClickListener(this);
        mEdit_read.setOnClickListener(this);
    }

    //处理有点击事件时，滑动无法生效的问题
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //选择拦截
        //如果横向滑动，我就拦截，否则就不拦截
        switch (ev.getAction()) {
            case ACTION_DOWN:
                mInterceptDownX = ev.getX();
                mInterceptDownY = ev.getY();
                break;
            case ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                if (Math.abs(x - mInterceptDownX) > 0) {
                    //自己消费
                    return true;
                }
                break;
            case ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        //先测量内容View,宽度和父控件一样；高度分三种情况讨论，match_parent,wrap_content,指定大小。
        LayoutParams contentLayoutParams = mContentView.getLayoutParams();
        //获取到了当前View的高度
        int contentHeight = contentLayoutParams.height;
        int contentHeightSpec;
        if (contentHeight == LayoutParams.MATCH_PARENT) {
            contentHeightSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.EXACTLY);
        } else if (contentHeight == LayoutParams.WRAP_CONTENT) {
            contentHeightSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
        } else {
            contentHeightSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        }
        mContentView.measure(widthMeasureSpec, contentHeightSpec);

        //编辑部分
        int editHeightSize = contentHeight;
        int editWidthSize = parentWidthSize * 3 / 4;

        int editWidthSpec = MeasureSpec.makeMeasureSpec(editWidthSize, MeasureSpec.EXACTLY);
        int editHeightSpec = MeasureSpec.makeMeasureSpec(editHeightSize, MeasureSpec.EXACTLY);

        mEditView.measure(editWidthSpec, editHeightSpec);


        //测量自己
        //宽度就是前面的宽度总和，高度和内容View一样
        setMeasuredDimension(editWidthSize + parentWidthSize, editHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //内容布局
        int childContentWidth = mContentView.getMeasuredWidth();
        int childHeight = mContentView.getMeasuredHeight();
        int right, top, bottom;
        right = mLeft + childContentWidth;
        top = 0;
        bottom = top + childHeight;
        mContentView.layout(mLeft, top, right, bottom);
        //编辑布局
        int childEditWidth = mEditView.getMeasuredWidth();
        int editLeft = right;
        int editRight = right + childEditWidth;
        mEditView.layout(editLeft, top, editRight, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                Log.d(TAG, "ACTION_DOWN-->" + action);
                Log.d(TAG, "downX-->" + mDownX);
                Log.d(TAG, "downY-->" + mDownY);
                break;
            case ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                Log.d(TAG, "ACTION_UP-->" + action);
                Log.d(TAG, "upX-->" + upX);
                Log.d(TAG, "upY-->" + upY);
                //处理释放以后，是显示还是收缩回去
                int hasBeenScrolled = getScrollX();
                int width = mEditView.getMeasuredWidth() / 2;
                //如果超过就显示,否则会隐藏
                if (hasBeenScrolled >= width) {
                    //显示出来
                    mScroller.startScroll(getScrollX(), 0,
                            mEditView.getMeasuredWidth() - getScrollX(), 500);
                } else {
                    Log.d(TAG, "hasBeenScrolled-->no");
                    mScroller.startScroll(getScrollX(), 0, -getScrollX(), 500);
                }
                invalidate();
                break;
            case ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //下面需要scrollBy
                int scrollX = getScrollX();
                //移动的差值
                int dx = (int) (moveX - mDownX);
                //判断边界
                int resultScroll = scrollX - dx;
                if (resultScroll <= 0) {
                    scrollTo(0, 0);
                } else if (resultScroll > mEditView.getMeasuredWidth()) {
                    scrollTo(mEditView.getMeasuredWidth(), 0);
                } else {
                    //把差值利用起来
                    scrollBy(-dx, 0);
                }

                //mLeft += dx;
                //请求更新布局
                requestLayout();
                //当下一轮再移动那move就是那个down了
                mDownX = moveX;
                mDownY = moveY;
                Log.d(TAG, "ACTION_MOVE-->" + action);
                Log.d(TAG, "moveX-->" + moveX);
                Log.d(TAG, "moveY-->" + moveY);
                break;
        }
        // boolean b = super.onTouchEvent(event);
        // Log.d(TAG, "b-->"+b);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int curr = mScroller.getCurrX();
            //滑动到指定位置即可
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    /**
     * 实现编辑View的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.text_View_delete:
                mListener.onDeleteClick();
                break;
            case R.id.text_View_top1:
                Log.d(TAG, "onClick-->top");
                mListener.onTopClick();
                break;
            case R.id.text_View_readed:
                mListener.onReadClick();
                break;
            default:
                break;
        }
    }

    /**
     * 定义功能接口
     */
    public interface OnEditClickListener {

        void onReadClick();

        void onDeleteClick();

        void onTopClick();
    }

    /**
     * 回调函数
     *
     * @param listener
     */
    public void setOnEditClickListener(OnEditClickListener listener) {
        mListener = listener;
        editOnClick();
        if (mListener != null) {
            Log.d(TAG, "setOnEditClickListener-->");
        }

    }

    public int getFunction() {
        return mFunction;
    }

    public void setFunction(int function) {
        mFunction = function;
    }
}
