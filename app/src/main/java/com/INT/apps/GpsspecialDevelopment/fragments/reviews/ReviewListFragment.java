package com.INT.apps.GpsspecialDevelopment.fragments.reviews;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewListFragment extends AbstractReviewListFragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LISTING_ID = "listing_id";
    private static final String ARG_DO_PAGINATION = "doPagination";
    private static final String ARG_LIMIT = "limit";
    private static final String ARG_LISTING_TITLE = "listingTitle";

    // TODO: Rename and change types of parameters
    private String mListingId;
    private String mListingTitle;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewListFragment newInstance(String listingId, boolean doPagination,int limit,String title)
    {
        ReviewListFragment fragment = new ReviewListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_ID, listingId);
        args.putBoolean(ARG_DO_PAGINATION, doPagination);
        args.putInt(ARG_LIMIT, limit);
        args.putString(ARG_LISTING_TITLE,title);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if(getArguments() != null)
        {
            mListingId = getArguments().getString(ARG_LISTING_ID);
            mDoPagination = getArguments().getBoolean(ARG_DO_PAGINATION);
            Integer limit = getArguments().getInt(ARG_LIMIT);
            mListingTitle = getArguments().getString(ARG_LISTING_TITLE);
            setPageLimit(limit);
            setDoPagination(mDoPagination);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    ReviewPaginationQuery getReviewPaginationQuery()
    {
        ReviewPaginationQuery query = new ReviewPaginationQuery();
        query.setMethod("listing");
        query.setNamedParameter("listingId",mListingId);
        return query;
    }

    @Override
    public String getPagerWindowTitle()
    {
        return getResources().getString(R.string.entity_reviews,mListingTitle);
    }
}
