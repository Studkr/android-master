package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.CvSettings;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.DoBusinessCheckIn;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.UserCheckedInEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FilterBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckinFragment extends DialogFragment
{

    View mainView;
    String mListingName;
    String mListingId;
    String mListingViewUrl;
    static String ARG_LISTING_ID="listing_id";
    static String ARG_LISTING_TITLE="listing_title";
    static String ARG_LISTING_VIEW_URL="listing_view_url";
    boolean mSaving = false;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @InjectView(R.id.checkin_comment)
    EditText mCheckinComment;
    ShareDialog mFbShareDialog;
    CallbackManager mFbCallbackManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FilterBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckinFragment newInstance(String listingId,String listingName,String listingViewUrl)
    {
        CheckinFragment fragment = new CheckinFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_LISTING_ID,listingId);
        arguments.putString(ARG_LISTING_TITLE,listingName);
        arguments.putString(ARG_LISTING_VIEW_URL,listingViewUrl);
        fragment.setArguments(arguments);
        return fragment;
    }

    public CheckinFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mListingId = getArguments().getString(ARG_LISTING_ID);
        mListingName = getArguments().getString(ARG_LISTING_TITLE);
        mListingViewUrl = getArguments().getString(ARG_LISTING_VIEW_URL);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mFbShareDialog = new ShareDialog(this);
        mFbCallbackManager = CallbackManager.Factory.create();
        mFbShareDialog.registerCallback(mFbCallbackManager,new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getActivity(),"Shared On Facebook",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {

            }
        });
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_check_in, null);
        mainView = v;
        Listing_ listing = (Listing_) JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                .getCachedObject("Listing",mListingId,Listing_.class);
        String dialogTitle ="";
        if(listing != null)
        {
            ListingRowViewHolder rowViewHolder = new ListingRowViewHolder(60,60);
            rowViewHolder.setViewContent(getActivity(),listing,mainView);
            dialogTitle = getResources().getString(R.string.checkin_in_business,listing.getTitle());

        }
        ButterKnife.inject(this,mainView);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(dialogTitle)
                .create();

        return alertDialog;
    }

    private void showShareUI()
    {
        getDialog().setTitle(R.string.share_your_checkin);
        mProgressBar.setVisibility(View.GONE);
        mainView.findViewById(R.id.checkin_form).setVisibility(View.GONE);
        mainView.findViewById(R.id.do_checkin).setVisibility(View.GONE);
        mainView.findViewById(R.id.share_block).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.close).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.do_checkin)
    public void doCheckIn()
    {

        if(mSaving == true)
        {
            return;
        }
        mSaving = true;
        mProgressBar.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new DoBusinessCheckIn(mListingId,mCheckinComment.getText().toString()));
    }

    @Subscribe
    public void onUserCheckedIn(UserCheckedInEvent event)
    {
        showShareUI();
    }


    @OnClick(R.id.facbook_share)
    public void shareOnFacebook()
    {
        if(ShareDialog.canShow(ShareLinkContent.class))
        {
            String username = UserSession.getUserSession().getSessionUser().getUsername();
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getResources().getString(R.string.user_checked_in_business,mListingName))
                    .setContentDescription(getResources().getString(R.string.user_checked_in_business,mListingName))
                    .setContentUrl(Uri.parse(CvUrls.getAsUrl(CvSettings.getServerUrl() + mListingViewUrl))).build();
            mFbShareDialog.show(shareLinkContent);
        }
    }

    @OnClick(R.id.twitter_share)
    public void shareOnTwitter()
    {
        String tweetTitle = getResources().getString(R.string.user_checked_in_business,mListingName);
        String viewUrl = CvSettings.getServerUrl() + mListingViewUrl;
        String shareUrl = "https://twitter.com/intent/tweet?text="+tweetTitle+"&url="+viewUrl;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(shareUrl));
        startActivity(intent);

    }
    @OnClick(R.id.close)
    public void dismissWindow()
    {
        getDialog().dismiss();
        ((ListingViewActivity)getActivity()).onUserCheckIn(true);
        Toast.makeText(getActivity(),R.string.checked_in_successfully,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume()
    {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }
}