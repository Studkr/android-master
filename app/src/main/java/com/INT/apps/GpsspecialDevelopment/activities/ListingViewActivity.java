package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.CvSettings;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.CheckinFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.ListDealsFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.ListingRowViewHolder;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingGalleryFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.ReviewListFragment;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ToggleBookMarkEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingViewLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.LoadListingViewEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class ListingViewActivity extends BaseActivity implements OnMapReadyCallback, ListDealsFragment.DealsListProvider {

    public static String ARG_LISTING_ID = "listing_id";
    public static String ARG_LISTING_TITLE = "listingTitle";

    @InjectView(R.id.main_wrapper)
    protected View listingMainWrapper;

    @InjectView(R.id.listing_view_info_row)
    protected View listingRowView;

    @InjectView(R.id.write_review_button)
    protected Button writeReviewButton;

    @InjectView(R.id.bookmark_listing_button)
    protected Button bookMarkListingButton;

    @InjectView(R.id.unbookmark_listing_button)
    protected Button unBookMarkListingButton;

    @InjectView(R.id.add_photo)
    protected Button addPhotoButton;

    @InjectView(R.id.check_in_business_button)
    protected Button checkInUserButton;

    @InjectView(R.id.checked_in_business_button)
    protected Button checkedInAlreadyButton;

    @InjectView(R.id.listing_map_view)
    public MapView mapView;

    @InjectView(R.id.listing_address)
    public TextView listingAddress;

    @InjectView(R.id.make_phone_call)
    public TextView mPhoneButton;

    @InjectView(R.id.listing_progress)
    public ProgressBar mListingProgressBar;

    @InjectView(R.id.layout_avg_review)
    protected View listingAVGReviewView;


    @Optional
    @InjectView(R.id.review_count)
    TextView reviewCount;

    @Optional
    @InjectView(R.id.ratings)
    ImageView ratings;

    private Listing_ mListing;
    String mListingTitle;
    String mListigId;

    private static int CODE_ADD_REVIEW = 1;
    private static int CODE_ADD_PHOTO = 1;

    private boolean loadReviewsOnResume = false;
    private boolean reLoadingListing = false;

    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_view);
        ButterKnife.inject(this);
        Toolbar toolbar = setToolbar();
        String listingId = mListigId = getIntent().getStringExtra(ARG_LISTING_ID);
        mListingTitle = getIntent().getStringExtra(ARG_LISTING_TITLE);
        mListingProgressBar.setVisibility(View.VISIBLE);
        listingRowView.setVisibility(View.GONE);
        IOBus.getInstance().post(new LoadListingViewEvent(listingId, null, true));
        attachGallery(listingId);
        mapView.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.menu_listing_view);
        toolbar.setNavigationIcon(R.drawable.nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ViewTreeObserver vto = listingMainWrapper.getViewTreeObserver();
        vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Rect scrollBounds = new Rect();
                listingRowView.getHitRect(scrollBounds);
                if (listingRowView.getLocalVisibleRect(scrollBounds)) {
                    // Any portion of the imageView, even a single pixel, is within the visible window
                    //Toast.makeText(ListingViewActivity.this,"Visible",Toast.LENGTH_SHORT).show();
                    //System.out.println("****Visible");
                } else {
                    // NONE of the imageView is within the visible window
                    //Toast.makeText(ListingViewActivity.this,"In-Visible",Toast.LENGTH_SHORT).show();
                    //System.out.println("****In-Visible");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        mapView.onResume();
        if (loadReviewsOnResume == true) {
            loadReviewsOnResume = false;
            if (mListing != null) {
                findViewById(R.id.reviews_container).setVisibility(View.GONE);
                attachReviewsSection(mListing.getId(), false);
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_review) {
            addReview();
            return true;
        }
        if (id == R.id.action_add_photo) {
            openAddPhoto();
            return true;
        }

        if (id == R.id.share || id == R.id.action_share_2) {
            if (mListing != null)
                startActivity(getShareIntent());
        }
        if (id == R.id.bookmark_listing_button) {
            bookMarkListing();
        }
        if (id == R.id.make_phone_call) {
            callBusiness();
        }
        if (id == R.id.open_in_browser) {
            String viewUrl = mListing.getViewUrl();
            if (viewUrl != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUrl));
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void listingViewLoaded(ListingViewLoadedEvent event) {
        Listing_ listing = event.getListing();
        mListingProgressBar.setVisibility(View.GONE);
        listingRowView.setVisibility(View.VISIBLE);
        if (mListing != null && reLoadingListing == false) {
            return;
        }
        mListing = listing;
        setListingView(listing);
        getToolbar().setTitle(listing.getTitle());
        mapView.getMapAsync(this);
        listingAddress.setText(listing.getAddressData());
        if (listing.getReviewCount() > 0) {
            findViewById(R.id.review_list_layout).setVisibility(View.VISIBLE);
            attachReviewsSection(listing.getId(), reLoadingListing);
            reLoadingListing = false;
        } else {
            findViewById(R.id.review_list_layout).setVisibility(View.GONE);
        }
        if (listing.hasDeals()) {
            //SAQIB---------------------------
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

           /* FrameLayout dealLayout = (FrameLayout)findViewById(R.id.deals_list);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            dealLayout.setLayoutParams(params);*/
            //---------------------------
            Fragment fragment = ListDealsFragment.newInstance();
            if (getSupportFragmentManager().findFragmentById(R.id.deals_list) == null || true) {
                getSupportFragmentManager().beginTransaction().replace(R.id.deals_list, fragment).commit();
            }
        }
    }
    private void setListingView(Listing_ listing) {
        View rowView = null;
        if (((ViewGroup) listingRowView).getChildCount() == 0) {
            rowView = getLayoutInflater().inflate(R.layout.listing_view_info_row, (ViewGroup) listingRowView);
        } else {
            rowView = ((ViewGroup) listingRowView).getChildAt(0);
        }

        ListingRowViewHolder viewHolder = new ListingRowViewHolder(0, 0, ListingRowViewHolder.RATING_MEDIUM);
        viewHolder.setViewContent(this, listing, rowView);
        if (listing.getReviewCount() == 0) {
            findViewById(R.id.reviews_container).setVisibility(View.GONE);
        }
        String phoneValue = listing.getPhoneValue();
        if (phoneValue != null && phoneValue.length() > 0) {
            mPhoneButton.setText(phoneValue);
            getToolbar().getMenu().findItem(R.id.make_phone_call).setVisible(true);
        } else {
            getToolbar().getMenu().findItem(R.id.make_phone_call).setVisible(false);
            mPhoneButton.setVisibility(View.GONE);
        }
        setBookMarkButton(listing.isBookMarked());
        newLayoutRating(listing,ListingRowViewHolder.RATING_MEDIUM);

        if (listing.getReviewCount() == 0) {
            listingAVGReviewView.setVisibility(View.GONE);
        }
    }
    private void newLayoutRating(Listing_ listing,String rating){
        if(ratings != null)
        {
            String mRatingType = rating;
            String usrAvgString = listing.getUserAvg();
            double usrAvg = 0d;
            int usrAvgInt = 0;
            if(usrAvgString != null)
            {
                Double usrAvgF = Double.parseDouble(usrAvgString);
                usrAvgInt = (int)(usrAvgF*10);
                usrAvgInt = (usrAvgInt) - (usrAvgInt%5);

            }
            Resources resources = getResources();
            String ratingImage = mRatingType+"0";
            if(usrAvgInt > 0)
            {
                ratingImage = mRatingType+usrAvgInt;
            }
            int resourceId = resources.getIdentifier(ratingImage, "drawable", getPackageName());
            Drawable dr = resources.getDrawable(resourceId);
            ratings.setImageDrawable(dr);
        }

        if(reviewCount != null)
        {
            String reviewsCount = getResources().getQuantityString(R.plurals.review_count, listing.getReviewCount(), listing.getReviewCount());
            reviewCount.setText(reviewsCount);
        }
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        MarkerOptions markerOptions = new MarkerOptions();
        if (mListing != null)
            markerOptions.position(new LatLng(mListing.getLatitude(), mListing.getLongitude())).draggable(false);
        LatLngBounds.Builder lngBuilder = new LatLngBounds.Builder();
        Marker markerOb = googleMap.addMarker(markerOptions);
        lngBuilder.include(markerOb.getPosition());
        LatLngBounds bounds = lngBuilder.build();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), (float) 12.0);

        try {
            googleMap.animateCamera(cameraUpdate);
        } catch (IllegalStateException e) {
            final MapView mapView1 = mapView;
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        @SuppressWarnings("deprecation")
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                mapView1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                mapView1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            googleMap.animateCamera(cameraUpdate);
                        }
                    }
            );
        }
    }

    private void attachGallery(String listingId) {
        Fragment fragment = ListingGalleryFragment.newInstance(listingId, 10, true);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("gallery") == null) {
            fm.beginTransaction().replace(R.id.listing_gallery_wrapper, fragment, "gallery").commit();
        }
    }
    //SAQIB->Reviews visibility is "GONE"
    private void attachReviewsSection(String listingId, boolean forceAdd) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("reviews_section") == null || forceAdd) {
            Fragment fragment = ReviewListFragment.newInstance(listingId, false, 4, mListingTitle);
            fm.beginTransaction().
                    replace(R.id.listing_reviews, fragment, "reviews_section")
                    .commit();
        }
    }

    @OnClick(R.id.open_gallery)
    public void openGallery() {
        if (mListing != null) {
            Intent intent = new Intent(this, ListingAssetViewActivity.class);
            intent.putExtra(ListingViewActivity.ARG_LISTING_ID, mListing.getId());
            startActivity(intent);
        }
    }

    @OnClick({R.id.add_review, R.id.write_review_button})
    public void addReview() {
        if (mListing != null) {
            Intent intent = new Intent(this, ReviewAddActivity.class);
            intent.putExtra(ReviewAddActivity.ARG_LISTING_ID, mListing.getId());
            intent.putExtra(ReviewAddActivity.ARG_LISTING_TITLE, mListing.getTitle());
            startActivityForResult(intent, CODE_ADD_REVIEW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_ADD_REVIEW && resultCode == ReviewAddActivity.RESULT_OK) {
            reloadListing();
        } else if (requestCode == CODE_ADD_PHOTO && resultCode == RESULT_OK) {
            if (mListing != null)
                attachGallery(mListing.getId());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadListing() {
        reLoadingListing = true;
        mListingProgressBar.setVisibility(View.VISIBLE);
        listingRowView.setVisibility(View.GONE);
        IOBus.getInstance().post(new LoadListingViewEvent(mListigId, null, true));
    }

    @OnClick(R.id.view_reviews)
    public void viewAllReviews() {
        if (mListing != null) {
            Intent intent = new Intent(this, ReviewPaginationActivity.class);
            intent.putExtra(ReviewPaginationActivity.ARG_LISTING_ID, mListing.getId());
            intent.putExtra(ReviewPaginationActivity.ARG_LISTING_TITLE, mListing.getTitle());
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick({R.id.add_photo, R.id.add_photo_2})
    public void openAddPhoto() {
        if (mListing != null) {
            Intent intent = new Intent(this, ListingImageAddActivity.class);
            intent.putExtra(ListingImageAddActivity.ARG_LISTING_ID, mListing.getId());
            startActivity(intent);
        }
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mListing.getTitle());
        String viewUrl = CvSettings.getServerUrl() + mListing.getViewUrl();
        Spanned htmlContent = Html.fromHtml(mListing.getTitle() + " <br />" + mListing.getAddressData() + "<br />" + viewUrl);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, htmlContent.toString());
        intent.putExtra(Intent.EXTRA_TEXT, mListing.getTitle() + " \n\n" + mListing.getAddressData() + "\n" + viewUrl);
        return intent;
    }

    @OnClick(R.id.get_directions)
    public void openGetDirections() {
        if (mListing != null) {
            Double lat = mListing.getLatitude();
            Double lng = mListing.getLongitude();
            String url = "http://maps.google.com/maps?daddr=" + lat + "," + lng;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    @OnClick(R.id.make_phone_call)
    public void callBusiness() {
        if (mListing != null) {
            String phoneValue = mListing.getPhoneValue();
            if (phoneValue != null && phoneValue.length() > 0) {
                Uri phoneUri = Uri.fromParts("tel", phoneValue, null);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
                startActivity(callIntent);
            }
        }
    }

    @OnClick(R.id.more_information)
    public void showMoreInformation() {
        if (mListing != null) {
            Intent intent = new Intent(this, ListingExtraInformationActivity.class);
            intent.putExtra(ListingExtraInformationActivity.ARG_LISTING_ID, mListing.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
        }
    }

    public void setBookMarkButton(boolean isBookmarked) {
        if (isBookmarked == false) {
            bookMarkListingButton.setVisibility(View.VISIBLE);
            unBookMarkListingButton.setVisibility(View.GONE);
            getToolbar().getMenu().findItem(R.id.bookmark_listing_button).setVisible(true);
        } else {
            bookMarkListingButton.setVisibility(View.GONE);
            unBookMarkListingButton.setVisibility(View.VISIBLE);
            getToolbar().getMenu().findItem(R.id.bookmark_listing_button).setVisible(false);
        }
    }

    @OnClick(R.id.bookmark_listing_button)
    public void bookMarkListing() {
        if (UserSession.getUserSession().isLoggedIn() == false) {
            openLoginPage();
            return;
        }
        if (mListing != null) {
            setBookMarkButton(true);
            getToolbar().getMenu().findItem(R.id.bookmark_listing_button).setVisible(false);
            IOBus.getInstance().post(new ToggleBookMarkEvent(mListing, true));
        }
    }

    @OnClick(R.id.unbookmark_listing_button)
    public void unBookMarkLising() {
        if (mListing != null) {
            setBookMarkButton(false);
            IOBus.getInstance().post(new ToggleBookMarkEvent(mListing, false));
        }
    }

    @Override
    public List<DealInfo> getDeals() {
        if (mListing == null) {
            return null;
        }
        return mListing.getDeals();
    }

    @OnClick(R.id.check_in_business_button)
    public void checkInBusiness() {
        if (!UserSession.getUserSession().isLoggedIn()) {
            openLoginPage();
            return;
        }
        if (mListing != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CheckinFragment checkinFragment = CheckinFragment.newInstance(mListing.getId(), mListing.getTitle(), mListing.getViewUrl());
            fragmentManager.beginTransaction().add(checkinFragment, "checkin_box").commit();
            //setCheckedIn(true);
        }
    }

    private void setCheckedIn(boolean checkedIn) {
        if (checkedIn) {
            checkInUserButton.setVisibility(View.GONE);
            checkedInAlreadyButton.setVisibility(View.VISIBLE);
        } else {
            checkInUserButton.setVisibility(View.VISIBLE);
            checkedInAlreadyButton.setVisibility(View.GONE);
        }
    }

    public void onUserCheckIn(boolean didCheckedIn) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(".");
        fragmentManager.beginTransaction().remove(fragment);
        setCheckedIn(didCheckedIn);
    }
}
