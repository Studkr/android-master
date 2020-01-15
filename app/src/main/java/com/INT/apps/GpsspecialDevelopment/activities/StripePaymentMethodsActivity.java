package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Context;
import android.content.Intent;

import com.stripe.android.view.PaymentMethodsActivity;

/**
 * @author Michael Soyma (Created on 9/12/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class StripePaymentMethodsActivity extends PaymentMethodsActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, StripePaymentMethodsActivity.class);
    }
}
