package com.INT.apps.GpsspecialDevelopment.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

import timber.log.Timber;

/**
 * @author Michael Soyma (Created on 9/4/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class MapViewInsideScrollView extends MapView {

    public MapViewInsideScrollView(Context context) {
        super(context);
    }

    public MapViewInsideScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapViewInsideScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                Timber.d("unlocked");
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
                Timber.d("locked");
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
