package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.FilterBoxFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.SearchBoxFragment;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import timber.log.Timber;

public class ListingIndexActivity extends AbstractListingIndexActivity implements SearchBoxFragment.QuerySelectListener, FilterBoxFragment.SearchFilteringResult {

    private TextView mDummySearchBox;
    boolean hideSearchBoxOnResume = false;
    Double mLatitude;
    Double mLongitude;
    private Timer timer;
    @InjectView(R.id.layout_search_box)
    View SearchBoxView;
    public boolean isSearchByCurrLocation = true;

    void setToolbarMenuItems(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final View view = getLayoutInflater().inflate(R.layout.dummy_searchbox, null);
        mDummySearchBox = view.findViewById(R.id.dummy_search_text);
        final View filterIcon = view.findViewById(R.id.trigger_filter);
        toolbar.addView(view, new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
        final Drawable drawable = filterIcon.getBackground();
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterBox();
                filterIcon.setBackground(getResources().getDrawable(R.drawable.tune_filter_bg_selected));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filterIcon.setBackground(drawable);
                    }
                }, 200);
            }
        });
        setDummySearchBoxText(getQueryBuilder());
        mDummySearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchbox(false);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IOBus.getInstance().register(this);
        final LocationDetector locationDetector = LocationDetector.getInstance(getApplicationContext());
        if (locationDetector.isLocationEnabled()) {
            LocationDetector.getInstance(getApplicationContext()).getLocation(true);
        } else {
            onQueryResult(getQueryBuilder());
        }
        final Handler handler1 = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                handler1.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {

                        try {
                            //System.out.println("----**Query----" + x);
                            // System.out.println("----**QNewLat----" + newLatitude);
                            //  System.out.println("----**QNewLang----" + newLongitude);
                            //System.out.println("----**Query2:-" + mQueryBuilder.toString());
                            //mQueryBuilder.setSearchCoordinates(newLatitude, newLongitude);
                            //System.out.println("----**Query3:-" + mQueryBuilder.toString());
                            //((ListingIndexActivity)getActivity()).attachRefreshFragment(mQueryBuilder);
                            // attachRefreshFragment();
                            if (locationDetector.isLocationEnabled()) {
                                LocationDetector.getInstance(getApplicationContext()).getLocation(true);
                            } else {
                                onQueryResult(getQueryBuilder());
                            }

                        } catch (Exception e) {
                            Timber.d(e,"----**Error occured ListingPaginationFragment");
                        }


                    }
                });
            }
        };
        //SAQIB 20-03-2017
        //Refresh data every 3 min
        // timer.schedule(doAsynchronousTask, 0, 180000);
    }

    private void openSearchbox(boolean focusLocation) {
        findViewById(R.id.loader_amin12).setVisibility(View.GONE);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = SearchBoxFragment.newInstance(getQueryBuilder().build(), focusLocation);
        SearchBoxView.setVisibility(View.VISIBLE);
        fm.beginTransaction().replace(R.id.layout_search_box, fragment, "search_box")
                .addToBackStack("search_box_fragment")
                .commit();
    }

    @Override
    public void onQueryResult(ListingPaginationQueryBuilder query) {
        hideSearchBoxOnResume = true;
        setQueryBuilder(query);
        String location = query.getLocationSearchKeyword();
        if (TextUtils.isEmpty(location) && mLatitude != null && mLongitude != null) {
            setLattitude(mLatitude);
            setLongitude(mLongitude);
            isSearchByCurrLocation = true;
            Timber.d("----**" + "lat: %f long: %f", mLatitude, mLongitude);
        } else if (TextUtils.isEmpty(location)) {
            showSetLocationPrompt();
        } else isSearchByCurrLocation = false;
        removeSearchboxFragment();
        attachListingIndexFragment(query, true);
        setDummySearchBoxText(query);
    }

    @Override
    protected void onStop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onStop();
    }

    private void removeSearchboxFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("search_box");
        if (fragment != null) {
            SearchBoxView.setVisibility(View.GONE);
            fm.popBackStack("search_box_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().remove(fragment).commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    public void onSearchCancel(ListingPaginationQueryBuilder query) {
        removeSearchboxFragment();
    }

    @Override
    public void onBackPressed() {
        Fragment fm = getSupportFragmentManager().findFragmentByTag("search_box");
        if (fm != null) {
            removeSearchboxFragment();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().unregister(this);
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    //TODO need change logic load listings
    @Subscribe
    public void onUserLocation(LocationDetector.OnLocationEvent event) {
        mLatitude = event.getLatitude();
        mLongitude = event.getLongitude();
        onQueryResult(getQueryBuilder());
    }

    public LatLng getCurrentLocation() {
        return new LatLng(mLatitude, mLongitude);
    }

    @Subscribe
    public void onUserLocationLoadError(LocationDetector.OnLocationFetchErrorEvent errorEvent) {
        showSetLocationPrompt();
    }

    private void showSetLocationPrompt() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.location_not_accessible);
        dialog.setMessage(R.string.location_disabled_message);
        dialog.setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, LocationDetector.LOCATION_INTENT);
            }
        });
        dialog.setNegativeButton(R.string.set_location, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openSearchbox(true);
            }
        });
        dialog.show();
    }

    private void showFilterBox() {
        DialogFragment dialogFragment = FilterBoxFragment.newInstance(getQueryBuilder());
        dialogFragment.show(getSupportFragmentManager().beginTransaction(), "filter");
    }

    public void onSearchFilteringResult(ListingPaginationQueryBuilder builder) {
        if (builder.toString().equals(getQueryBuilder().toString()) == false) {
            setQueryBuilder(builder);
            attachListingIndexFragment(builder, true);
        }
    }

    @Override
    public void onSearchFilteringCancel() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationDetector.LOCATION_INTENT) {
            LocationDetector.getInstance(getApplicationContext()).getLocation(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDummySearchBoxText(ListingPaginationQueryBuilder builder) {
        mDummySearchBox.setFocusable(false);
        String searchText = builder.getSearchKeyword();
        if (getQueryBuilder().getSearchListingCategory().length() > 0) {
            searchText = builder.getSearchListingCategory();
        }
        if (searchText.length() == 0) {
            searchText = getResources().getString(R.string.everything);
        }
        mDummySearchBox.setText(searchText);
    }


}
