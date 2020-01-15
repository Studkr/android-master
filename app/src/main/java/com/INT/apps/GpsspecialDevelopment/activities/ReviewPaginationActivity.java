package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.AbstractReviewListFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.ReviewListFragment;

public class ReviewPaginationActivity extends AbstractReviewPaginationActivity
{
    public static String ARG_LISTING_ID="listing_id";
    public static String ARG_LISTING_TITLE="listing_title";

    private String mListingId;
    private String mListingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mListingId = getIntent().getStringExtra(ARG_LISTING_ID);
        mListingTitle = getIntent().getStringExtra(ARG_LISTING_TITLE);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = setToolbar(true);
        getToolbar().setTitle(getResources().getString(R.string.entity_reviews,mListingTitle));
        TextView textView = new TextView(this);
        textView.setText(R.string.write_review);
        textView.setTextAppearance(this,R.style.toolbar_text);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddReview();
            }
        });
        toolbar.addView(textView);
    }

    @Override
    AbstractReviewListFragment getReviewFragment()
    {
        ReviewListFragment fragment = ReviewListFragment.newInstance(mListingId,true,10,mListingTitle);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == 1)
        {
            openAddReview();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAddReview()
    {
        Intent intent = new Intent(this,ReviewAddActivity.class);
        intent.putExtra(ReviewAddActivity.ARG_LISTING_ID,mListingId);
        intent.putExtra(ReviewAddActivity.ARG_LISTING_TITLE,mListingTitle);
        startActivity(intent);
    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }
}
