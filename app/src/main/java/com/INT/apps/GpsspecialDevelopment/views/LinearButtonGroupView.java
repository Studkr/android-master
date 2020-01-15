package com.INT.apps.GpsspecialDevelopment.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;

/**
 * Created by shrey on 17/8/15.
 */
public class LinearButtonGroupView extends LinearLayout
{

    private boolean initDone = false;
    ListAdapter mListAdapter;
    OnItemClickListener mItemClickListener;
    public LinearButtonGroupView(Context context,AttributeSet set)
    {
        super(context,set);
        init();
    }
    public LinearButtonGroupView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        init();
    }

    private void init()
    {
        if(initDone == true)
        {
            return;
        }
        setOrientation(HORIZONTAL);
        initDone = true;
        View view = inflate(getContext(),R.layout.linear_button_group,this);
    }
    public void setListAdapter(ListAdapter listAdapter)
    {
        mListAdapter = listAdapter;
        refreshList();
    }
    private void refreshList()
    {
        init();
        final LinearLayout wrapper = (LinearLayout)findViewById(R.id.items_row);
        int count = mListAdapter.getCount();
        for(int i=0;i<count;i++)
        {
            View view = mListAdapter.getView(i,null,wrapper);
            view.setTag(i);
            view.setClickable(true);
            setClickEvent(view,i);
            wrapper.addView(view);
            if(i < count -1)
            {
                TextView textView = new TextView(getContext());
                textView.setBackground(new ColorDrawable(getResources().getColor(R.color.button_grey)));
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2));
                wrapper.addView(textView);
            }
        }
        wrapper.setClickable(true);
    }

    private void setClickEvent(View view,final int position)
    {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mItemClickListener != null)
                {
                    LinearLayout wrapper = (LinearLayout)findViewById(R.id.items_row);
                    mItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    public void setOnItemClickListner(OnItemClickListener mClickListener)
    {
        mItemClickListener = mClickListener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view,int position);
    }

    public ListAdapter getListAdapter() {
        return mListAdapter;
    }
}
