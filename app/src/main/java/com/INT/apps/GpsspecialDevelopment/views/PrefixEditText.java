package com.INT.apps.GpsspecialDevelopment.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;

import timber.log.Timber;

public class PrefixEditText extends AppCompatEditText {
    float mOriginalLeftPadding = -1;

    public PrefixEditText(Context context) {
        super(context);
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefixEditText(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculatePrefix();
    }

    @SuppressLint("NewApi")
    private void calculatePrefix() {
        if (mOriginalLeftPadding == -1) {
            String prefix = (String) getTag();
            float[] widths = new float[prefix.length()];
            getPaint().getTextWidths(prefix, widths);
            float textWidth = 0;
            for (float w : widths) {
                textWidth += w;
            }
            if (checkRtl()) {
                mOriginalLeftPadding = getCompoundPaddingRight();
            } else {
                mOriginalLeftPadding = getCompoundPaddingLeft();
            }

            if (VERSION.SDK_INT == VERSION_CODES.JELLY_BEAN) {
                setLeftPrefixPadding(textWidth);
            } else {
                setPaddingIfRtl(textWidth);
            }
        }
    }

    private void setLeftPrefixPadding(float textWidth) {
        setPadding((int) (textWidth + mOriginalLeftPadding),
                getPaddingRight(), getPaddingTop(),
                getPaddingBottom());
    }

    private void setRightPrefixPadding(float textWidth) {
        Timber.i("setRightPadding");
        setPadding(getPaddingLeft(),
                (int) (textWidth + mOriginalLeftPadding), getPaddingTop(),
                getPaddingBottom());
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
    private void setPaddingIfRtl(float textWidth) {
        if (checkRtl()) {
            setRightPrefixPadding(textWidth);
        } else {
            setLeftPrefixPadding(textWidth);
        }
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
    private boolean checkRtl() {
        return VERSION.SDK_INT != VERSION_CODES.JELLY_BEAN && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String prefix = (String) getTag();
        canvas.drawText(prefix, mOriginalLeftPadding,
                getLineBounds(0, null), getPaint());
    }
}
