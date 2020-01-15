package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.FieldInformation;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingViewLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.LoadListingViewEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class ListingExtraInformationActivity extends BaseActivity {
    public static String ARG_LISTING_ID = "listingId";

    String mListingId;
    Listing_ mlisting;

    @InjectView(R.id.field_list_view)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_extra_information);
        Timber.i("listing extra information");
        ButterKnife.inject(this);
        mListingId = getIntent().getStringExtra(ARG_LISTING_ID);
        setToolbar(true);
        IOBus.getInstance().post(new LoadListingViewEvent(mListingId));

    }

    @Subscribe
    public void listingViewLoaded(ListingViewLoadedEvent event) {
        Listing_ listing = event.getListing();
        mlisting = listing;
        getToolbar().setTitle(listing.getTitle());
        FieldInformation[] fieldInformationList = mlisting.getFieldInformation();
        List<FieldInformation> fieldInformations = new ArrayList<>();
        if (!TextUtils.isEmpty(mlisting.getDescription())) {
            final FieldInformation description = new FieldInformation();
            description.setLabel(getString(R.string.description));
            description.setDisplayValue(mlisting.getDescription());
            fieldInformations.add(description);
        }
        for (int i = 0; i < fieldInformationList.length; i++) {
            FieldInformation fieldInformation = fieldInformationList[i];
            String value = fieldInformation.getDisplayValue();
            if (value == null || value.length() == 0)
                break;
            fieldInformations.add(fieldInformationList[i]);
        }

        mListView.setAdapter(new FieldInformationAdapter(this, fieldInformations));
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    class FieldInformationAdapter extends ArrayAdapter<FieldInformation> {
        FieldInformationAdapter(Context context, List<FieldInformation> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listing_extra_information_row, null);
            }
            TextView label = (TextView) convertView.findViewById(R.id.field_label);
            TextView value = (TextView) convertView.findViewById(R.id.field_value);
            FieldInformation fieldInformation = getItem(position);

            if (fieldInformation != null) {
                label.setText(fieldInformation.getLabel());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(fieldInformation.getType(), FieldInformation.TYPE_URL)) {
                        String url = fieldInformation.getValue();
                        value.setText(url);
                        Linkify.addLinks(value, Linkify.WEB_URLS);
                    } else {
                        value.setText(Html.fromHtml(fieldInformation.getDisplayValue()));
                    }
                }
            }
            value.setMovementMethod(LinkMovementMethod.getInstance());
            value.setLinksClickable(true);
            return convertView;
        }
    }
}
