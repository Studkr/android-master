package com.INT.apps.GpsspecialDevelopment.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.INT.apps.GpsspecialDevelopment.data.models.CategoryBrowser;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.SetDynamicCategory;
import com.INT.apps.GpsspecialDevelopment.fragments.categories.CategoryBrowserFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.HotListingsFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.SearchBoxFragment;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.DynamicCategoryRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.SendLocationUpdateEvents;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.CheckUserSessionEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnFocusChange;
import timber.log.Timber;

import static com.INT.apps.GpsspecialDevelopment.fragments.categories.CategoryBrowserFragment.PERMISSION_LOCATION;

public class HomeActivity extends BaseActivity implements CategoryBrowserFragment.OnCategoryFragmentInteractionListener, CategoryBrowserFragment.CategoryListDataSource, SearchBoxFragment.QuerySelectListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String EXTRA_NEED_DISPLAY_LOGIN = "extra_need_display_login";
    private static final String MAP_FRAGMENT_TAG = "map_with_categories";
    private static final String LIST_FRAGMENT_TAG = "hot_listings_list";

    @InjectView(R.id.layout_search_box)
    View SearchBoxView;
    boolean mSearchBoxVisible = false;
    private boolean hideSearchBoxOnResume = false;

    /**
     * @saqib Variable for periodic location update based on distance travel
     */
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 10000; // 10 sec
    private static int DISPLACEMENT = 1000; // 10 meters
    private int requestCoutnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Timber.i("OnCreate");
        attachFragments();
        setTitle(R.string.hot_listings);
        Toolbar toolbar = setToolbar();
        toolbar.setTitle(getTitle());
        ButterKnife.inject(this);
        IOBus.getInstance().register(this);
        if (savedInstanceState != null && savedInstanceState.getBoolean("mSearchBoxVisible", false) == true) {
            showSearchBox();
        }
        IOBus.getInstance().post(new CheckUserSessionEvent());
        /*SAQIB
        Enable Disable dummy location here only*/
        if (getResources().getBoolean(R.bool.can_set_dummy_location)) {
            toolbar.inflateMenu(R.menu.menu_dummy_location);
        }
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        if (getIntent().getBooleanExtra(EXTRA_NEED_DISPLAY_LOGIN, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, R.string.auto_logout, Toast.LENGTH_LONG).show();
        }

        DynamicCategoryRequest dynamicCategoryRequest = new DynamicCategoryRequest(getApplicationContext(), IOBus.getInstance());
        dynamicCategoryRequest.getAllCategory(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Toast.makeText(HomeActivity.this, "onStart()", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        //  Toast.makeText(HomeActivity.this, "onResume()", Toast.LENGTH_SHORT).show();
        if (hideSearchBoxOnResume) {
            SearchBoxView.setVisibility(View.GONE);
            mSearchBoxVisible = false;
            hideSearchBoxOnResume = false;
        }
        //if we unregister IOBus in on pause, we need to register it in onResume
        //IOBus.getInstance().register(this);
        /**
         * Location updates usage
         * */
        //checkPlayServices();
        // Resuming the periodic location updates
        /*if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }*/
        //SAQIB - 23-02-2017
        //Error while login
        /*LoginResult.LoginResult_ authToken = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                .getUserSession();
        System.out.println("## session : " + UserSession.getUserSession().isLoggedIn());*/
        super.onResume();
    }

    @Override
    protected void onPause() {
        //   Toast.makeText(HomeActivity.this, "onPause()", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  Toast.makeText(HomeActivity.this, "onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IOBus.getInstance().unregister(this);
        // Toast.makeText(HomeActivity.this, "onDestroy()", Toast.LENGTH_SHORT).show();
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void attachFragments() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment listingFragment = fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        if (listingFragment == null) {
            listingFragment = HotListingsFragment.newInstance();
            fm.beginTransaction().add(R.id.main_container, listingFragment, LIST_FRAGMENT_TAG).commit();
            fm.beginTransaction().hide(listingFragment).commit();
        }

        Fragment fragment = fm.findFragmentByTag(MAP_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = CategoryBrowserFragment.newInstance();
            fm.beginTransaction().add(R.id.main_container, fragment, MAP_FRAGMENT_TAG).commit();
            fm.beginTransaction().show(fragment).commit();
        }
    }

    @OnCheckedChanged(R.id.switch_view_state)
    protected void changeViewHotListingsState(boolean checked) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        Fragment mapFragment = fm.findFragmentByTag(MAP_FRAGMENT_TAG);
        FragmentTransaction fmTransaction = fm.beginTransaction();
        if (listFragment != null && mapFragment != null) {
            if (checked) {
                fmTransaction
                        .hide(mapFragment)
                        .show(listFragment);
            } else {
                fmTransaction
                        .hide(listFragment)
                        .show(mapFragment);
            }
            fmTransaction.commit();
        }
    }

    @Override
    public void onCategoryFragmentInteraction(Category c) {
        if (c.getId() == -1) {
            Intent intent = new Intent(this, CategoryHierarchy.class);
            startActivity(intent);
        } else {
            ListingPaginationQueryBuilder queryBuilder = ListingPaginationQueryBuilder.newInstance();
            queryBuilder.setSearchListingCategory(c.getId(), c.getTitle());
            Intent intent = new Intent(this, ListingIndexActivity.class);
            intent.putExtra(ListingIndexActivity.SEARCH_QUERY, queryBuilder.build());
            startActivity(intent);
        }
    }

    @Override
    public ArrayList<Category> getCategories() {
        CategoryBrowser categoryBrowser = CategoryBrowser.getInstance(this.getApplicationContext());
        ArrayList<Category> categories = categoryBrowser.getChildrenCategories(null);
        String s = getResources().getString(R.string.all_categories);
        String iconName = getResources().getString(R.string.icon_more);
        categories.add(new Category(s, -1, iconName));
        return categories;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationDetector.LOCATION_INTENT) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
            if (fragment != null && fragment instanceof HotListingsFragment) {
                ((HotListingsFragment) fragment).onLocationSettings();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnFocusChange(R.id.dummy_search_text)
    void onDummyTextFocus(View view, boolean focus) {
        if (focus) {
            showSearchBox();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) return;
        if (requestCode == PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    private boolean requestedLocationPermissions() {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void showSearchBox() {
        mSearchBoxVisible = true;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("search_box");
        SearchBoxView.setVisibility(View.VISIBLE);
        if (fragment == null) {
            fragment = SearchBoxFragment.newInstance(null, false);
            fm.beginTransaction().replace(R.id.layout_search_box, fragment, "search_box")
                    .addToBackStack("search_box_fragment")
                    .commit();
        } else {
            ((SearchBoxFragment) fragment).onReAppear();
        }

    }

    @Override
    public void onQueryResult(ListingPaginationQueryBuilder query) {
        hideSearchBoxOnResume = true;
        String location = query.getLocationSearchKeyword();
        if ((location == null || location.length() == 0)) {
            query.enableSearchByCoordinates();
        } else {
            query.setSearchLocationKeyword(location);
        }
        Intent intent = new Intent(this, ListingIndexActivity.class);
        intent.putExtra(ListingIndexActivity.SEARCH_QUERY, query.build());
        startActivity(intent);
    }

    @Override
    public void onSearchCancel(ListingPaginationQueryBuilder query) {
        Fragment fm = getSupportFragmentManager().findFragmentByTag("search_box");
        if (fm != null) {
            getSupportFragmentManager().beginTransaction().remove(fm).commit();
            getSupportFragmentManager().popBackStack("search_box_fragment", 0);
            getSupportFragmentManager().executePendingTransactions();
        }
        SearchBoxView.setVisibility(View.GONE);
        mSearchBoxVisible = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mSearchBoxVisible", mSearchBoxVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.dummy_location) {
            setDummyLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDummyLocation() {
        //Old lat long [41.88092d, -87.62543d]->California

        alertWithCustomLayout(HomeActivity.this, "Enter Lat-Long").show();

        //Following are old code to set dummy location it is moved to above dilog's ok event
        //LocationDetector.getInstance(getApplicationContext()).setDummyLocation(27.981974, -82.829476);
        //attachCategoryFragment(true);
        //simpleAlertWithTitle(HomeActivity.this,"Lat-Long","27.981974,-82.829476").show();
    }
    /**
     * @saqib
     * Code for periodic location update based on distance travel
     * */
    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        if (requestedLocationPermissions()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Timber.i("Connection failed: ConnectionResult.getErrorCode() = %d", result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        requestCoutnt++;
        Timber.d("----Request count -------- %d", requestCoutnt);
        mLastLocation = location;
        //Here we were writing code for api request
        // Toast.makeText(HomeActivity.this, "Location changed", Toast.LENGTH_SHORT).show();
        Timber.d("----Location send from on location changed");
        Timber.d("----session : %b", UserSession.getUserSession().isLoggedIn());
        if (UserSession.getUserSession().isLoggedIn()) {
            IOBus.getInstance().post(new SendLocationUpdateEvents(location.getLatitude(), location.getLongitude()));
        }
        LocationDetector.getInstance(getApplicationContext()).notifyLocation(location.getLatitude(), location.getLongitude());
    }

    private AlertDialog simpleAlertWithTitle(Activity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    private AlertDialog alertWithCustomLayout(Activity activity, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dilog_location, null);
        final EditText lat = (EditText) view.findViewById(R.id.et_lat);
        final EditText lang = (EditText) view.findViewById(R.id.et_lang);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Check It Now!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        LocationDetector.getInstance(getApplicationContext()).setDummyLocation(Double.parseDouble(lat.getText().toString()), Double.parseDouble(lang.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    @Subscribe
    public void setDynamicCategory(final SetDynamicCategory category) {
        CategoryBrowserFragment fragment = (CategoryBrowserFragment) getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
        if (fragment != null) fragment.fillCategories(true);
    }

}
