package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Michael Soyma (Created on 9/21/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class DealRowVH {

    private View contentView;
    private Object tag;

    @InjectView(R.id.listing_image)
    ImageView dealImageView;
    @InjectView(R.id.ratings)
    ImageView ratingsImageView;
    @InjectView(R.id.listing_title)
    TextView titleView;
    @InjectView(R.id.review_count)
    TextView reviewCountView;
    @InjectView(R.id.distance)
    TextView distanceView;
    @InjectView(R.id.deal_info)
    TextView infoDealView;
    @InjectView(R.id.regular_price)
    TextView regularPriceView;
    @InjectView(R.id.final_price)
    TextView finalPriceView;

    public DealRowVH(final View contentView) {
        ButterKnife.inject(this, this.contentView = contentView);
    }

    public void fillDealView(final Listing_ listing, final DealInfo dealsInfo) {
        titleView.setText(listing.getTitle());
        reviewCountView.setText(contentView.getResources().getQuantityString(R.plurals.review_count, listing.getReviewCount(), listing.getReviewCount()));
        distanceView.setText(listing.getDistance());
        ratingsImageView.setImageDrawable(getRatingDrawable(contentView.getContext(), listing.getUserAvg()));

        String imageUrl = Listing_.getImageUrlForSize(dealsInfo.getImage(), 80, 80, true);
        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder100).fit().centerCrop().into(dealImageView);

        infoDealView.setText(dealsInfo.getTitle());
        regularPriceView.setPaintFlags(regularPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        regularPriceView.setText(String.format("%s%s", dealsInfo.getCurrencySymbol(), dealsInfo.getRegularPrice()));
        finalPriceView.setText(String.format("%s%s", dealsInfo.getCurrencySymbol(), dealsInfo.getFinalPrice()));
    }

    private static Drawable getRatingDrawable(final Context context, final String userAvgString) {
        final String ratingType = ListingRowViewHolderDealsMarker.RATING_SMALL;
        int usrAvgInt = 0;
        if (userAvgString != null) {
            Double usrAvgF = Double.parseDouble(userAvgString);
            usrAvgInt = (int) (usrAvgF * 10);
            usrAvgInt = (usrAvgInt) - (usrAvgInt % 5);
        }
        Resources resources = context.getResources();
        String ratingImage = ratingType + "0";
        if (usrAvgInt > 0) {
            ratingImage = ratingType + usrAvgInt;
        }
        int resourceId = resources.getIdentifier(ratingImage, "drawable", context.getPackageName());
        return resources.getDrawable(resourceId);
    }

    public void enable() {
        contentView.setVisibility(View.VISIBLE);
    }

    public void disable() {
        contentView.setVisibility(View.GONE);
    }

    public void saveTag(Object tag) {
        this.tag = tag;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        contentView.setOnClickListener(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTag() {
        return tag == null ? null : (T) tag;
    }
}
