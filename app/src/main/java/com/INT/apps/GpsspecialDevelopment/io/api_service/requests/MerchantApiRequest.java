package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealsPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingsPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.Code;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchases;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.FindCouponResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.RedeemCouponResult;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 * Company: Thinkmobiles
 * Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantApiRequest extends ApiRequest {

    public MerchantApiRequest(IOBus bus) {
        super(bus);
    }

    @Subscribe
    public void findCoupon(FindCouponEvent event) {
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().searchCouponCode(event.getCouponCode(), token, new Callback<MerchantPurchaseCoupons.Coupon>() {
            @Override
            public void success(MerchantPurchaseCoupons.Coupon couponResult, Response response) {
                getBus().post(new FindCouponResult(couponResult));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 401 || error.getResponse().getStatus() == 403)
                    return;

                getBus().post(error.getResponse().getStatus() == 404 ?
                        new FindCouponError(true) : new FindCouponError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void merchantPurchases(GetMerchantPurchasesEvent event) {
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().getMerchantPurchases(event.getPage(), token, new Callback<MerchantPurchases>() {
            @Override
            public void success(MerchantPurchases merchantPurchases, Response response) {
                getBus().post(new GetMerchantPurchasesResult(merchantPurchases));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 403)
                    return;

                getBus().post(new GetMerchantPurchasesError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void merchantListings(GetMerchantListingsEvent event) {
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().getMerchantListings(event.getPage(), token, new Callback<ListingsPaging>() {
            @Override
            public void success(ListingsPaging listingsPaging, Response response) {
                getBus().post(new GetMerchantListingsResult(listingsPaging));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                Timber.i("merchant listings failure");
                if (status == 403) return;

                getBus().post(new GetMerchantListingsError(error.getMessage(), status));
            }
        });
    }

    @Subscribe
    public void merchantDeals(GetMerchantDealsEvent event) {
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().getMerchantDeals(event.getPage(), token, new Callback<DealsPaging>() {
            @Override
            public void success(DealsPaging dealsPaging, Response response) {
                getBus().post(new GetMerchantDealsResult(dealsPaging));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new GetMerchantDealsError(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void merchantPurchaseCoupons(final GetMerchantPurchaseCouponsEvent event) {
        UserSession.getUserSession().retrieveCachedUser();
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().getMerchantPurchaseCoupons(event.getDealId(), event.isUsed(), event.getPage(), token, new Callback<MerchantPurchaseCoupons>() {
            @Override
            public void success(MerchantPurchaseCoupons merchantPurchasesCoupons, Response response) {
                getBus().post(new GetMerchantPurchaseCouponsResult(merchantPurchasesCoupons, event.isUsed()));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post(new GetMerchantPurchaseCouponsError(error.getMessage(), event.isUsed()));
            }
        });
    }

    @Subscribe
    public void redeemCoupon(RedeemCouponEvent event) {
        final String token = UserSession.getUserSession().getAuthToken();
        getRequestApi().redeemCouponById(event.getDealId(), "", token, new Callback<Code>() {
            @Override
            public void success(Code redeemedCode, Response response) {
                getBus().post(new RedeemCouponResult(redeemedCode));
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                getBus().post((status == 400 || status == 404) ?
                        new RedeemCouponError(true) : new RedeemCouponError(error.getMessage()));
            }
        });
    }
}
