package com.zwh.viscous;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Zhangwh on 2016/11/26 0026.
 * email:616505546@qq.com
 * #E52D49
 */

public class ViscousView extends View {
  private Paint mPaint,mXPaint,mXPaint2;
  private int mDuration;
  private static final int DEFAULT_DURATION = 2000;
  private boolean isSwiping = false;
  private boolean isInvalue = false;
  public boolean cantransform = true; //是否可以重绘
  public boolean isAuto = false; //是否是自动刷新
  private float mFraction;
  private float mFraction2;
  private float mFraction3;
  private int mWidth, mHeight;
  private int mCenterX, mCenterY;
  private Path mPath,mXPath,mXPath2;

  public ViscousView(Context context) {
    this(context, null);
  }

  public ViscousView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ViscousView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mDuration = DEFAULT_DURATION;
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(Color.argb(255, 227, 43, 71));
    mXPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mXPaint.setStyle(Paint.Style.STROKE);
    mXPaint.setColor(Color.WHITE);
    mXPaint.setStrokeCap(Paint.Cap.ROUND);
    mXPaint.setStrokeWidth(10);
    mXPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mXPaint2.setStyle(Paint.Style.FILL);
    mXPaint2.setColor(Color.WHITE);
    mPath = new Path();
    mXPath = new Path();
    mXPath2 = new Path();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;
    mCenterX = mWidth / 2;
    mCenterY = mHeight / 2;
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (!cantransform) {
      mFraction = 1;
      mFraction2 = 1;
    }
    float[] px1 = { mWidth, mHeight * 3 / 8 * mFraction * mFraction };
    float[] px2 = { mWidth, 0 };
    float[] px3 = { mWidth * (1 - mFraction), 0 };
    float[] px4 = { mWidth * (1 - mFraction), mHeight * 3 / 8 * mFraction * mFraction };
    float[] px5 = { mWidth * (1 - mFraction), mHeight / 2 * mFraction };
    float[] px6 = { mWidth - mWidth * mFraction / 2, mHeight / 2 * mFraction };
    float[] px7 = { mWidth, mHeight / 2 * mFraction };
    if (mFraction >= 0.9f && !isSwiping) { //
      startRefresh2();
      px6[1] = mHeight / 2 * mFraction + mHeight * mFraction2;
      isSwiping = true;
    }
    if (mFraction2 > 0) {
      px6[1] = mHeight / 2 * mFraction + mHeight * mFraction2;
    }
    if (mFraction2 >= 0.5) {
      px1[1] = mHeight * 4 / 8 * mFraction2 * 5;
      px4[1] = mHeight * 4 / 8 * mFraction2 * 5;
    }
    //画上下两个圆弧柱形
    mPath.reset();
    mPath.moveTo(px1[0], px1[1]);
    mPath.lineTo(px2[0], px2[1]);
    mPath.lineTo(px3[0], px3[1]);
    mPath.lineTo(px4[0], px4[1]);
    mPath.quadTo(px5[0], px5[1], px6[0], px6[1]);
    mPath.quadTo(px7[0], px7[1], px1[0], px1[1]);
    mPath.close();
    canvas.drawPath(mPath, mPaint);
    mPath.reset();
    mPath.moveTo(px1[0], mHeight - px1[1]);
    mPath.lineTo(px2[0], mHeight - px2[1]);
    mPath.lineTo(px3[0], mHeight - px3[1]);
    mPath.lineTo(px4[0], mHeight - px4[1]);
    mPath.quadTo(px5[0], mHeight - px5[1], px6[0], mHeight - px6[1]);
    mPath.quadTo(px7[0], mHeight - px7[1], px1[0], mHeight - px1[1]);
    mPath.close();
    canvas.drawPath(mPath, mPaint);
    canvas.save();
    if (mFraction >= 0.9) {
      //计算X的旋转角度
      float left = mWidth * 1 / 4 + (float) (((mWidth * 1 / 4) - (mWidth * 1 / 4) * Math.cos(
          Math.toRadians(45 * mFraction2))));
      float top = (float) (mHeight / 2 - ((mWidth * 1 / 4) * Math.sin(Math.toRadians(45 * mFraction2))));
      float right = mWidth * 3 / 4 - (float) (((mWidth * 1 / 4) - (mWidth * 1 / 4) * Math.cos(
          Math.toRadians(45 * mFraction2))));
      float bottom =
          (float) (mHeight / 2 + ((mWidth * 1 / 4) * Math.sin(Math.toRadians(45 * mFraction2))));
      mXPath.reset();
      mXPath.moveTo(left, top);
      mXPath.lineTo(right, bottom);
      canvas.drawPath(mXPath, mXPaint);
      mXPath.moveTo(left, bottom);
      mXPath.lineTo(right, top);
      canvas.drawPath(mXPath, mXPaint);
      mXPath.moveTo(left, top);
      int k = 15;
      int m = 15;
      //画X的粘性部分
      mXPath.quadTo(left + mWidth / 4 * mFraction2, mCenterY, left + mFraction2 * k,
          bottom - mFraction2 * k);//左
      if (mFraction2 <= 0.5) {
        mXPath.quadTo(mCenterX, mCenterY + 50 * mFraction2 - mFraction2 * m + 3,
            right - mFraction2 * k, bottom - mFraction2 * k);//下
      } else {
        mXPath.quadTo(mCenterX, mCenterY + 50 * (1 - mFraction2) - mFraction2 * m + 3,
            right - mFraction2 * k, bottom - mFraction2 * k);//下
      }
      mXPath.quadTo(right - mWidth / 4 * mFraction2, mCenterY, right, top);//右
      if (mFraction2 <= 0.5) {
        mXPath.quadTo(mCenterX, mCenterY - 50 * mFraction2 + mFraction2 * m + 3,
            left + mFraction2 * k, top + mFraction2 * k);//上
      } else {
        mXPath.quadTo(mCenterX, mCenterY - 50 * (1 - mFraction2) + mFraction2 * m + 3,
            left + mFraction2 * k, top + mFraction2 * k);//上
      }
      mXPath.close();
      canvas.drawPath(mXPath, mXPaint2);
    }
  }

  public void startRefresh(final float f) {
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(80.f, 100.f);
    valueAnimator.setDuration(150);
    valueAnimator.setInterpolator(new LinearInterpolator());
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        isAuto = true;
        mFraction = valueAnimator.getAnimatedFraction()+f;
        if(mFraction>=0.97f){
          mFraction =0.97f;
        }
        invalidate();
      }
    });
    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
      }
    });
    if (!valueAnimator.isRunning()) {
      valueAnimator.start();
    }
  }
  public void startRefresh2() {  //粘性部分
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(80.f, 100.f);
    valueAnimator.setDuration(800);
    valueAnimator.setInterpolator(new DecelerateInterpolator());
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mFraction2 = valueAnimator.getAnimatedFraction();
        invalidate();
        if(mFraction2==1){
          cantransform= false;
        }
      }
    });
    if (!valueAnimator.isRunning()) {
      valueAnimator.start();
    }
  }
  public void setmFraction(float fraction){
    mFraction = fraction;
    if(fraction<=0.97f&&!isInvalue&&!isAuto){
      invalidate();
      if(mFraction == 0.97f){
        isSwiping =true;
      }
    }
  }

  public void reset() {
    isSwiping = false;
    isInvalue = false;
    cantransform = true;
    mFraction = 0;
    mFraction2 = 0;
    isAuto = false;
  }
}
