package com.INT.apps.GpsspecialDevelopment.fragments.reviews;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.activities.ReviewAddActivity;
import com.INT.apps.GpsspecialDevelopment.activities.UserProfileActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review_;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewViewLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewViewLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ReviewViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewViewFragment extends Fragment
{
    private static final String ARG_QUERY = "listing_id";
    private static final String ARG_POSITION = "position";

    // TODO: Rename and change types of parameters
    private ReviewPaginationQuery mQuery;
    private int mPosition;

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

    @InjectView(R.id.listing_title)
    TextView listingTitleView;

    @Optional
    @InjectView(R.id.checkin_icon)
    ImageView checkinIcon;
    @Optional
    @InjectView(R.id.checkin_count)
    TextView checkinCount;

    @Optional
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @InjectView(R.id.avatar_wrapper)
    View mAvatarWrapper;

    @InjectView(R.id.review_rating_list)
    LinearLayout ratingListWrapper;

    Review mReview;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ReviewViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewViewFragment newInstance(ReviewPaginationQuery query, int position) {
        ReviewViewFragment fragment = new ReviewViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query.toString());
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewViewFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mQuery = new ReviewPaginationQuery(getArguments().getString(ARG_QUERY));
            mPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_review_view, container, false);
        ButterKnife.inject(this,view);
        mProgressBar.setVisibility(View.VISIBLE);
        mAvatarWrapper.setVisibility(View.GONE);
        IOBus.getInstance().post(new ReviewViewLoadEvent(mQuery,mPosition));
        return view;
    }

    @Override
    public void onPause()
    {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Subscribe
    public void onReviewLoaded(ReviewViewLoadedEvent event)
    {
        if(event.getPosition() != mPosition)
        {
            return;
        }
        mProgressBar.setVisibility(View.GONE);
        mAvatarWrapper.setVisibility(View.VISIBLE);
        Review_ review = event.getReview().getReview();
        mReview = event.getReview();
        Date dateObject = DateParser.fromStringToDate(review.getCreated());
        String dateString = "";//review.getCreated();
        if(dateObject != null)
        {
            DateFormat df = new SimpleDateFormat(getActivity().getString(R.string.simple_date_format));
            dateString = df.format(dateObject);
        }
        body.setText(review.getBody());
        title.setText(review.getTitle());
        username.setText(review.getPoster().getUsername());
        String rating = review.getAvgScore();
        Float usrAvgF = Float.parseFloat(rating);
        ratingStars.setImageDrawable(getRatingDrawable(usrAvgF.intValue(),false));
        if(review.getRatings().length > 1) {
            for (Review_.ReviewRating reviewRating : review.getRatings()) {
                ratingListWrapper.addView(getSubRatingView(reviewRating));
            }
        }

        date.setText(dateString);
        String avatarUrl = review.getPoster().getAvatar();
        if(avatarUrl != null && avatarUrl != "")
        {
            avatarUrl = CvUrls.getImageUrlForSize(avatarUrl, 80, 80, true);
            Picasso.get().load(avatarUrl).fit().placeholder(R.drawable.avatar_placeholder80).into(avatar);
        }
        if(checkinCount != null)
        {
            if(review.getCheckedIn() > 0)
            {
                checkinCount.setVisibility(View.VISIBLE);
                checkinIcon.setVisibility(View.VISIBLE);
                checkinCount.setText(getActivity().getResources().getQuantityString(R.plurals.checkin_count, review.getCheckedIn(), review.getCheckedIn()));
            }else
            {
               checkinCount.setVisibility(View.GONE);
               checkinIcon.setVisibility(View.GONE);
            }
        }

        listingTitleView.setText(getResources().getString(R.string.posted_in_business,review.getListingTitle()));
    }

    public void openBusiness()
    {
        if(mReview == null)
        {
            return;
        }
        Intent intent = new Intent(getActivity(), ListingViewActivity.class);
        intent.putExtra(ListingViewActivity.ARG_LISTING_ID,mReview.getReview().getListingId());
        intent.putExtra(ListingViewActivity.ARG_LISTING_TITLE,mReview.getReview().getListingTitle());
        startActivity(intent);
    }

    public void openAddReview()
    {
        if(mReview == null)
        {
            return;
        }
        Intent intent = new Intent(getActivity(), ReviewAddActivity.class);
        intent.putExtra(ReviewAddActivity.ARG_LISTING_ID,mReview.getReview().getListingId());
        intent.putExtra(ReviewAddActivity.ARG_LISTING_TITLE,mReview.getReview().getListingTitle());
        startActivity(intent);
    }
    private Drawable getRatingDrawable(Integer rating,Boolean small)
    {
        int usrAvg = rating;
        Resources resources = getActivity().getResources();
        String ratingImage = "stars_med_";
        if(small)
        {
            usrAvg = usrAvg*10;
            ratingImage = "stars_small_";
        }
        if(usrAvg > 0)
        {
            ratingImage = ratingImage+usrAvg;
        }else
        {
            ratingImage = ratingImage+"_0";
        }
        int resourceId = resources.getIdentifier(ratingImage, "drawable", getActivity().getPackageName());
        Drawable dr = resources.getDrawable(resourceId);
        return  dr;
    }
    private View getSubRatingView(Review_.ReviewRating rating)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.review_subrating,null);
        TextView label = (TextView)view.findViewById(R.id.rating_label);
        ImageView imageView = (ImageView)view.findViewById(R.id.rating_image);
        imageView.setImageDrawable(getRatingDrawable(rating.rating.intValue(),true));
        label.setText(rating.label);
        return view;
    }

    @OnClick(R.id.avatar_wrapper)
    public void openUserProfile()
    {
        if(mReview == null)
        {
            return;
        }
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.ARG_USER_ID,mReview.getReview().getUserId());
        startActivity(intent);
    }
}
