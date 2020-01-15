package com.INT.apps.GpsspecialDevelopment.activities.merchant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.squareup.otto.Subscribe;

import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantScanQRActivity extends Activity implements ZXingScannerView.ResultHandler {

    @InjectView(R.id.surface_camera)
    protected ZXingScannerView surfaceCameraView;
    @InjectView(R.id.coupon_code)
    protected EditText couponCodeInputView;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_scan_qr_coupon);
        ButterKnife.inject(this);

        couponCodeInputView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int actionId, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchCouponCode(couponCodeInputView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        initQRReader();
        IOBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IOBus.getInstance().unregister(this);
    }

    @OnClick(R.id.back_view)
    protected void clickBack() {
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceCameraView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceCameraView.setResultHandler(this);
        surfaceCameraView.startCamera();
    }

    private void initQRReader() {
        surfaceCameraView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        surfaceCameraView.setAutoFocus(true);
    }

    @Override
    public void handleResult(Result rawResult) {
        couponCodeInputView.setText(rawResult.getText());
        searchCouponCode(rawResult.getText());
    }

    private void searchCouponCode(final String couponCode) {
        if (!TextUtils.isEmpty(couponCode)) {
            showProgress();
            IOBus.getInstance().post(new FindCouponEvent(couponCode));
        }
    }

    @Subscribe
    public void searchCouponSuccess(FindCouponResult findCouponResult) {
        dismissProgress();
        final Intent sendIntent = new Intent(this, RedeemCouponActivity.class);
        sendIntent.putExtras(RedeemCouponActivity.getIntentExtras(findCouponResult.getCouponResult()));
        finish();
        startActivity(sendIntent);
    }

    @Subscribe
    public void searchCouponError(FindCouponError findCouponError) {
        dismissProgress();
        surfaceCameraView.resumeCameraPreview(this);
        if (findCouponError.isNotFound())
            Toast.makeText(this, R.string.coupon_code_not_found, Toast.LENGTH_LONG).show();
        else Toast.makeText(this, findCouponError.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showProgress() {
        dismissProgress();
        progressDialog = new ProgressDialog(this, R.style.AppQRScanTheme_ProgressBarStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.search_coupon_progress));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
