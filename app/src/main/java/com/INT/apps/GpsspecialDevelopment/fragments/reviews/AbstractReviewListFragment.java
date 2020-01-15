package com.INT.apps.GpsspecialDevelopment.fragments.reviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ReviewViewPagerActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review_;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewPaginateLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewPaginateLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;
import com.INT.apps.GpsspecialDevelopment.views.EndlessScrollListener;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by shrey on 18/8/15.
 */

/**
 * Works with ScrollView as well if doPagination is false
 * Calculates height of rows and set parent view height after caluclation.
 */
abstract public class AbstractReviewListFragment extends Fragment
{
    boolean mDoPagination = false;
    Integer mPageLimit = 10;
    private boolean loadedFirst = false;
    private ReviewPaging mLastReviewPaging;
    private Object busSubscriber;
    LinearLayout loadinProblemLayout;
    int lastLoadingPage = 1;
    private ReviewPagingAdapter mReviewPagingAdapter;


    protected boolean getDoPagination()
    {
        return mDoPagination;
    }

    protected void setDoPagination(boolean doPagination)
    {
        mDoPagination = doPagination;
    }

    protected Integer getPageLimit()
    {
        return mPageLimit;
    }

    protected void setPageLimit(Integer pageLimit)
    {
        mPageLimit = pageLimit;
    }

    protected void loadPage(Integer page,View view)
    {
        loadinProblemLayout.setVisibility(View.GONE);
        lastLoadingPage = page;
        view.findViewById(R.id.loading_review).setVisibility(View.VISIBLE);
        ReviewPaginationQuery query = getReviewPaginationQuery();
        query.setLimit(getPageLimit().toString());
        query.setPage(page.toString());
        ReviewPaginateLoadEvent event = new ReviewPaginateLoadEvent(query);
        IOBus.getInstance().post(event);
    }

    abstract ReviewPaginationQuery getReviewPaginationQuery();

    abstract String getPagerWindowTitle();


    protected void displayReviews(ReviewPaging reviewPaging)
    {
        final FrameLayout view = (FrameLayout)getView();
        final ListView listView = getListView();
        if(mDoPagination == false)
        {
            final StringBuilder firstDoneCall = new StringBuilder();
            listView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout()
                        {
                            if(firstDoneCall.toString().length() > 0)
                            {
                                int first = listView.getFirstVisiblePosition();
                                int last  = listView.getLastVisiblePosition();
                                listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                if(last > 0)
                                {
                                    int height = 0;
                                    int i = first;
                                    for(i = first;i<=last;i++)
                                    {
                                        if(listView.getChildAt(i) != null)
                                        {
                                            height = height + listView.getChildAt(i).getHeight();
                                        }
                                    }
                                    if(height > 0)
                                    {
                                        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
                                        mReviewPagingAdapter.notifyDataSetChanged();
                                    }
                                }
                            }else
                            {
                                //listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int height = (listView.getHeight() + 2) * (getPagingAdapter().getCount());
                                firstDoneCall.append("a");
                                //height = height+3;
                                mReviewPagingAdapter.notifyDataSetChanged();
                                view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
                            }
                        }
                    }
            );
            listView.setFocusable(false);
            listView.setFocusableInTouchMode(false);
        }
        if(loadedFirst == false)
        {
            loadedFirst = true;
            ReviewPagingAdapter pagingAdapter = getPagingAdapter();
            for(Review review : reviewPaging.getReviews())
            {
                pagingAdapter.add(review);
            }
            pagingAdapter.notifyDataSetChanged();
            listView.setAdapter(pagingAdapter);
            if(mDoPagination == true)
            {
                listView.setOnScrollListener(new EndlessScrollListener(3) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount)
                    {
                        if (mLastReviewPaging != null && mLastReviewPaging.getPaging().getPageCount() >= page) {
                            loadPage(page,getView());
                        }
                    }
                });
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intent = new Intent(getActivity(), ReviewViewPagerActivity.class);
                    intent.putExtra(ReviewViewPagerActivity.ARG_PAGING_QUERY,getReviewPaginationQuery().toString());
                    intent.putExtra(ReviewViewPagerActivity.ARG_PAGER_POSITION,position+1);
                    intent.putExtra(ReviewViewPagerActivity.ARG_TITLE,getPagerWindowTitle());
                    intent.putExtra(ReviewViewPagerActivity.ARG_TOTAL_COUNT,mLastReviewPaging.getPaging().getCount());
                    startActivity(intent);
                }
            });
        }else
        {

            ArrayAdapter<Review> listAdapter = null;
            if(listView.getAdapter() instanceof HeaderViewListAdapter)
            {
                listAdapter = (ArrayAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
            }else
            {
                listAdapter = (ArrayAdapter)listView.getAdapter();
            }
            for(Review review : reviewPaging.getReviews())
            {
                listAdapter.add(review);
            }
            listAdapter.notifyDataSetChanged();
        }
        getView().findViewById(R.id.loading_review).setVisibility(View.GONE);
        loadinProblemLayout.setVisibility(View.GONE);
        mLastReviewPaging = reviewPaging;
    }

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);
        loadinProblemLayout = (LinearLayout)view.findViewById(R.id.loading_problem_layout);
        loadinProblemLayout.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        Button retryButton = (Button)view.findViewById(R.id.retry_connection);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage(lastLoadingPage, getView());
            }
        });
        loadPage(1, view);
        //a fake footer to add border in last row.
        ListView listView = getListView(view);
        TextView footerView = new TextView(getActivity());
        footerView.setLayoutParams(new ListView.LayoutParams(1,1));
        footerView.setBackground(new ColorDrawable(android.R.color.transparent));
        listView.addFooterView(footerView);
        return view;
    }
    protected ListView getListView(View view)
    {
        return (ListView)view.findViewById(R.id.review_list);
    }
    protected ListView getListView()
    {
        return getListView(getView());
    }

    @Override
    public void onResume()
    {
        IOBus.getInstance().register(getBusSubscriber());
        super.onResume();
    }

    @Override
    public void onPause()
    {
        IOBus.getInstance().unregister(getBusSubscriber());
        super.onPause();
    }

    private Object getBusSubscriber()
    {
        if(busSubscriber == null)
        {
            busSubscriber = new Object(){
                @Subscribe
                public void onReviewsLoaded(ReviewPaginateLoadedEvent event)
                {
                    displayReviews(event.getReviewPaging());
                }

                @Subscribe
                public void onApiError(ApiRequestFailedEvent event)
                {
                    getView().findViewById(R.id.loading_review).setVisibility(View.GONE);
                    loadinProblemLayout.setVisibility(View.VISIBLE);
                }

            };
        }
        return busSubscriber;
    }

    protected ReviewPagingAdapter getPagingAdapter()
    {
        if(mReviewPagingAdapter == null) {
            mReviewPagingAdapter = new ReviewPagingAdapter(getActivity());
        }
        return mReviewPagingAdapter;
    }
}

class ReviewPagingAdapter extends ArrayAdapter<Review>
{
    ReviewPagingAdapter(Context context)
    {
        super(context,0,new ArrayList<Review>());
    }
    ReviewPagingAdapter(Context context,ReviewPaging paging)
    {
        super(context,0,paging.getReviews());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ReviewViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_row,null);
            holder = new ReviewViewHolder();
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        }
        holder = (ReviewViewHolder)convertView.getTag();
        Review reviewObj = getItem(position);
        Review_ review = reviewObj.getReview();

        Date dateObject = DateParser.fromStringToDate(review.getCreated());
        String dateString = "";//review.getCreated();
        if(dateObject != null)
        {
            DateFormat df = new SimpleDateFormat(getContext().getString(    R.string.simple_date_format));
            dateString = df.format(dateObject);
        }
        String body = review.getBody();
        body = body.replace("<br />","\\n");
        body = body.replaceAll("\\n+|\\s+"," ");
        holder.body.setText(body);
        holder.title.setText(review.getTitle());
        holder.username.setText(review.getPoster().getUsername());
        holder.ratingStars.setImageDrawable(getRatingDrawable(review));
        holder.date.setText(dateString);
        String avatar = review.getPoster().getAvatar();
        if(avatar != null && avatar != "" && 1==1)
        {
            avatar = CvUrls.getImageUrlForSize(avatar, 50, 50, true);
            Picasso.get().load(avatar).fit().placeholder(R.drawable.avatar_placeholder80).into(holder.avatar);
        }
        if(holder.checkinCount != null)
        {
            if(review.getCheckedIn() > 0)
            {
                holder.checkinCount.setVisibility(View.VISIBLE);
                holder.checkinIcon.setVisibility(View.VISIBLE);
                holder.checkinCount.setText(getContext().getResources().getQuantityString(R.plurals.checkin_count, review.getCheckedIn(), review.getCheckedIn()));
            }else
            {
                holder.checkinCount.setVisibility(View.GONE);
                holder.checkinIcon.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    protected Drawable getRatingDrawable(Review_ review)
    {
        String rating = review.getAvgScore();
        Float usrAvgF = Float.parseFloat(rating);
        int usrAvg = usrAvgF.intValue();
        Resources resources = getContext().getResources();
        String ratingImage = "stars_med_";
        if(usrAvg > 0)
        {
            ratingImage = ratingImage+usrAvg;
        }else
        {
            ratingImage = ratingImage+"_0";
        }
        int resourceId = resources.getIdentifier(ratingImage, "drawable", getContext().getPackageName());
        Drawable dr = resources.getDrawable(resourceId);
        return  dr;
    }

    class ReviewViewHolder
    {
        @InjectView(R.id.review_content)
        TextView body;
        @InjectView(R.id.review_title)
        TextView title;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.username)
        TextView username;
        @InjectView(R.id.user_avatar)
        ImageView avatar;
        @InjectView(R.id.rating_stars)
        ImageView ratingStars;
        @Optional
        @InjectView(R.id.checkin_icon)
        ImageView checkinIcon;
        @Optional
        @InjectView(R.id.checkin_count)
        TextView checkinCount;
    }
}
