package com.github.dkharrat.nexusdialog.controllers;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.github.dkharrat.nexusdialog.R;

/**
 * Created by shrey on 18/6/15.
 */
public class RatingController extends LabeledFieldController
{
    private int mNumRatings=5;
    private RatingBar mRatingBar;
    public RatingController(Context ctx, String name, String labelText, boolean isRequired)
    {
        super(ctx,name,labelText,isRequired);
        setLabelOrientation(LABEL_ORIENTATION_VERTICAL);
        setShowLabel(true);
    }

    @Override
    public void setShowLabel(boolean showLabel)
    {
        super.setShowLabel(true);
    }

    @Override
    protected View createFieldView()
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RatingBar ratingBar = (RatingBar)inflater.inflate(R.layout.rating_bar,null);//new RatingBar(getContext());
        mRatingBar = ratingBar;
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setLayoutParams(new ActionBar.LayoutParams( ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                if(fromUser == true) {
                    ratingBar.setRating(rating);
                    if(rating > 0) {
                        getModel().setValue(getName(), (new Float(rating)).toString());
                    }else {
                        getModel().setValue(getName(), "");
                    }
                }
            }
        });
        return ratingBar;
    }

    @Override
    public void refresh()
    {
        Object value = getModel().getValue(getName());
        if(value != null)
        {
            if(value.toString().length() > 0) {
                mRatingBar.setRating(Float.parseFloat(value.toString()));
            }else {
                mRatingBar.setRating(0f);
            }
        }
    }
}
