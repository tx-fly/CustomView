package com.sunofbeach.customview.view.watchfaceview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.sunofbeach.customview.R;

public class WatchFaceView extends View {

    private int mSecondColor;
    private int mMinColor;
    private int mHourColor;
    private int mScaleColor;
    private int mFaceBg;
    private boolean mIsScaleShow;
    private final static String TAG = "WatchFaceView";
    private Bitmap mBgBitmap = null;
    private Paint mScalePaint;
    private Paint mHourPaint;
    private Paint mSecondPaint;
    private Paint mMinPaint;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private Rect mSrcRect;
    private Rect mTargetRect;

    public WatchFaceView(Context context) {
        this(context, null);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        initAttrs(context, attrs);
        //创建画笔
        initPaints();

    }

    /**
     * 创建相关画笔
     */
    private void initPaints() {
        //秒针画笔
        mMinPaint = new Paint();
        mMinPaint.setAntiAlias(true);
        mMinPaint.setColor(mMinColor);
        mMinPaint.setStrokeWidth(2f);
        mMinPaint.setStyle(Paint.Style.STROKE);
        //分针画笔
        mSecondPaint = new Paint();
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setColor(mSecondColor);
        mSecondPaint.setStrokeWidth(3f);
        mSecondPaint.setStyle(Paint.Style.STROKE);
        //时针画笔
        mHourPaint = new Paint();
        mHourPaint.setAntiAlias(true);
        mHourPaint.setColor(mHourColor);
        mHourPaint.setStrokeWidth(4f);
        mHourPaint.setStyle(Paint.Style.STROKE);
        //刻度画笔
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStrokeWidth(3f);
        mScalePaint.setStyle(Paint.Style.STROKE);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {

        //secondColor，分针颜色
        //minColor，秒针颜色
        //hourColor，时针颜色
        //scaleColor，刻度颜色
        //faceBg，表盘颜色
        //scaleShow，是否显示表盘
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WatchFaceView);
        mSecondColor = a.getColor(R.styleable.WatchFaceView_secondColor, getResources().getColor(R.color.secondColorDefault));
        mMinColor = a.getColor(R.styleable.WatchFaceView_minColor, getResources().getColor(R.color.minColorDefault));
        mHourColor = a.getColor(R.styleable.WatchFaceView_hourColor, getResources().getColor(R.color.hourColorDefault));
        mScaleColor = a.getColor(R.styleable.WatchFaceView_scaleColor, getResources().getColor(R.color.scaleColorDefault));
        mFaceBg = a.getResourceId(R.styleable.WatchFaceView_watchFaceBackground, -1);
        mIsScaleShow = a.getBoolean(R.styleable.WatchFaceView_scaleShow, true);
        a.recycle();

        //如果不为空，则有表盘图片载入
        if (mFaceBg != -1) {
            mBgBitmap = BitmapFactory.decodeResource(getResources(), mFaceBg);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量自己（即测量整个控件的大小）
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        //减去外边距，长宽取最小即可得到矩形
        int widthTargetSize = parentWidth - getPaddingLeft() - getPaddingRight();
        int heightTargetSize = parentHeight - getPaddingTop() - getPaddingBottom();
        //判断大小，取最小的值
        int endTargetSize = Math.min(widthTargetSize, heightTargetSize);
        setMeasuredDimension(endTargetSize, endTargetSize);
        //创建Ret，drawBitmap需要
        initRect();
    }

    private void initRect() {

        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();

        //源坑，即想要图片大小
        mSrcRect = new Rect();
        mSrcRect.left = 0;
        mSrcRect.top = 0;
        mSrcRect.bottom = mBgBitmap.getHeight();
        mSrcRect.right = mBgBitmap.getWidth();
        //目标坑，即想要背景大小
        mTargetRect = new Rect();
        mTargetRect.left = 0;
        mTargetRect.top = 0;
        mTargetRect.right = mMeasuredWidth;
        mTargetRect.bottom = mMeasuredHeight;

    }

    /**
     * 这个方法会很频繁调用，就是秒针每次跳动一下就会调用这个方法，刷新一次
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawColor(getResources().getColor(R.color.black));
        mBgBitmap = null;
        //绘制刻度
        drawScale(canvas);
        //绘制时针
    }

    private void drawScale(Canvas canvas) {
        if (mBgBitmap != null) {
            canvas.drawBitmap(mBgBitmap, mSrcRect, mTargetRect, mScalePaint);
        } else {
            //内环半径
            int innerC = (int) (mMeasuredWidth / 2 * 0.8);
            //外环半径
            int outerC = (int) (mMeasuredWidth / 2 * 0.9);
            //半径
            int radius = mMeasuredWidth / 2;
            // for (int i = 0; i < 12; i++) {
            //     //每一份的角度
            //     double th = i * (Math.PI * 2 / 12);
            //     //内环
            //     int innerB = (int) (Math.cos(th) * innerC);
            //     int innerY = (int) mMeasuredHeight / 2 - innerB;
            //     int innerA = (int) (Math.sin(th) * innerC);
            //     int innerX = mMeasuredWidth / 2 + innerA;
            //     //外环
            //     int outerB = (int) (Math.cos(th) * outerC);
            //     int outerY = (int) mMeasuredHeight / 2 - outerB;
            //     int outerA = (int) (Math.sin(th) * outerC);
            //     int outerX = mMeasuredWidth / 2 + outerA;
            //     canvas.drawLine(innerX, innerY, outerX, outerY, mScalePaint);
            // }
            //第二种方法通过旋转的方式绘制刻度
            canvas.save();
            for (int i = 0; i < 12; i++) {
                canvas.drawLine(radius,radius-outerC,radius,radius-innerC,mScalePaint);
                canvas.rotate(30,radius,radius);
            }
            canvas.restore();
        }
    }
}
