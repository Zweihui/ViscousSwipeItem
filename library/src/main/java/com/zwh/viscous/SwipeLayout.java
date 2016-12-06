package com.zwh.viscous;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by RuiDu on 2015/12/20.
 */
public class SwipeLayout extends FrameLayout {

    /**
     * the class which help to implement drag the item
     */
    private ViewDragHelper mViewDragHelper;

    /**
     * the child view which can be draged
     */
    private ViewGroup childView;
    ViscousView viscousView;
    /**
     * the underlyingView
     */
    private ViewGroup underlyingView;


    private OnSwipeListener mOnSwipeListener;

    /**
     * the current state
     */
    public State mState = State.Close;

    private String TAG = "SwipeLayout";
    private float dLeft = 0;

    /**
     * the length of overlap part in px,
     * since we assumed the two layouts are one covered by another,this length may be
     * the length of the underlying layout
     */
    private int  overlapLength = 150;

    /**
     * the margin to be thought as open in px,
     * it allows an offset to the position at OPEN state
     * when it acts from CLOSE to OPEN,this value allows user drag more over the position of OPEN and slide back to OPEN
     * once released.
     * when it acts from OPEN to Close,this value allows user's slight drag be thought as cancel action
     */
    private int  openMargin = 0;

    /**
     * the margin to be thought as close in px
     * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
     */
    private int  closeMargin = 30;




    public enum State{

        Open, Close
    }

    /**
     * an interface to do some specific works when the child view is released and slide
     * these method will be called before the slide animations.
     */
    public interface OnSwipeListener{
        public void onStartOpen();
        public void onOpen();
        public void onOpening(int dx);
        public void onStartClose();
        public void onClose();
    }

    /**
     * set the SwipeListener
     * @param onSwipeListener
     */
    public void setOnOnSwipeListener(OnSwipeListener onSwipeListener){
        mOnSwipeListener = onSwipeListener;
    }


    /**
     * configurate some params
     * @param swipeConfig
     */
    public void configSwipe(SwipeConfig swipeConfig){

        this.overlapLength = swipeConfig.getOverlapLength();
        this.closeMargin = swipeConfig.getCloseMargin();
        this.openMargin = swipeConfig.getOpenMargin();
    }


    /**
     * get the current state
     * @return
     */
    public State getState() {
        return mState;
    }

    public SwipeLayout(Context context) {
        this(context,null);

    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }


    /**
     * initialize the class,its main work is to get an instantce of ViewDragHelper
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new SwipeCallback());

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * we should put the event to ViewDragHelper and we need implement the call back method
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "SwipeLayout$onTouchEvent: "+mState.toString());
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * get the second view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        underlyingView = (ViewGroup) this.getChildAt(0);
        childView = (ViewGroup) this.getChildAt(1);
        viscousView = (ViscousView) underlyingView.getChildAt(0);
    }


    /**
     * execute the smoothly slide animation and change the state
     */
    public void close(){
        Log.d(TAG, "SwipeLayout$close(): "+mState.toString());
        mState = State.Close;
        viscousView.reset();
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onStartClose();
        }
        if(mViewDragHelper.smoothSlideViewTo(childView, 0, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onClose();
        }
    }

    /**
     * execute the smoothly slide animation and change the state
     */
    public void open(){
        Log.d(TAG, "SwipeLayout$open: before:"+mState.toString());
        mState = State.Open;
        Log.d(TAG, "SwipeLayout$open() "+mState.toString());
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onStartOpen();
        }
        viscousView.startRefresh((-dLeft/150f));
        dLeft = 0;
        if(mViewDragHelper.smoothSlideViewTo(childView, -overlapLength, 0)){
        ViewCompat.postInvalidateOnAnimation(this);
        }
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onOpen();
        }

    }


    //works with the smoothSlideViewTo method
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);

        }
    }


    /**
     * reset the view, making sure there's no problem in reusing
     */
    public void initialState(){
        close();
    }



    class SwipeCallback extends ViewDragHelper.Callback{

        /**
         * only the second view should be draged, the first(underlying)view is acted as button
         * @param child
         * @param arg1
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int arg1) {

            return child == childView;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            int maxLeftLeght = overlapLength+openMargin;
            dLeft = dLeft+dx;
            Log.d(TAG, "SwipeLayout$clampViewPositionHorizontal: "+mState.toString());
            if(dx>0){
                if(left>0){
                    return 0;
                }else{
                    return left;
                    }
            }else{
                if(left<-maxLeftLeght){
                    return -maxLeftLeght;
                }else{
                    viscousView.setmFraction(-0.97f*left/150);
                    return left;
                }
            }
            //mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback()
            //{
            //    @Override
            //    public boolean tryCaptureView(View child, int pointerId)
            //    {
            //        return child == childView;
            //    }
            //    @Override
            //    public int getViewHorizontalDragRange(View child) {
            //        return overlapLength;
            //    }
            //    @Override
            //    public int clampViewPositionHorizontal(View child, int left, int dx)
            //    {
            //        dLeft = dLeft+dx;
            //        if(dLeft>=-150)
            //        viscousView.setmFraction(-0.97f*dLeft/150);
            //        else
            //            dLeft = -150;
            //        Log.e(TAG, "SwipeLayout$clampViewPositionHorizontal: "+dLeft);
            //        return dLeft;
            //    }
            //
            //});
        }


        @Override
        public int getViewHorizontalDragRange(View child) {
            return overlapLength;
        }



        @Override
        public void onViewReleased(View releasedChild, float xvel,
                                   float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

                //to see if its a close action
                if(releasedChild.getLeft()>-closeMargin||
                        (xvel>0)){
                    close();
                }else{
                    open();
                }


        }
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //dLeft = dLeft+dx;
            //dLeft = -150;
            //viscousView.setmFraction(-0.97f*dLeft/150);
            //Log.e(TAG, "SwipeLayout$clampViewPositionHorizontal: "+dLeft);
        }
    }


    /**
     * to configurate some parameters
     */
    public static class SwipeConfig {

        /**
         * the length of overlap part in px,
         * since we assumed the two layouts are one covered by another,this length may be
         * the length of the underlying layout
         */
        private int  overlapLength = 240;

        /**
         * the margin to be thought as open in px,
         * it allows an offset to the position at OPEN state
         * when it acts from CLOSE to OPEN,this value allows user drag more over the position of OPEN and slide back to OPEN
         * once released.
         * when it acts from OPEN to Close,this value allows user's slight drag be thought as cancel action
         */
        private int  openMargin = 20;

        /**
         * the margin to be thought as close in px
         * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
         */
        private int  closeMargin = 20;



        /**
         * initialize the overlaplength px
         * @param overlapLength
         */
        public SwipeConfig(int overlapLength){
            this.overlapLength = overlapLength;
        }

        /**
         * initialize the overlaplength px and all the margins as the same value
         * @param overlapLength
         * @param margins
         */
        public SwipeConfig(int overlapLength,int margins){
            this.overlapLength = overlapLength;
            this.openMargin = margins;
            this.closeMargin = margins;

        }

        /**
         * initialize the overlaplength px and all the margins as different value
         * @param overlapLength
         * @param openMargin
         * @param closeMargin
         */
        public SwipeConfig(int overlapLength,int openMargin,int closeMargin){
            this.overlapLength = overlapLength;
            this.openMargin = openMargin;
            this.closeMargin = closeMargin;

        }

        public int getOverlapLength() {
            return overlapLength;
        }

        public void setOverlapLength(int overlapLength) {
            this.overlapLength = overlapLength;
        }

        public int getOpenMargin() {
            return openMargin;
        }

        public void setOpenMargin(int openMargin) {
            this.openMargin = openMargin;
        }

        public int getCloseMargin() {
            return closeMargin;
        }

        public void setCloseMargin(int closeMargin) {
            this.closeMargin = closeMargin;
        }


    }

}
