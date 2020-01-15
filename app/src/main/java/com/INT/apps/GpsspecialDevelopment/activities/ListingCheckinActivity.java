package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.CheckinInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.UserCheckins;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.ListingRowViewHolder;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins.FetchUserCheckinsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins.OnUserCheckinsEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.INT.apps.GpsspecialDevelopment.views.EndlessScrollListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class ListingCheckinActivity extends BaseActivity {
    @InjectView(R.id.check_in_list)
    ListView mListView;
    ProgressBar mProgressBar;
    Paging mLastPaging;
    Integer mPage = 1;

    public static String ARG_USER_ID = "user_id";
    public static String ARG_USERNAME = "username";
    private CheckinRowAdapater mCheckinRowAdapater;

    public String mUserId;
    public String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_checkin);
        ButterKnife.inject(this);
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        mUserId = getIntent().getStringExtra(ARG_USER_ID);
        mUsername = getIntent().getStringExtra(ARG_USERNAME);
        mCheckinRowAdapater = new CheckinRowAdapater(this);
        mListView.addFooterView(mProgressBar);
        mListView.setAdapter(mCheckinRowAdapater);
        setToolbar(true);
        if (UserSession.getUserSession().isLoggedIn() && UserSession.getUserSession().getSessionUser().getId().equals(mUserId)) {
            getToolbar().setTitle(R.string.my_checkins);
        } else {
            getToolbar().setTitle(getString(R.string.user_checkins, mUsername));
        }
        loadPage(1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckinInfo checkinInfo = mCheckinRowAdapater.getItem(position);
                Intent intent = new Intent(ListingCheckinActivity.this, ListingViewActivity.class);
                intent.putExtra(ListingViewActivity.ARG_LISTING_ID, checkinInfo.getListing().getId());
                intent.putExtra(ListingViewActivity.ARG_LISTING_TITLE, checkinInfo.getListing().getTitle());
                startActivity(intent);
            }
        });
    }

    protected void loadPage(int page) {
        mPage = page;
        mProgressBar.setVisibility(View.VISIBLE);
        Timber.i("----** Checking request sent");
        IOBus.getInstance().post(new FetchUserCheckinsEvent(mUserId, page));

    }

    @Subscribe
    public void onCheckinsLoaded(OnUserCheckinsEvent event) {
        Toast.makeText(ListingCheckinActivity.this, "Checkin data", Toast.LENGTH_SHORT).show();
        Timber.d("----** Checking data received %d", event.getUserCheckins().getCheckins().size());
        mProgressBar.setVisibility(View.GONE);
        UserCheckins userCheckins = event.getUserCheckins();
        mLastPaging = userCheckins.getPaging();
        for (CheckinInfo checkinInfo : event.getUserCheckins().getCheckins()) {
            mCheckinRowAdapater.add(checkinInfo);
        }
        mCheckinRowAdapater.notifyDataSetChanged();
        if (mPage == 1) {
            mListView.setOnScrollListener(new EndlessScrollListener(2, 1) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Integer totalPageCount = new Integer(mLastPaging.getPageCount());
                    Integer currentPage = new Integer(mPage);
                    if (mLastPaging != null && totalPageCount > currentPage && totalPageCount > 1) {
                        loadPage(mPage + 1);
                    }
                }
            });
        }
    }

    @Subscribe
    public void onCheckinsError(ApiRequestFailedEvent event) {
        simpleAlert(ListingCheckinActivity.this, "Error").show();
        Toast.makeText(ListingCheckinActivity.this, "Error:" + event.getError(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }

    class CheckinRowAdapater extends ArrayAdapter<CheckinInfo> {

        CheckinRowAdapater(Context context) {
            super(context, 0, new ArrayList<CheckinInfo>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListingRowViewHolder mRowViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_checkin_row, null);
                mRowViewHolder = new ListingRowViewHolder(80, 80, ListingRowViewHolder.RATING_SMALL);
                convertView.setTag(mRowViewHolder);
            } else {
                mRowViewHolder = (ListingRowViewHolder) convertView.getTag();
            }
            CheckinInfo checkinInfo = getItem(position);
            TextView checkinCount = (TextView) convertView.findViewById(R.id.checkin_count);
            if (checkinCount != null) {
                Integer checkinCountNumber = Integer.parseInt(checkinInfo.getCheckin().getCheckinCount());
                if (checkinCountNumber == null) {
                    checkinCountNumber = 0;
                }

                checkinCount.setText(getResources().getQuantityString(R.plurals.checkin_count, checkinCountNumber, checkinCountNumber));
            }
            String created = checkinInfo.getCheckin().getCreated();
            if (created != null && created.length() > 0) {
                String fancyDate = DateParser.stringDateToPretty(created);
                if (fancyDate != null) {
                    ((TextView) convertView.findViewById(R.id.checkin_date)).setText(fancyDate);
                }
            }
            mRowViewHolder.setViewContent(ListingCheckinActivity.this, checkinInfo.getListing(), convertView);
            return convertView;
        }
    }

    private AlertDialog simpleAlert(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Error");
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListingCheckinActivity.this.finish();
                    }
                });
        return builder.create();
    }

}
