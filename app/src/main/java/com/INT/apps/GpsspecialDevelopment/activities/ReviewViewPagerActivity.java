package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.ReviewViewFragment;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ReviewViewPagerActivity extends BaseActivity
{
    static public String ARG_PAGING_QUERY="paging_query";
    static public String ARG_TITLE="title";
    static public String ARG_PAGER_POSITION="position";
    static public String ARG_TOTAL_COUNT="count";

    private ReviewPaginationQuery mReviewPaginationQueryString;
    private int mTotalCount;
    private int mDefualtPosition;
    @InjectView(R.id.review_list_pager)
    protected ViewPager mViewPager;


    @InjectView(R.id.next_pager)
    protected ImageButton nextPager;
    @InjectView(R.id.prev_pager)
    protected ImageButton prevPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view_pager);
        ButterKnife.inject(this);
        mDefualtPosition = getIntent().getIntExtra(ARG_PAGER_POSITION,0);
        mTotalCount = getIntent().getIntExtra(ARG_TOTAL_COUNT,0);
        mReviewPaginationQueryString = new ReviewPaginationQuery(getIntent().getStringExtra(ARG_PAGING_QUERY));
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(getIntent().getStringExtra(ARG_TITLE));
        String method = mReviewPaginationQueryString.getParameter("method");
        if(method.equals("listing"))
        {
            //toolbar.getMenu().add(1, 1, 1, getResources().getString(R.string.write_review)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            TextView textView = new TextView(this);
            textView.setText(R.string.write_review);
            textView.setClickable(true);
            textView.setTextAppearance(this,R.style.toolbar_text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBusinessWindow();
                }
            });
            //textView.setLayoutParams(layoutParams);
            toolbar.addView(textView);
        }else
        {
            //toolbar.getMenu().add(1, 1, 1, getResources().getString(R.string.view_business)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        if(mTotalCount > 0)
        {
            ViewPager pager = (ViewPager)findViewById(R.id.review_list_pager);
            pager.setAdapter(new ReviewViewPager(getSupportFragmentManager()));
            pager.setOffscreenPageLimit(1);
            pager.setCurrentItem(mDefualtPosition-1);
            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position)
                {
                    setPagerNavVisbilty(position);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setPagerNavVisbilty(mDefualtPosition-1);
        }
    }

    private void setPagerNavVisbilty(int position)
    {
        if(position == 0)
        {
            prevPager.setVisibility(View.GONE);
            nextPager.setVisibility(View.VISIBLE);
        }else if(position == (mViewPager.getAdapter().getCount()-1))
        {
            prevPager.setVisibility(View.VISIBLE);
            nextPager.setVisibility(View.GONE);
        }else
        {
            prevPager.setVisibility(View.VISIBLE);
            nextPager.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.next_pager)
    protected void nextPager()
    {
        int totalCount = mViewPager.getAdapter().getCount() -1;
        if(totalCount > mViewPager.getCurrentItem())
        {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
        }
    }

    @OnClick(R.id.prev_pager)
    protected void prevPager()
    {
        if(mViewPager.getCurrentItem() > 0 )
        {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 1)
        {
            openBusinessWindow();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openBusinessWindow()
    {
        ReviewViewPager pager = (ReviewViewPager)mViewPager.getAdapter();
        ReviewViewFragment fragment = (ReviewViewFragment)pager.getItemFragment(mViewPager.getCurrentItem());
        if(fragment != null)
        {
            String method = mReviewPaginationQueryString.getParameter("method");
            if(method.equals("user"))
            {
                fragment.openBusiness();
            }else if(method.equals("listing"))
            {
                fragment.openAddReview();
            }
        }
    }

    class ReviewViewPager extends FragmentStatePagerAdapter
    {
        HashMap<Integer,WeakReference<Fragment>> fragments = new HashMap<>();
        ReviewViewPager(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            if(fragments.containsKey(position))
            {
                Fragment fragment = fragments.get(position).get();
                return fragment;
            }
            Fragment fragment = ReviewViewFragment.newInstance(mReviewPaginationQueryString, position + 1);
            fragments.put(position, new WeakReference<Fragment>(fragment));
            return fragment;
        }

        public Fragment getItemFragment(int position)
        {
            WeakReference<Fragment> fragmentReference = fragments.get(position);
            if(fragmentReference == null)
            {
                return null;
            }
            Fragment fragment = fragmentReference.get();
            return fragment;
        }

        @Override
        public int getCount()
        {
            return mTotalCount;
        }


    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }
}
