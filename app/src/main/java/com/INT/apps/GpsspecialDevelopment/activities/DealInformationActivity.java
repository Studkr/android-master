package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.CvSettings;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.Deal;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealInformation;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.DealInformationLoaded;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadDealInformationEvent;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class DealInformationActivity extends BaseActivity {

    public static String ARG_DEAL_ID = "deal_id";
    public static String ARG_DEAL_TITLE = "deal_title";
    public static String ARG_DEAL_IMAGE = "deal_image";

    public String mDealId;
    public String mDealTitle;
    public String mDealImage;
    Deal mDealInformation;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    @InjectView(R.id.progress_bar)
    View progressView;

    @InjectView(R.id.deal_title)
    TextView dealTitleView;

    @InjectView(R.id.deal_image)
    ImageView dealImageView;
    @InjectView(R.id.discount)
    TextView discountView;
    @InjectView(R.id.bought)
    TextView boughtAmountView;
    @InjectView(R.id.original_price)
    TextView originalPriceView;
    @InjectView(R.id.discounted_price)
    TextView discountetPriceView;
    @InjectView(R.id.deal_description)
    TextView dealDescriptionView;
    @InjectView(R.id.days_left)
    TextView dealsDaysLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_information);
        Timber.i("deal information");
        ButterKnife.inject(this);
        progressView.setVisibility(View.VISIBLE);
        setToolbar(true);
        mDealId = getIntent().getStringExtra(ARG_DEAL_ID);
        mDealTitle = getIntent().getStringExtra(ARG_DEAL_TITLE);
        mDealImage = getIntent().getStringExtra(ARG_DEAL_IMAGE);
        getToolbar().setTitle(mDealTitle);
        getToolbar().inflateMenu(R.menu.menu_deal_information);

        numberFormat.setMinimumFractionDigits(2);

        dealTitleView.setText(mDealTitle);
        setDealImage(mDealImage);
        IOBus.getInstance().post(new LoadDealInformationEvent(mDealId));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.open_in_browser) {
            String viewUrl = mDealInformation.getDeal().getViewUrl();
            if (viewUrl != null) {
                viewUrl = CvSettings.getServerUrl() + viewUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUrl));
                startActivity(intent);
            }

        }
        if (item.getItemId() == R.id.share || item.getItemId() == R.id.action_share_2) {
            startActivity(getShareIntent());
        }
        return super.onOptionsItemSelected(item);
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

    private void setDealImage(String dealImage) {
        dealImage = CvUrls.getImageUrlForSize(dealImage, 480, 320, true);
        Picasso.get().load(dealImage).placeholder(R.drawable.placeholder600).into(dealImageView);
    }

    @Subscribe
    public void onDealInformationLoaded(DealInformationLoaded event) {
        DealInformation dealInformation = event.getDealInformation();
        mDealInformation = dealInformation.getDeal();
        displayDeal(dealInformation.getDeal().getDeal());
        progressView.setVisibility(View.GONE);
        findViewById(R.id.deal_details_wrapper).setVisibility(View.VISIBLE);
        findViewById(R.id.deal_prize_and_count).setVisibility(View.VISIBLE);
    }

    private void displayDeal(Deal.Deal_ deal) {
        setDealImage(deal.getImage());
        dealTitleView.setText(deal.getTitle());
        discountView.setText(deal.getDiscount() + "%");
        boughtAmountView.setText(deal.getQuantityConsumed() + "");
        int totalQuantity = Integer.parseInt(deal.getTotalQuantity());
        if (totalQuantity == 0) {
            findViewById(R.id.availability).setVisibility(View.GONE);
        }
        originalPriceView.setText(deal.getCurrencySymbol() + deal.getRegularPrice());
        originalPriceView.setPaintFlags(originalPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String finalPrice = String.format(Locale.US, "%.2f", Double.valueOf(deal.getFinalPrice()));
        String discountPrice = deal.getCurrencySymbol() + finalPrice;
        discountetPriceView.setText(discountPrice);
        setHtmlText(dealDescriptionView, deal.getDescription());
        setHtmlText((TextView) findViewById(R.id.fine_print), deal.getFinePrint());
        setHtmlText((TextView) findViewById(R.id.validity), deal.getValidity());
        setHtmlText((TextView) findViewById(R.id.terms), deal.getTerms());
        if (deal.getTimePendingSeconds() != null) {
            Integer seconds = Integer.parseInt(deal.getTimePendingSeconds());
            Integer days = (int) (seconds / (3600 * 24));
            if (days == 0) {
                days = 1;
            }
            dealsDaysLeft.setText(getResources().getString(R.string.deal_deals_left, days.toString()));

        }
    }

    private void setHtmlText(TextView textView, String content) {
        if (content == null || content.length() == 0) {
            ((View) textView.getParent()).setVisibility(View.GONE);
            return;
        }
        textView.setText(Html.fromHtml(content));
    }

    @OnClick(R.id.buy_deal)
    public void openBuyDeal() {
        Intent intent = new Intent(this, BuyDealActivity.class);
        intent.putExtra(BuyDealActivity.KEY_ARG_DEAL_ID, mDealId);
        startActivity(intent);
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Deal.Deal_ deal = mDealInformation.getDeal();
        intent.putExtra(Intent.EXTRA_SUBJECT, deal.getTitle());
        Spanned htmlContent = Html.fromHtml(deal.getDescription());
        intent.putExtra(Intent.EXTRA_HTML_TEXT, htmlContent.toString());
        intent.putExtra(Intent.EXTRA_TEXT, deal.getDescription());
        return intent;
    }
}
