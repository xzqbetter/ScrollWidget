package com.example.snap.gooddetail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SnapParent extends ViewGroup {

    private Scroller mScroller;

    public SnapParent(Context context) {
        this(context, null);
    }

    public SnapParent(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SnapParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int totalHeight = 0;
            int totalwidth = 0;
            int childCount = getChildCount();
            for(int i=0; i<childCount; i++){
                View childView = getChildAt(i);
                int childwidth = childView.getMeasuredWidth();
                int childheight = childView.getMeasuredHeight();
                childView.layout(0, totalHeight, childwidth, totalHeight+childheight);
                totalHeight += childheight;
                totalwidth += childwidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void scrollToBottom() {
        mScroller.startScroll(0, -getScrollY(), 0, -(getMeasuredHeight()-getScrollY()), 1000);
        postInvalidate();
    }

    public void scrollToTop() {
        mScroller.startScroll(0, -getScrollY(), 0, getScrollY(), 1000);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, -mScroller.getCurrY());
            postInvalidate();
        }
    }
}
