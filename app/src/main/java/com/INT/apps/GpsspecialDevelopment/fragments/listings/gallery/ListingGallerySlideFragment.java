package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListingGallerySlideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingGallerySlideFragment extends android.support.v4.app.Fragment
{
    static public String ARG_ASSET_ID = "listing_id";
    static public String ARG_IMAGE_URL="image_url";
    // TODO: Rename and change types of parameters
    private String mAssetId;
    private String mImageUrl;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ListingGallerySliderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingGallerySlideFragment newInstance(String assetId, String imageUrl)
    {
        ListingGallerySlideFragment fragment = new ListingGallerySlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ASSET_ID,assetId);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public ListingGallerySlideFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mAssetId = getArguments().getString(ARG_ASSET_ID);
            mImageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.listing_gallery_slide, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.gallery_image);
        String imageUrl = CvUrls.getImageUrl(mImageUrl);
        Picasso.get().load(imageUrl).into(imageView);
        return view;
    }
}
