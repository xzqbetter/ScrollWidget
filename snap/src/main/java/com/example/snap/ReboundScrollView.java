package com.example.snap;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

public class ReboundScrollView extends LinearLayout {

    private Context context;
    private Scroller mScroller;
    private VelocityTracker mVelocity;
    private FloatEvaluator mFloatEvaluator;

    public ReboundScrollView(Context context) {
        this(context, null);
    }

    public ReboundScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ReboundScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        mScroller = new Scroller(context);
        mVelocity = VelocityTracker.obtain();
        mFloatEvaluator = new FloatEvaluator();
    }

    private Float mStartY = null;
    private Float mStartX = null;
    private Float mEndY = null;
    private Float mEndX = null;
    private float mDifY = 0;
    private float mDifX = 0;
    private int mScrollHeight = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction()== MotionEvent.ACTION_DOWN) {
            mStartX = null;
            mStartY = null;
            isFling = false;
            mScroller.forceFinished(true);
        }
        if (mStartY == null) {
            mStartX = ev.getRawX();
            mStartY = ev.getRawY();
        }
        mEndX = ev.getRawX();
        mEndY = ev.getRawY();
        mDifY = mEndY - mStartY;
        mDifX = mEndX - mStartX;
        mStartX = ev.getRawX();
        mStartY = ev.getRawY();

        if (mVelocity == null) {
            mVelocity = VelocityTracker.obtain();
        }
        mVelocity.addMovement(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (Math.abs(mDifY) > Math.abs(mDifX)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                isFling = false;
                if (getScrollY()<0) {
                    float fraction = mFloatEvaluator.evaluate(Math.abs(getScrollY())*1.0f/1000, 1, 10);
                    scrollBy(0, (int) (-mDifY/fraction));
                } else if (getScrollY()>mScrollHeight) {
                    float fraction = mFloatEvaluator.evaluate(Math.abs(getScrollY()-mScrollHeight)*1.0f/1000, 1, 10);
                    scrollBy(0, (int) (-mDifY/fraction));
                } else {
                    scrollBy(0, -(int) mDifY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocity.computeCurrentVelocity(1000);
                int yVelocity = (int) mVelocity.getYVelocity();
                mVelocity.recycle();
                mVelocity = null;
                if (Math.abs(yVelocity) > 600) {
                    isFling = true;
                    int distance = Math.abs(yVelocity);
                    mScroller.fling(0, -getScrollY(),
                            0, yVelocity,
                            0,0,
                            -getScrollY()-distance, -getScrollY()+distance);
                } else {
                    isFling = false;
                    if (getScrollY() < 0) {
                        mScroller.startScroll(0, -getScrollY(), 0, getScrollY(), 1000);
                    } else if (getScrollY() > mScrollHeight){
                        mScroller.startScroll(0, -getScrollY(), 0, getScrollY()-mScrollHeight, 1000);
                    }
                }
                postInvalidate();
                break;
        }

        return true;
    }

    private Integer mPreYFling = null;
    private float mDifYFling = 0;
    private boolean isFling = false;

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            if (isFling) {
                if (mPreYFling == null) {
                    mPreYFling = mScroller.getCurrY();
                }
                mDifYFling = mPreYFling - mScroller.getCurrY();
                mPreYFling = mScroller.getCurrY();
                if (getScrollY()<0
                        && getScrollY()<-mScroller.getCurrVelocity()/-getScrollY()*10) {
                    snapToTop();
                } else if (getScrollY()>mScrollHeight
                        && (getScrollY()-mScrollHeight)>mScroller.getCurrVelocity()/(getScrollY()-mScrollHeight)*10) {
                    snapToBottom();
                } else if (getScrollY()<0) {
                    float fraction = mFloatEvaluator.evaluate(Math.abs(getScrollY())*1.0f/1000, 1, 10);
                    scrollBy(0, (int) (mDifYFling/fraction));
                } else if (getScrollY()>mScrollHeight) {
                    float fraction = mFloatEvaluator.evaluate(Math.abs(getScrollY()-mScrollHeight)*1.0f/1000, 1, 10);
                    scrollBy(0, (int) (mDifYFling/fraction));
                } else {
                    scrollTo(0, -mScroller.getCurrY());
                }
            } else {
                scrollTo(0, -mScroller.getCurrY());
            }
            postInvalidate();
        } else {
            mPreYFling = null;
            if (getScrollY() < 0 && isFling) {
                snapToTop();
            } else if (getScrollY() > mScrollHeight && isFling) {
                snapToBottom();
            }
        }
    }

    private void snapToTop() {
        isFling = false;
        mScroller.forceFinished(true);
        mScroller.startScroll(0, -getScrollY(), 0, getScrollY(), 1000);
        postInvalidate();
    }

    private void snapToBottom() {
        isFling = false;
        mScroller.forceFinished(true);
        mScroller.startScroll(0, -getScrollY(), 0, getScrollY()- mScrollHeight, 1000);
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            int totalHeight = 0;
            int totalwidth = 0;
            int childCount = getChildCount();
            for(int i=0; i<childCount; i++){
                View childView = getChildAt(i);
                int childwidth = childView.getMeasuredWidth();
                int childheight = childView.getMeasuredHeight();
                totalHeight += childheight;
                totalwidth += childwidth;
            }
            mScrollHeight = totalHeight- getMeasuredHeight();
            if (mScrollHeight < 0) {
                mScrollHeight = 0;
            }
        }
    }

}
