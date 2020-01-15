package com.INT.apps.GpsspecialDevelopment.views.decorator;

import android.graphics.drawable.Drawable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

/**
 * Created by shrey on 14/5/15.
 */
//may cause leak
public class ClearableEditTextDecorator implements ViewDecorator, TextWatcher, View.OnFocusChangeListener, View.OnTouchListener {
    private EditText textView;
    private Drawable xD;
    private boolean showing = false;
    private int indexDrawable;
    private Drawable anotherSideDrawable;

    public ClearableEditTextDecorator(EditText editText) {
        textView = editText;
        decorate();
        indexDrawable = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL ? 0 : 2;
    }

    @Override
    public void decorate() {
        xD = textView.getCompoundDrawables()[2];
        anotherSideDrawable = textView.getCompoundDrawables()[0];
        if (xD == null) {
            xD = textView.getContext().getResources().getDrawable(android.R.drawable.presence_offline);
        }
        xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
        setClearIconVisible(false);
        textView.setOnFocusChangeListener(this);
        textView.setOnTouchListener(this);
        textView.addTextChangedListener(this);
    }

    private void setClearIconVisible(boolean visible) {
        boolean wasVisible = (textView.getCompoundDrawables()[indexDrawable] != null);
        if (visible != wasVisible) {
            if (visible) {
                showing = true;
            }
            Drawable x = visible ? xD : null;
            if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                textView.setCompoundDrawables(x, textView.getCompoundDrawables()[1], anotherSideDrawable, textView.getCompoundDrawables()[3]);
            } else {
                textView.setCompoundDrawables(anotherSideDrawable, textView.getCompoundDrawables()[1], x, textView.getCompoundDrawables()[3]);
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setClearIconVisible(isNotEmpty());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isNotEmpty() {
        return textView.getText() != null && textView.getText().length() > 0;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && isNotEmpty()) {
            setClearIconVisible(true);
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (textView.getCompoundDrawables()[indexDrawable] != null && showing) {
            boolean tappedX = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL ?
                    (event.getX() < (textView.getPaddingLeft() + xD.getIntrinsicWidth())) :
                    (event.getX() > (textView.getWidth() - textView.getPaddingRight() - xD.getIntrinsicWidth()));
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    textView.setText("");
                    textView.requestFocus();
                    return true;
                }
            }
        }
        return false;
    }
}
