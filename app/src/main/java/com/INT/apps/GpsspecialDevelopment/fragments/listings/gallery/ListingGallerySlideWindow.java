package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Asset;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Assets;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListingGallerySlideWindow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingGallerySlideWindow extends android.support.v4.app.DialogFragment
{

    private GalleryProvider mGalleryProvider;
    boolean dismissDialog = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ListingGallerySlideWindow.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingGallerySlideWindow newInstance()
    {
        ListingGallerySlideWindow fragment = new ListingGallerySlideWindow();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListingGallerySlideWindow() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.app.DialogFragment.STYLE_NORMAL, R.style.AppTheme_DialogFullScreen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog resultDialog = super.onCreateDialog(savedInstanceState);
        if (resultDialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            resultDialog.getWindow().setLayout(width, height);
            resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return resultDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_listing_gallery_slide_window, container, false);
        ViewPager pager = (ViewPager)view.findViewById(R.id.gallery_pager);
        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        if(mGalleryProvider.getAssets() == null)
        {
            dismissDialog = true;
            return view;
        }
        GalleryFragmentPager adapter = new GalleryFragmentPager(fm,mGalleryProvider.getAssets());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        pager.setClipToPadding(false);
        pager.setCurrentItem(mGalleryProvider.getPosition());
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return  view;
    }

    @Override
    public void onResume()
    {
        if(dismissDialog)
        {
            dismissDialog = false;
            dismiss();
        }
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            if(activity instanceof GalleryProvider)
            {
                mGalleryProvider = (GalleryProvider) activity;
            }else
            {
                mGalleryProvider = (GalleryProvider) getTargetFragment();
            }
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mGalleryProvider = null;
    }

    public void show()
    {
        //super.show(getFragmentManager(),getTag());
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface GalleryProvider
    {
        public Assets getAssets();

        public int getPosition();
    }

    static class GalleryFragmentPager extends FragmentStatePagerAdapter
    {
        Assets mAssets;
        GalleryFragmentPager(android.support.v4.app.FragmentManager fm,Assets assets)
        {
            super(fm);
            mAssets = assets;
        }

        @Override
        public int getCount()
        {
            return mAssets.assets.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            Asset asset = mAssets.assets.get(position);
            ListingGallerySlideFragment fragment = ListingGallerySlideFragment.newInstance(asset.id,asset.fileUrl);
            return fragment;
        }
    }
}
