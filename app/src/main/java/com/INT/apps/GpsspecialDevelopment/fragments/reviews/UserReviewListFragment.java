package com.INT.apps.GpsspecialDevelopment.fragments.reviews;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review_;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserReviewListFragment extends AbstractReviewListFragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_PAGE_TITLE = "page_title";

    // TODO: Rename and change types of parameters
    private String mUserId;
    private String mPageTitle;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserReviewListFragment newInstance(String userId, String title)
    {
        UserReviewListFragment fragment = new UserReviewListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_PAGE_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public UserReviewListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if(getArguments() != null)
        {
            mUserId = getArguments().getString(ARG_USER_ID);
            mPageTitle = getArguments().getString(ARG_PAGE_TITLE);
        }
        setDoPagination(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    ReviewPaginationQuery getReviewPaginationQuery()
    {
        ReviewPaginationQuery query = new ReviewPaginationQuery();
        query.setMethod("user");
        query.setNamedParameter("user",mUserId);
        return query;
    }

    @Override
    public String getPagerWindowTitle()
    {
        return mPageTitle;
    }

    @Override
    protected ReviewPagingAdapter getPagingAdapter()
    {
        return new ReviewPagingAdapter(getActivity())
        {
            private TextView mListingTitle;
            private TextView mReviewedByUser;
            private ImageView mListingImage;
            private ImageView mRatingImage;
            private TextView mReviewDate;
            private TextView mReviewText;
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                Review_ review = getItem(position).getReview();
                if(convertView == null)
                {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_review_row, null);
                }
                mListingTitle = (TextView)convertView.findViewById(R.id.listing_name);
                mReviewedByUser = (TextView)convertView.findViewById(R.id.reviewed_by_user);
                mReviewDate = (TextView)convertView.findViewById(R.id.review_date);
                mReviewText = (TextView)convertView.findViewById(R.id.review);
                mListingImage = (ImageView)convertView.findViewById(R.id.listing_image);
                mRatingImage = (ImageView)convertView.findViewById(R.id.rating);
                mListingTitle.setText(review.getListingTitle());
                Date dateObject = DateParser.fromStringToDate(review.getCreated());
                String dateString = "";//review.getCreated();
                if(dateObject != null)
                {
                    PrettyTime prettyTime = new PrettyTime();
                    //DateFormat df = new SimpleDateFormat(getContext().getString( R.string.simple_date_format));
                    dateString = prettyTime.format(dateObject);
                }
                mReviewDate.setText(dateString);
                mReviewText.setText(review.getBody());
                mRatingImage.setImageDrawable(getRatingDrawable(review));
                mReviewedByUser.setText(getString(R.string.reviewed_by_user,review.getPoster().getUsername()));
                String listingImage = CvUrls.getImageUrlForSize(review.getListingImage(), 80, 80, true);
                if(listingImage != null && listingImage.length() > 0)
                {
                    Picasso.get().load(listingImage).fit().placeholder(R.drawable.avatar_placeholder80).into(mListingImage);
                }
                return convertView;
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        getListView().setDivider(new ColorDrawable(android.R.color.transparent));
        super.onViewCreated(view, savedInstanceState);
    }
}
