package com.suntend.arktoolbox.fragment.toolbox.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

@SuppressLint("AppCompatCustomView")
public class CustomSpinnerView extends Spinner {
    private boolean mOpenInitialed = false;
    private OnSpinnerPopStatusListener mListener;
    private int mSpinnerId;

    public CustomSpinnerView(Context context) {
        super(context);
    }

    public CustomSpinnerView(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public interface OnSpinnerPopStatusListener {
        void onSpinnerOpened(int spinnerId);

        void onSpinnerClosed(int spinnerId);
    }

    public void setSpinnerPopStatusListener(OnSpinnerPopStatusListener listener) {
        this.mListener = listener;
    }

    public void setSpinnerView(int spinnerId) {
        this.mSpinnerId = spinnerId;
    }

    @Override
    public boolean performClick() {
        // 展示之前会执行这个方法
        mOpenInitialed = true;
        if (mListener != null) {
            mListener.onSpinnerOpened(mSpinnerId);
        }
        return super.performClick();
    }

    public void performClosed() {
        mOpenInitialed = false;
        if (mListener != null) {
            mListener.onSpinnerClosed(mSpinnerId);
        }
    }

    public boolean hasBeenOpened() {
        return mOpenInitialed;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // 通过窗口的焦点获取来判断  spinner是否获得焦点
        if (hasBeenOpened() && hasWindowFocus) {
            performClosed();
        }
    }
}