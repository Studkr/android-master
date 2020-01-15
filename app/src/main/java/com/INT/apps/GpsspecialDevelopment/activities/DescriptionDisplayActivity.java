package com.INT.apps.GpsspecialDevelopment.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;

public class DescriptionDisplayActivity extends BaseActivity
{
    public static String ARG_TITLE="title";
    public static String ARG_DESCRIPTION_LIST="description";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_display);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(getIntent().getStringExtra(ARG_TITLE));
        String[] descriptionList = getIntent().getStringArrayExtra(ARG_DESCRIPTION_LIST);
        for(int i=0;i<descriptionList.length;i++)
        {
            showDescription(descriptionList[i]);
        }
    }

    private void showDescription(String description)
    {

        TextView textView = new TextView(this);
        textView.setLinksClickable(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,0,10);
        textView.setLayoutParams(layoutParams);
        textView.setText(Html.fromHtml(description));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(Color.rgb(51,51,51));
        ((LinearLayout)findViewById(R.id.description_wrapper)).addView(textView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView1 = new TextView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(10,10,0,10);
        textView1.setLayoutParams(layoutParams1);
        textView1.setBackgroundColor(Color.rgb(51, 51, 51));
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
        textView1.setLinksClickable(true);
        ((LinearLayout)findViewById(R.id.description_wrapper)).addView(textView1,LinearLayout.LayoutParams.MATCH_PARENT, 1);
    }

    @Override
    protected boolean useNavigationDrawer()
    {
        return false;
    }
}
