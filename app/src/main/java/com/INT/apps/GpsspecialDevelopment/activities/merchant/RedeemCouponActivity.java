package com.INT.apps.GpsspecialDevelopment.activities.merchant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.BaseActivity;
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.DealRowVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponResult;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RedeemCouponActivity extends BaseActivity {

    public static final String ARG_COUPON = "ARG_COUPON";

    @InjectView(R.id.label_redeem)
    TextView redeemView;
    @InjectView(R.id.label_already_redeemed)
    TextView redeemAlreadyView;
    @InjectView(R.id.user_avatar)
    ImageView userAvatarView;
    @InjectView(R.id.user_name)
    TextView userNameView;
    @InjectView(R.id.purchased_date)
    TextView purchasedDateView;
    @InjectView(R.id.deal)
    View dealView;

    private DealRowVH dealRowVH;
    private MerchantPurchaseCoupons.Coupon currentCoupon;

    private ProgressDialog progressDialog;

    public static Bundle getIntentExtras(final MerchantPurchaseCoupons.Coupon coupon) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_COUPON, coupon);
        return bundle;
    }

    private void readArgs() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentCoupon = (MerchantPurchaseCoupons.Coupon) extras.getSerializable(ARG_COUPON);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArgs();
        setContentView(R.layout.activity_coupon_redeem);
        ButterKnife.inject(this);

        dealRowVH = new DealRowVH(dealView);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IOBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IOBus.getInstance().unregister(this);
    }

    private void initUI() {
        String imageUrl = CvUrls.getImageUrlForSize(currentCoupon.getUser().getAvatar(), 140, 140, true);
        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder100).fit().centerCrop().into(userAvatarView);
        userNameView.setText(currentCoupon.getUserName());
        purchasedDateView.setText(getString(R.string.purchased_at, currentCoupon.getDatePurchasedAgo()));
        dealRowVH.fillDealView(currentCoupon.getListing(), currentCoupon.getDealOrder());

        initRedeemState();
    }

    private void initRedeemState() {
        if (currentCoupon.getDealOrderCode().isUsed()) {
            redeemView.setVisibility(View.GONE);
            redeemAlreadyView.setVisibility(View.VISIBLE);
        } else {
            redeemAlreadyView.setVisibility(View.GONE);
            redeemView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.back_view)
    protected void clickBack() {
        onBackPressed();
    }

    @OnClick(R.id.deal_container)
    protected void clickDeal() {
        final DealInfo deal = currentCoupon.getDeal();
        Intent intent = new Intent(this, DealInformationActivity.class);
        intent.putExtra(DealInformationActivity.ARG_DEAL_ID, deal.getId());
        intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, deal.getTitle());
        intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, deal.getImage());
        startActivity(intent);
    }

    @OnClick(R.id.action_redeem)
    protected void redeem() {
        if (!currentCoupon.getDealOrderCode().isUsed()) {
            showConfirmationDialog();
        } else {
            Toast.makeText(this, R.string.redeem_redeemed, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.redeem_confirmation);
        builder.setMessage(R.string.redeem_confirmation_msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProgress();
                        IOBus.getInstance().post(new RedeemCouponEvent(currentCoupon.getDealOrderCode().getId()));
                    }
                }).setNegativeButton("No", null);
        builder.create().show();
    }

    @Subscribe
    public void redeemSuccess(RedeemCouponResult couponResult) {
        dismissProgress();
        Toast.makeText(this, R.string.redeem_success, Toast.LENGTH_SHORT).show();
        IOBus.getInstance().post(new SyncCouponsEvent(couponResult.getRedeemedCode(), currentCoupon.getDeal().getId()));
        finish();
    }

    @Subscribe
    public void redeemError(RedeemCouponError error) {
        dismissProgress();
        if (error.isAlreadyRedeemed())
            Toast.makeText(this, R.string.redeem_redeemed, Toast.LENGTH_LONG).show();
        else Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showProgress() {
        dismissProgress();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.redeem_progress));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
