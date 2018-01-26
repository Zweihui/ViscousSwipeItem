package com.zwh.viscous;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Zhangwh on 2016/11/26 0026.
 * email:616505546@qq.com
 * #E52D49
 */

public class ViscousView extends View {
    private Paint mPaint, mXPaint, mXPaint2;
    private static final int DEFAULT_DURATION = 6600;
    public static final int STATE_CLOSING = 0;
    public static final int STATE_OPENING = 1;
    public static final int STATE_ANIMATING = 3;
    private int mState;
    private float mFraction;
    private float mFraction2;
    private int mWidth, mHeight;
    private int mCenterX, mCenterY;
    private Path mPath, mXPath;
    private ValueAnimator valueAnimator = ValueAnimator.ofFloat(80.f, 100.f);

    private PointF pf1 = new PointF();
    private PointF pf2 = new PointF();
    private PointF pf3 = new PointF();
    private PointF pf4 = new PointF();
    private PointF pf5 = new PointF();
    private PointF pf6 = new PointF();
    private PointF pf7 = new PointF();

    public ViscousView(Context context) {
        this(context, null);
    }

    public ViscousView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViscousView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(255, 227, 43, 71));
        mXPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXPaint.setStyle(Paint.Style.STROKE);
        mXPaint.setColor(Color.WHITE);
        mXPaint.setStrokeCap(Paint.Cap.ROUND);
        mXPaint.setStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.dimen_X_width));
        mXPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXPaint2.setStyle(Paint.Style.FILL);
        mXPaint2.setColor(Color.WHITE);
        mPath = new Path();
        mXPath = new Path();
        mState = STATE_CLOSING;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.argb(255, 227, 43, 71));
        pf1.set(mWidth, mHeight * 2 / 9 * mFraction * mFraction);//上柱形的右下角坐标
        pf2.set(mWidth, 0);//上柱形的右上角坐标
        pf3.set(mWidth * (1 - mFraction), 0);//上柱形的左上角坐标
        pf4.set(mWidth * (1 - mFraction), mHeight * 2 / 9 * mFraction * mFraction);//上柱形的左下角坐标
        pf5.set(mWidth * (1 - mFraction), mHeight / 2 * mFraction);//左半边贝塞尔曲线控制点
        pf6.set(mWidth - mWidth * mFraction / 2, mHeight / 2 * mFraction);//上方柱形最低点，即贝塞尔曲线连接终点
        pf7.set(mWidth, mHeight / 2 * mFraction);//右半边贝塞尔曲线控制点
        if (mFraction >= 0.9f && mState == STATE_ANIMATING) { //当mFraction = 0.9时，开始执行中心X的动画
            startRefresh2();
        }
        if (mFraction2 > 0) {
            pf6.y = mHeight / 2 * mFraction + mHeight * mFraction2;
        }
        if (mFraction2 >= 0.5) {
            pf1.y = mHeight * 4 / 8 * mFraction2 * 5;
            pf4.y = mHeight * 4 / 8 * mFraction2 * 5;
        }
        mPath.reset();
        //画上方圆弧柱形 依次从 右下角(直线) -> 右上角(直线)-> 左上角(直线)-> 左下角(直线)-> 左半边贝塞尔曲线->右半边贝塞尔曲线
        mPath.moveTo(pf1.x, pf1.y);
        mPath.lineTo(pf2.x, pf2.y);
        mPath.lineTo(pf3.x, pf3.y);
        mPath.lineTo(pf4.x, pf4.y);
        mPath.quadTo(pf5.x, pf5.y, pf6.x, pf6.y);
        mPath.quadTo(pf7.x, pf7.y, pf1.x, pf1.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        //画下方圆弧柱形 过程同上
        mPath.reset();
        mPath.moveTo(pf1.x, mHeight - pf1.y);
        mPath.lineTo(pf2.x, mHeight - pf2.y);
        mPath.lineTo(pf3.x, mHeight - pf3.y);
        mPath.lineTo(pf4.x, mHeight - pf4.y);
        mPath.quadTo(pf5.x, mHeight - pf5.y, pf6.x, mHeight - pf6.y);
        mPath.quadTo(pf7.x, mHeight - pf7.y, pf1.x, mHeight - pf1.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
//        if (mFraction >= 0.9f) {
//            mPaint.setColor(Color.WHITE);
//            mPath.reset();
//            mPath.moveTo(pf4.x, pf4.y);
//            mPath.cubicTo(mCenterX, mCenterY,0, mCenterY/2, mCenterX, mCenterY);
//            mPath.moveTo(pf4.x, mHeight - pf4.y);
//            mPath.cubicTo(mCenterX, mCenterY,0, mCenterY/2, mCenterX, mCenterY);
//            mPath.close();
//            canvas.drawPath(mPath, mPaint);
//            mPath.reset();
//            mPath.moveTo(pf1.x, pf1.y);
//            mPath.cubicTo(mCenterX, mCenterY, mCenterX, mCenterY,mWidth, mCenterY/2);
//            mPath.cubicTo(mCenterX, mCenterY, mCenterX, mCenterY,pf1.x, mHeight - pf1.y);
//            mPath.close();
//            canvas.drawPath(mPath, mPaint);
//        }
        float k = 0.4f; //控制X的粘性效果，越大越明显，不能大于1，同时太大会影响动画流畅度，推荐在0.5左右效果最佳
        if (mFraction >= 0.9) {
            //计算X的旋转角度
            float left //X的左坐标
                    = mWidth / 4 + (float) (((mWidth / 4) - (mWidth / 4)
                    * Math.cos(Math.toRadians(45 * mFraction2))));
            float top //X的上坐标
                    = (float) (mHeight / 2 - ((mWidth / 4) * Math.sin(Math.toRadians(45 * mFraction2))));
            float right //X的右坐标
                    = mWidth * 3 / 4 - (float) (((mWidth / 4) - (mWidth / 4) * Math.cos(
                    Math.toRadians(45 * mFraction2))));
            float bottom //X的下坐标
                    = (float) (mHeight / 2 + ((mWidth / 4) * Math.sin(Math.toRadians(45 * mFraction2))));
            mXPath.reset();
            mXPath.moveTo(left, top);
            mXPath.lineTo(right, bottom);
            canvas.drawPath(mXPath, mXPaint);
            mXPath.moveTo(left, bottom);
            mXPath.lineTo(right, top);
            canvas.drawPath(mXPath, mXPaint);
            if (mFraction2 <= k) {
                mXPath.moveTo(left, top);
                float x1 = (mCenterX - left) * mFraction2 + left;
                float y1 = bottom - (bottom - mCenterY) * mFraction2;
                mXPath.quadTo(x1, mCenterY, left,
                        bottom);//左粘性贝塞尔曲线
                mXPath.quadTo(mCenterX, y1,
                        right, bottom);//下粘性贝塞尔曲线
                float x2 = right - (right - mCenterX) * mFraction2;
                float y2 = (mCenterY - top) * mFraction2 + top;
                mXPath.quadTo(x2, mCenterY,
                        right, top);//右粘性贝塞尔曲线
                mXPath.quadTo(mCenterX, y2,
                        left, top);//上粘性贝塞尔曲线
            } else {
                float m = (mFraction2 - k) / (1 - k);
                mXPath.moveTo((mCenterX - left) * m + left, (mCenterY - top) * m + top);
                float x1 = (mCenterX - left) * m + left;
                float y1 = bottom - (bottom - mCenterY) * m;
                float x2 = right - (right - mCenterX) * m;
                float y2 = (mCenterY - top) * m + top;
                mXPath.quadTo((mCenterX - left) * mFraction2 + left, mCenterY, x1,
                        y1);//左粘性贝塞尔曲线
                mXPath.quadTo(mCenterX, bottom - (bottom - mCenterY) * mFraction2,
                        x2, y1);//下粘性贝塞尔曲线
                mXPath.quadTo(right - (right - mCenterX) * mFraction2, mCenterY,
                        x2, y2);//右粘性贝塞尔曲线
                mXPath.quadTo(mCenterX, (mCenterY - top) * mFraction2 + top,
                        x1, y2);//上粘性贝塞尔曲线
            }
            mXPath.close();
            canvas.drawPath(mXPath, mXPaint2);
        }
    }


    public void startRefresh2() {  //粘性部分
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFraction2 = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = STATE_OPENING;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }
    }

    public void setmFraction(float fraction, boolean isCloseing) {
        mFraction = fraction;
        if (isCloseing) {
            mState = STATE_CLOSING;
            valueAnimator.cancel();
            mFraction2 = 0;
        } else {
            if (mFraction == 0) {
                mState = STATE_CLOSING;
            } else if (mFraction == 1) {
                mState = STATE_OPENING;
            } else {
                mState = STATE_ANIMATING;
            }
        }
        invalidate();
    }

    public void reset() {
        mState = STATE_CLOSING;
        mFraction = 0;
        mFraction2 = 0;
    }


    public int getState() {
        return mState;
    }


}
