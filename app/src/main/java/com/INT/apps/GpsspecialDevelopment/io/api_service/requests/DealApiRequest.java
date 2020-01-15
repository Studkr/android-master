package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealInformation;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealOrderResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PayResultResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeals;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfoObj;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.GenerateEphemeralResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.RegisterStripeResponse;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.DealInformationLoaded;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadDealInformationEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadInfoOrderEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDeals;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDealsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDealsResponse;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadMyPurchasedDealsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnMyPurchasedDealsErrorEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnMyPurchasedDealsLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnRequestToPayResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OrderInfoLoadError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OrderInformationLoaded;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.RequestToPayEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.GenerateEphemeralKeyEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnEphemeralKeyGeneratedError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnEphemeralKeyGeneratedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnUserRegisteredInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.RegisterUserInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by shrey on 17/7/15.
 */
public class DealApiRequest extends ApiRequest {
    public DealApiRequest(IOBus bus) {
        super(bus);
    }

    @Subscribe
    public void loadDeal(LoadDealInformationEvent event) {
        Timber.tag("deal loaded").d( "Loading");
        getRequestApi().dealView(event.getDealId(), new Callback<DealInformation>() {
            @Override
            public void success(DealInformation dealInformation, Response response) {
                Timber.tag("deal loaded").d("Lodaded");
                getBus().post(new DealInformationLoaded(dealInformation));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.tag("erro w").d(error);
                getBus().post(new ApiRequestFailedEvent(error));
            }
        });
    }

    @Subscribe
    public void loadMyPurchasedDeals(final LoadMyPurchasedDealsEvent event) {
        String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().myPurchasedDeals(event.getLocation(), event.isUsed(), token, new Callback<PurchasedDeals>() {
            @Override
            public void success(PurchasedDeals purchasedDeals, Response response) {
                getBus().post(new OnMyPurchasedDealsLoadedEvent(purchasedDeals, event.isUsed()));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new OnMyPurchasedDealsErrorEvent(error.getMessage(), event.isUsed()));
            }
        });
    }

    @Subscribe
    public void getListOfDeals(LoadListOfDeals loadListOfDeals) {
        getRequestApi().getListOfDeals(loadListOfDeals.getListingId(), loadListOfDeals.getPage(), new Callback<DealInfoObj>() {
            @Override
            public void success(DealInfoObj dealInfoObj, Response response) {
                getBus().post(new LoadListOfDealsResponse(dealInfoObj));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new LoadListOfDealsError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void registerInStripe(RegisterUserInStripeEvent event) {
        String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().registerInStripe(event.getUserId(), token, "", new Callback<RegisterStripeResponse>() {
            @Override
            public void success(RegisterStripeResponse registerStripeResponse, Response response) {
                getBus().post(new OnUserRegisteredInStripeEvent(registerStripeResponse.registerResult));
            }

            @Override
            public void failure(RetrofitError error) {
                getBus().post(new OnUserRegisteredInStripeEvent(null));
            }
        });
    }

    @Subscribe
    public void generateEphemeralKey(GenerateEphemeralKeyEvent event) {
        String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().generateEphemeralKey(event.getApiVersion(), event.getCustomerId(), token, "", new Callback<GenerateEphemeralResponse>() {
            @Override
            public void success(GenerateEphemeralResponse generateEphemeralResponse, Response response) {
                Timber.i("onEphemeral generated");
                getBus().post(new OnEphemeralKeyGeneratedEvent(generateEphemeralResponse.key.toString()));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error);
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new OnEphemeralKeyGeneratedError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void loadOrderInfo(LoadInfoOrderEvent event) {
        Timber.d("----**LoadInfoOrderEvent()->>Receive");
        String location = event.getLat() + "," + event.getLng();
        String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().getOrderInfo(location, event.getDealId(), token, new Callback<DealOrderResponse>() {

            @Override
            public void success(DealOrderResponse orderResponse, Response response) {
                getBus().post(new OrderInformationLoaded(orderResponse.getInfo()));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new OrderInfoLoadError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void requestToPay(RequestToPayEvent event) {
        Timber.d("----**LoadInfoOrderEvent()->>Receive");
        String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().requestToPay(
                event.getCardId(),
                event.getDealId(),
                event.getAmount(),
                event.getCurrency(),
                event.getCurrencySymbol(),
                event.getQuantity(),
                event.getTax(),
                event.getPoints(),
                token, new Callback<PayResultResponse>() {
                    @Override
                    public void success(PayResultResponse payResultResponse, Response response) {
                        getBus().post(new OnRequestToPayResult(payResultResponse.getPayResult()));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String errorMsg;
                        if (error.getBody() instanceof PayResultResponse) {
                            PayResultResponse payResultResponse = (PayResultResponse) error.getBody();
                            if (payResultResponse.getPayResult().errorsValidation != null)
                                errorMsg = TextUtils.join(",\n", payResultResponse.getPayResult().errorsValidation);
                            else errorMsg = payResultResponse.getPayResult().errorMsg;
                        } else errorMsg = error.getMessage();
                        getBus().post(new OnRequestToPayResult(errorMsg));
                    }
                });
    }
}
