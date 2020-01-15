package com.INT.apps.GpsspecialDevelopment.fragments.deals;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Michael Soyma (Created on 9/28/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class CouponShareDialogFragment extends DialogFragment {

    public static final String ARG_DEAL_TITLE = "ARG_DEAL_TITLE";
    public static final String ARG_DEAL_IS_USED = "ARG_DEAL_IS_USED";
    public static final String ARG_COUPON_CODE = "ARG_COUPON_CODE";
    public static final String ARG_LISTING_TITLE = "ARG_LISTING_TITLE";
    public static final String ARG_FINAL_PRICE = "ARG_FINAL_PRICE";
    public static final String ARG_REGULAR_PRICE = "ARG_REGULAR_PRICE";

    private String couponCode;
    private boolean couponIsUsed;
    private String dealTitle;
    private String listingTitle;
    private String finalPrice;
    private String regularPrice;

    private View shareView;

    @InjectView(R.id.qr_code)
    ImageView qrCodeView;
    @InjectView(R.id.coupon_code)
    TextView couponCodeView;

    public static CouponShareDialogFragment instance(String coupon,
                                                     boolean used,
                                                     String listingTitle,
                                                     String dealTitle,
                                                     String finalPrice,
                                                     String regularPrice) {
        final CouponShareDialogFragment fragment = new CouponShareDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_DEAL_TITLE, dealTitle);
        args.putBoolean(ARG_DEAL_IS_USED, used);
        args.putString(ARG_COUPON_CODE, coupon);
        args.putString(ARG_LISTING_TITLE, listingTitle);
        args.putString(ARG_FINAL_PRICE, finalPrice);
        args.putString(ARG_REGULAR_PRICE, regularPrice);
        fragment.setArguments(args);
        return fragment;
    }

    private void readArgs() {
        if (getArguments() != null) {
            couponCode = getArguments().getString(ARG_COUPON_CODE);
            couponIsUsed = getArguments().getBoolean(ARG_DEAL_IS_USED);
            dealTitle = getArguments().getString(ARG_DEAL_TITLE);
            listingTitle = getArguments().getString(ARG_LISTING_TITLE);
            finalPrice = getArguments().getString(ARG_FINAL_PRICE);
            regularPrice = getArguments().getString(ARG_REGULAR_PRICE);

            if (!TextUtils.isEmpty(couponCode))
                couponCode = couponCode.toUpperCase();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        readArgs();
        shareView = inflater.inflate(R.layout.view_stub_share_coupon, null);
        return inflater.inflate(R.layout.fragment_dialog_share_coupon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        fillShareView();
        couponCodeView.setText(couponCode);
        if (couponIsUsed)
            couponCodeView.setPaintFlags(couponCodeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        try {
            final Bitmap qrBmp = createBarcodeBitmap(couponCode, qrCodeView.getMaxWidth(), qrCodeView.getMaxHeight());
            if (qrBmp != null) {
                qrCodeView.setImageBitmap(qrBmp);
                ((ImageView) shareView.findViewById(R.id.qr_code)).setImageBitmap(qrBmp);
            } else fillPlaceholderImage();
        } catch (WriterException e) {
            e.printStackTrace();
            fillPlaceholderImage();
        }
    }

    private void fillPlaceholderImage() {
        qrCodeView.setImageResource(R.drawable.placeholder100);
        ((ImageView) shareView.findViewById(R.id.qr_code)).setImageResource(R.drawable.placeholder100);
    }

    private void fillShareView() {
        if (couponIsUsed)
            ((TextView) shareView.findViewById(R.id.coupon_code)).setPaintFlags(couponCodeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        ((TextView) shareView.findViewById(R.id.coupon_code)).setText(couponCode);
        ((TextView) shareView.findViewById(R.id.deal_title)).setText(dealTitle);
        ((TextView) shareView.findViewById(R.id.listing_title)).setText(listingTitle);
        ((TextView) shareView.findViewById(R.id.final_price)).setText(finalPrice);

        final TextView regularPriceView = shareView.findViewById(R.id.regular_price);
        regularPriceView.setPaintFlags(regularPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        regularPriceView.setText(regularPrice);
    }

    @OnClick(R.id.share_coupon_code)
    protected void share() {
        String pathOfBmp = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), buildShareBitmap(), "Deal Coupon", null);
        Uri bmpUri = Uri.parse(pathOfBmp);
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        startActivity(shareIntent);
    }

    @Nullable
    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(data,
                    BarcodeFormat.QR_CODE, width, height, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }

    private Bitmap buildShareBitmap() {
        shareView.measure(shareView.getMeasuredWidth(), shareView.getMeasuredHeight());
        shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());
        shareView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(shareView.getMeasuredWidth(), shareView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shareView.draw(canvas);
        return bitmap;
    }
}
