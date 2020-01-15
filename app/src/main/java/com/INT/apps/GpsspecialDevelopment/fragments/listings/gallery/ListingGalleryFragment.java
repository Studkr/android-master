package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Asset;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Assets;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingGalleryLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingGalleryLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListingGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingGalleryFragment extends Fragment implements ListingGallerySlideWindow.GalleryProvider
{
    public static final String ARG_LISTING_ID = "listing_id";
    public static final String ARG_LIMIT = "limit";
    public static final String ARG_USE_HORIZONTAL="layout_type";

    @InjectView(R.id.gallery_grid)
    GridLayout mGridLayout;

    // TODO: Rename and change types of parameters
    private String mListingId;
    private int mLimit;
    private boolean mUseHorizontalType = false;
    private Assets mAssets;
    private int mLastGridSelected=0;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListingGallerySliderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingGalleryFragment newInstance(String listingId, Integer limit,boolean isHorizontal)
    {
        ListingGalleryFragment fragment = new ListingGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LISTING_ID, listingId);
        args.putInt(ARG_LIMIT, limit);
        args.putBoolean(ARG_USE_HORIZONTAL,isHorizontal);
        fragment.setArguments(args);
        return fragment;
    }

    public ListingGalleryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mListingId = getArguments().getString(ARG_LISTING_ID);
            mLimit = getArguments().getInt(ARG_LIMIT);
            mUseHorizontalType = getArguments().getBoolean(ARG_USE_HORIZONTAL);
            IOBus.getInstance().post(new ListingGalleryLoadEvent(mListingId,mLimit));
        }
        Fragment fragment = getFragmentManager().findFragmentByTag("gallery_window");
        if(fragment != null)
        {
            //getFragmentManager().popBackStack("gallery_window",0);
            getFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        int layout = R.layout.listing_gallery;
        if(mUseHorizontalType)
        {
            layout = R.layout.listing_gallery_horizontal;
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout, container, false);
        ButterKnife.inject(this,view);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mGridLayout.setColumnCount(4);
        }
        return view;
    }

    @Subscribe
    public void assetsLoadedCallback(ListingGalleryLoadedEvent event)
    {
        int i= 0;
        for(Asset asset : event.getAssets().assets)
        {
            addAssetToGrid(asset,i);
            i++;
        }
        mAssets = event.getAssets();
    }

    private void addAssetToGrid( Asset asset,final int j)
    {
        final Integer i = new Integer(j);
        View view = getActivity().getLayoutInflater().inflate(R.layout.listing_gallery_item,null);
        mGridLayout.addView(view);
        ImageView listingImage = (ImageView)view.findViewById(R.id.gallery_image);
        String imageUrl = CvUrls.getImageUrlForSize(asset.fileUrl,100,100,true);
        Picasso.get().load(imageUrl).fit().placeholder(R.drawable.placeholder100).into(listingImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showGalleryWindow(i);
            }
        });
    }

    private void showGalleryWindow(int selectedItem)
    {
        mLastGridSelected = selectedItem;
        ListingGallerySlideWindow fragment = ListingGallerySlideWindow.newInstance();
        getFragmentManager().beginTransaction().add(fragment,"gallery_window").
                commit();
        fragment.setTargetFragment(this,selectedItem);
        fragment.show();
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

    @Override
    public Assets getAssets()
    {
        return mAssets;
    }

    @Override
    public int getPosition() {
        return mLastGridSelected;
    }
}
