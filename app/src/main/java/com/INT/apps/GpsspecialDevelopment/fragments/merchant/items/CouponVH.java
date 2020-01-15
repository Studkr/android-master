package com.INT.apps.GpsspecialDevelopment.fragments.merchant.items;

import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class CouponVH {

    private final View contentView;

    @InjectView(R.id.user_avatar)
    ImageView userAvatarView;
    @InjectView(R.id.user_name)
    TextView userNameView;
    @InjectView(R.id.purchased_date)
    TextView purchasedDateView;
    @InjectView(R.id.coupon_code)
    TextView couponCodeView;

    private int defaultPaintFlags;

    public CouponVH(View contentView) {
        ButterKnife.inject(this, this.contentView = contentView);
        defaultPaintFlags = couponCodeView.getPaintFlags();
    }

    public void inject(final MerchantPurchaseCoupons.Coupon coupon) {
        String imageUrl = CvUrls.getImageUrlForSize(coupon.getUser().getAvatar(), 80, 80, true);
        Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder100).fit().centerCrop().into(userAvatarView);

        userNameView.setText(coupon.getUserName());
        purchasedDateView.setText(coupon.getDatePurchased());
        couponCodeView.setText(coupon.getDealOrderCode().getDealCode());
        couponCodeView.setPaintFlags(coupon.getDealOrderCode().isUsed() ? (defaultPaintFlags |  Paint.STRIKE_THRU_TEXT_FLAG) : defaultPaintFlags);
        couponCodeView.setTextColor(ContextCompat.getColor(contentView.getContext(), coupon.getDealOrderCode().isUsed() ? R.color.dark_grey : R.color.taupe_black));
    }
}
