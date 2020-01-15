package com.INT.apps.GpsspecialDevelopment.io.stripe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.BuildConfig;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.GenerateEphemeralKeyEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnEphemeralKeyGeneratedError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnEphemeralKeyGeneratedEvent;
import com.squareup.otto.Subscribe;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Customer;

import timber.log.Timber;

/**
 * @author Michael Soyma (Created on 9/6/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class StripeManager {

    private static StripeManager instance;
    private Stripe stripe;
    private String lastInitStripeAccount;

    private InitializeListener initializeListener;
    private EphemeralKeyUpdateListener keyUpdateListener;

    private StripeManager(final Context context) {
        stripe = new Stripe(context, BuildConfig.STRIPE_PUB_KEY);
        PaymentConfiguration.init(BuildConfig.STRIPE_PUB_KEY);
    }

    public static StripeManager getInstance(final Context context) {
        if (instance == null)
            instance = new StripeManager(context);
        return instance;
    }

    public void initCustomer(final String customerId) {
        Timber.i("Init customer");
        try {
            CustomerSession customerSession = CustomerSession.getInstance();
            if (customerSession.getCachedCustomer() != null && !TextUtils.isEmpty(customerId) && customerId.equals(lastInitStripeAccount)) {
                if (initializeListener != null)
                    initializeListener.onInitialized();
                return;
            }
        } catch (IllegalStateException ignored) {}

        lastInitStripeAccount = null;
        IOBus.getInstance().unregister(this);
        IOBus.getInstance().register(this);
        CustomerSession.initCustomerSession(new EphemeralKeyProvider() {
            @Override
            public void createEphemeralKey(@NonNull @Size(min = 4L) String apiVersion, @NonNull final EphemeralKeyUpdateListener listener) {
                keyUpdateListener = listener;
                IOBus.getInstance().post(new GenerateEphemeralKeyEvent(apiVersion, customerId));
            }
        });
        CustomerSession.getInstance().retrieveCurrentCustomer(
                new CustomerSession.CustomerRetrievalListener() {
                    @Override
                    public void onCustomerRetrieved(@NonNull Customer customer) {
                        lastInitStripeAccount = customerId;
                        if (initializeListener != null)
                            initializeListener.onInitialized();
                    }

                    @Override
                    public void onError(int errorCode, @Nullable String errorMessage) {
                        Timber.tag("StripeManager").d("%d | %s", errorCode, errorMessage);
                        if (initializeListener != null)
                            initializeListener.onError(errorMessage);
                    }
                });
    }

    @Subscribe
    public void OnEphemeralKeyGenerated(final OnEphemeralKeyGeneratedEvent keyGeneratedEvent) {
        Timber.i("onEphemeralKeyGenerated");
//        IOBus.getInstance().unregister(this);
        if (keyUpdateListener != null)
            keyUpdateListener.onKeyUpdate(keyGeneratedEvent.getKey());
    }

    @Subscribe
    public void OnEphemeralKeyGeneratedError(final OnEphemeralKeyGeneratedError keyGeneratedError) {
        IOBus.getInstance().unregister(this);
        if (initializeListener != null)
            initializeListener.onError(keyGeneratedError.getError());
    }

    public void setInitializeListener(InitializeListener initializeListener) {
        this.initializeListener = initializeListener;
    }

    public interface InitializeListener {
        void onInitialized();
        void onError(String msg);
    }
}
