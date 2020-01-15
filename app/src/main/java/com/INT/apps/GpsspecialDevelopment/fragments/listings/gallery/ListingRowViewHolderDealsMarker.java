package com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

/**
 * Created by shrey on 7/5/15.
 */
public class ListingRowViewHolderDealsMarker {
    public static String RATING_MEDIUM = "stars_med_";
    public static String RATING_SMALL = "stars_small_";

    @InjectView(R.id.listing_title)
    TextView title;

    @Optional
    @InjectView(R.id.review_count)
    TextView reviewCount;

    @Optional
    @InjectView(R.id.category_list)
    TextView categoryList;

    @Optional
    @InjectView(R.id.ratings)
    ImageView ratings;

    @Optional
    @InjectView(R.id.listing_image)
    ImageView listingImage;

    @Optional
    @InjectView(R.id.distance)
    TextView distance;

    @Optional
    @InjectView(R.id.listing_address)
    TextView address;

    @Optional
    @InjectView(R.id.listing_deals)
    TextView deals;

    @Optional
    @InjectView(R.id.listings_deals_discount)
    TextView deals_discount;

    @Optional
    @InjectView(R.id.listings_deals_purchased)
    TextView deals_purchased;

    @Optional
    @InjectView(R.id.listings_deals_availability)
    TextView deals_availability;

    @Optional
    @InjectView(R.id.listings_deals_regular_price)
    TextView deals_regular_price;

    @Optional
    @InjectView(R.id.listings_deals_final_price)
    TextView deals_final_price;

    @Optional
    @InjectView(R.id.listings_deals_title)
    TextView deals_title;


    @Optional
    @InjectView(R.id.listings_deals_more_deals)
    TextView deals_more_deals;

    @Optional
    @InjectView(R.id.listings_deals_layout_mail)
    LinearLayout layout_delas_main;

    @Optional
    @InjectView(R.id.deal_icon_wrapper)
    View dealIconWrapper;

    //New deals layout views

    @Optional
    @InjectView(R.id.listing_layout_new)
    LinearLayout layout_delas_new;

    @Optional
    @InjectView(R.id.listing_title_new)
    TextView deals_title_new;


    private boolean injected = false;
    private int mImageHeight = 100;
    private int mImageWidth = 100;
    private String mRatingType = RATING_SMALL;

    public ListingRowViewHolderDealsMarker() {

    }

    public ListingRowViewHolderDealsMarker(int imageHeight, int imageWidth) {

        mImageHeight = imageHeight;
        mImageWidth = imageWidth;
    }

    public ListingRowViewHolderDealsMarker(int imageHeight, int imageWidth, String ratingType) {
        mImageHeight = imageHeight;
        mImageWidth = imageWidth;
        mRatingType = ratingType;
    }

    public void setViewContent(Activity activity, Listing_ listing, View rowView, String type) {
        if (injected == false) {
            ButterKnife.inject(this, rowView);
            injected = true;
        }
        if (title != null) {
            title.setText(listing.getTitle());
        }
        if (reviewCount != null) {
            String reviewsCount = activity.getResources().getQuantityString(R.plurals.review_count, listing.getReviewCount(), listing.getReviewCount());
            reviewCount.setText(reviewsCount);
        }
        if (listingImage != null) {
            String imageUrl = listing.getImageUrlForSize(mImageHeight, mImageWidth, true);
            Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder100).fit().centerCrop().into(listingImage);
        }
        String categories = TextUtils.join(", ", listing.getCategoryTitles().values());
        if (categoryList != null && categories != null) {
            categoryList.setText(categories);
        }
        if (ratings != null) {
            String usrAvgString = listing.getUserAvg();
            double usrAvg = 0d;
            int usrAvgInt = 0;
            if (usrAvgString != null) {
                Double usrAvgF = Double.parseDouble(usrAvgString);
                usrAvgInt = (int) (usrAvgF * 10);
                usrAvgInt = (usrAvgInt) - (usrAvgInt % 5);

            }
            Resources resources = activity.getResources();
            String ratingImage = mRatingType + "0";
            if (usrAvgInt > 0) {
                ratingImage = mRatingType + usrAvgInt;
            }
            int resourceId = resources.getIdentifier(ratingImage, "drawable", activity.getPackageName());
            Drawable dr = resources.getDrawable(resourceId);
            ratings.setImageDrawable(dr);
        }
        String distanceText = listing.getDistance();
        if (distance != null && distanceText != null) {
            distance.setText(distanceText);
        }

        String addressData = listing.getAddressData();
        if (address != null && addressData != null) {
            address.setText(addressData);
        }
        //"MAPSAQIB"
        if (deals != null) {
            deals.setVisibility(View.GONE);
        }

        int dealsData = listing.getDeals().size();
        StringBuilder dealbuilder = new StringBuilder();
        Timber.d("##wooh total deal ->%d", listing.getDeals().size());
        if (type.equals("TYPE_MAP")) {

            deals_title_new.setText(listing.getTitle());

            for (int i = 0; i < listing.getDeals().size(); i++) {
                Timber.d("##wooh deal title ->%s", listing.getDeals().get(i).getTitle());
                Timber.d("##wooh deal discount ->%s", listing.getDeals().get(i).getDiscount());
                Timber.d("##wooh deal sumbol ->%s", listing.getDeals().get(i).getCurrencySymbol());
                dealbuilder.append(listing.getDeals().get(i).getTitle());
                if (i > listing.getDeals().size()) {
                    dealbuilder.append(",");
                }

                TextView deal = new TextView(activity);
                deal.setText(listing.getDeals().get(i).getTitle());
                deal.setTextSize(14);
                deal.setPadding(5, 3, 5, 3);
                //deal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deals_icon, 0, 0, 0);
                //deal.setCompoundDrawablePadding(10);
                deal.setTextColor(Color.parseColor("#7DCA51"));
                layout_delas_new.addView(deal);

            }
        }


        if (deals != null && dealsData > 0) {
            deals.setText("Deal - " + dealbuilder.toString());
            deals.setVisibility(View.GONE);
        }

        //SAQIB
        /*Layout of following code is Hide. As per new layout request.*/
        if (type.equals("TYPE_MAP")) {
            if (dealsData > 0) {
                deals_title.setText(listing.getDeals().get(0).getTitle());
                deals_discount.setText(listing.getDeals().get(0).getDiscount());
                deals_purchased.setText(listing.getDeals().get(0).getQuantityConsumed());
                deals_regular_price.setText(listing.getDeals().get(0).getCurrencySymbol() + listing.getDeals().get(0).getRegularPrice());
                deals_final_price.setText(listing.getDeals().get(0).getCurrencySymbol() + listing.getDeals().get(0).getFinalPrice());
                if (listing.getDeals().get(0).getTotalQuantity().equals("0")) {
                    deals_availability.setVisibility(View.GONE);
                } else {
                    deals_availability.setVisibility(View.VISIBLE);
                }
                deals_regular_price.setPaintFlags(deals_regular_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                deals_more_deals.setVisibility(View.GONE);
                if (dealsData - 1 > 0) {
                    //It will show the hidden deals view so programatically hide it
                    //deals_more_deals.setVisibility(View.VISIBLE);
                    //deals_more_deals.setText(dealsData - 1 + " more deals available");
                }

            } else {
                layout_delas_main.setVisibility(View.GONE);
                deals_title.setVisibility(View.GONE);
                deals_more_deals.setVisibility(View.GONE);
            }
        }


        if (dealIconWrapper != null) {
            if (listing.hasDeals()) {
                dealIconWrapper.setVisibility(View.VISIBLE);
            } else {
                dealIconWrapper.setVisibility(View.GONE);
            }
        }
    }
}
