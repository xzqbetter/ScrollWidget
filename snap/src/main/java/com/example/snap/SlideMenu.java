package com.example.snap;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class SlideMenu extends HorizontalScrollView {

    private Context context;
    private int mWindowWidth;
    private boolean mHasInitWidth = false;
    private View mMenuView;
    private View mContentView;
    private VelocityTracker mVelocity;

    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        mWindowWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mHasInitWidth) {
            mHasInitWidth = true;
            ViewGroup wrapper = (ViewGroup) getChildAt(0);
            mMenuView = wrapper.getChildAt(0);
            mContentView = wrapper.getChildAt(1);
            mMenuView.getLayoutParams().width = mWindowWidth;
            mContentView.getLayoutParams().width = mWindowWidth;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mContentView.setElevation(30.0f);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            scrollTo(mWindowWidth, 0);
        }
    }

    private float mStartX;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mStartX = ev.getRawX();
            mVelocity = VelocityTracker.obtain();
        }
        if (mVelocity != null) {
            mVelocity.addMovement(ev);
        }
        if (mStartX > 30 && getScrollX()==mWindowWidth) {
            return false;
        }
        super.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mVelocity.computeCurrentVelocity(1000);
            float xVelocity = mVelocity.getXVelocity();
            mVelocity.recycle();
            mVelocity = null;
            if (xVelocity < -600) {
                smoothScrollTo(mWindowWidth, 0);
            } else if (xVelocity > 600) {
                smoothScrollTo(0, 0);
            } else if (getScrollX() > mWindowWidth/2) {
                smoothScrollTo(mWindowWidth, 0);
            } else if (getScrollX() < mWindowWidth/2) {
                smoothScrollTo(0, 0);
            }
        }
        return true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float fraction = l*1.0f/mWindowWidth;
        float scale = new FloatEvaluator().evaluate(fraction, 0.7, 1);
        float translationX = -new FloatEvaluator().evaluate(fraction,
                mWindowWidth * 0.3/2+mWindowWidth*0.7/3, 0);
        mContentView.setScaleX(scale);
        mContentView.setScaleY(scale);
        mContentView.setTranslationX(translationX);
    }

}
