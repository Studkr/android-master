package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.view.View;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Michael Soyma (Created on 9/19/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class ListingDealsRowVH {

    @InjectView(R.id.deal_1)
    View dealView1;
    @InjectView(R.id.deal_2)
    View dealView2;
    @InjectView(R.id.deal_3)
    View dealView3;

    private DealRowVH[] deals; //Max is 3

    @InjectView(R.id.show_more_deals)
    TextView showMoreDealsView;

    public ListingDealsRowVH(final View contentView) {
        ButterKnife.inject(this, contentView);
        deals = new DealRowVH[]{new DealRowVH(dealView1), new DealRowVH(dealView2), new DealRowVH(dealView3)};
    }

    public void setListing(final Listing_ listing, final DealsGroupClickListener groupClickListener) {
        final List<DealInfo> dealList = listing.getDeals();
        goneAllDeals();
        for (int i = 0; i < Math.min(listing.getDealsPaging().getCount(), listing.getDealsPaging().getLimit()); i++) {
            final DealInfo dealInfo = dealList.get(i);
            deals[i].fillDealView(listing, dealInfo);
            deals[i].saveTag(dealInfo);
            deals[i].enable();
            deals[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupClickListener.chooseDeal(dealInfo);
                }
            });
        }

        if (listing.getDealsPaging().getCount() > listing.getDealsPaging().getLimit()) {
            showMoreDealsView.setTag(listing);
            showMoreDealsView.setText(showMoreDealsView.getResources().getString(R.string.deals_more_by, String.valueOf(listing.getDealsPaging().getCount()), listing.getTitle()));
            showMoreDealsView.setVisibility(View.VISIBLE);
            showMoreDealsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() != null && view.getTag() instanceof Listing_) {
                        groupClickListener.showMore((Listing_) view.getTag());
                    }
                }
            });
        }
    }

    private void goneAllDeals() {
        showMoreDealsView.setTag(null);
        showMoreDealsView.setVisibility(View.GONE);
        for (DealRowVH dealRowVH : deals) {
            dealRowVH.saveTag(null);
            dealRowVH.disable();
        }
    }

    public interface DealsGroupClickListener {
        void chooseDeal(DealInfo dealsInfo);

        void showMore(Listing_ listing);
    }
}
