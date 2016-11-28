package com.kelin.scrollablepanel.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * A recycler view can pin first item view in the top.
 *
 */
public class PinFirstItemRecyclerView extends RecyclerView {
    private View pinHeaderView = null;
    private boolean needPin = true;

    public PinFirstItemRecyclerView(Context context) {
        super(context);
    }

    public PinFirstItemRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinFirstItemRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNeedPin(boolean needPin) {
        this.needPin = needPin;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (needPin && pinHeaderView != null) {
            canvas.save();
            Rect rect = new Rect(0, 0, pinHeaderView.getMeasuredWidth(), pinHeaderView.getMeasuredHeight());
            canvas.clipRect(rect);
            pinHeaderView.draw(canvas);
            canvas.restore();
        }

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (needPin) {
            pinHeaderView = pinHeaderView == null ? getChildAt(0) : pinHeaderView;
        }
    }


}
