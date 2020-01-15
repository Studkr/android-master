package com.INT.apps.GpsspecialDevelopment.io.api_service;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Data;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses.BonusInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealInformation;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealOrderResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealsPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PayResultResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeals;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.DynamicCategories;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfoObj;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listings;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingsPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Suggestions;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.AssetAddResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Assets;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.UserCheckins;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.Code;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchases;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.push.DeviceInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewAddResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewData;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.GenerateEphemeralResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.RegisterStripeResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterData;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;
import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;

import org.json.JSONObject;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by shrey on 23/4/15.
 */
public interface ApiService {
    //TODO for receiving location-specific listings, set needed location in the header, for example `X-Location: 50.2593706,30.6217386`
    @GET("/listings/filter.json?is_hot=true")
    void getHotListings(@Query("LOGIN_TOKEN") String loginToken, retrofit.Callback<Listings> cb);

    //TODO for receiving location-specific listings, set needed location in the header, for example `X-Location: 50.2593706,30.6217386`
    @GET("/listings/filter.json")
    void paginateListings(@QueryMap Map<String, String> parameters, @Query("LOGIN_TOKEN") String loginToken, retrofit.Callback<ListingPaging> cb);

    @GET("/listings/suggestKeywords.json")
    Suggestions suggestListingKeywordsSync(@Query("q") String keyword);

    @GET("/listing_assets/assetList/{listingId}/{limit}.json")
    void getListingAssets(@Path("listingId") String listingId, @Path("limit") int limit, retrofit.Callback<Assets> cb);

    @GET("/reviews/index.json")
    void paginateReviews(@Query("paginationQuery") ReviewPaginationQuery query, Callback<ReviewPaging> cb);

    @GET("/reviews/view/byPosition:{position}.json")
    void reviewViewByPosition(@Path("position") int position, @Query("paginationQuery") ReviewPaginationQuery query, Callback<ReviewPaging> cb);

    @FormUrlEncoded
    @POST("/users/login.json")
    void login(@Field("data[User][email]") String email, @Field("data[User][password]") String password, Callback<LoginResult> cb);

    @FormUrlEncoded
    @POST("/users/edit_profile.json")
    void editProfile(@Query("LOGIN_TOKEN") String loginToken,
                     @Field("data[User][first_name]") String firstName,
                     @Field("data[User][last_name]") String lastName,
                     @Field("data[User][phone]") String phone,
                     Callback<EditResult> cb);

    @GET("/users/loginByFacebook/{authToken}.json")
    void loginByFacebook(@Path("authToken") String authToken, Callback<LoginResult> cb);

    @GET("/users/registerForm.json")
    FieldsProperties getRegistrationFieldsSync();

    @POST("/users/register.json")
    void registerUser(@Body RegisterData registerData, Callback<RegisterResult> cb);

    @POST("/api/users/register_device.json")
    DeviceInfo registerDevice(@Body Data<DeviceInfo> deviceInfo, @Query("LOGIN_TOKEN") String loginToken);

    @GET("/reviews/reviewForm/{listingId}.json")
    FieldsProperties getReviewFormSync(@Path("listingId") String listingId);

    @POST("/reviews/add/{listingId}.json")
    void addReview(@Path("listingId") String listingId, @Body ReviewData reviewData, @Query("LOGIN_TOKEN") String loginToken, Callback<ReviewAddResult> cb);

    @POST("/listing_assets/multiAdd/{listingId}.json")
    void uploadListingImages(@Path("listingId") String lisitngId, @Body MultipartTypedOutput images, @Query("LOGIN_TOKEN") String loginToken, Callback<AssetAddResult> cb);

    @GET("/users/user_favorites/add/{listingId}/Listing.json")
    void bookMarkListing(@Path("listingId") String listingId, @Query("LOGIN_TOKEN") String loginToken, Callback<Object> cb);

    @GET("/users/user_favorites/remove/{listingId}/Listing.json")
    void unBookMarkListing(@Path("listingId") String listingId, @Query("LOGIN_TOKEN") String loginToken, Callback<Object> cb);

    @GET("/deals/view/{dealId}.json")
    void dealView(@Path("dealId") String dealId, Callback<DealInformation> cb);

    // "Dynamic"
    @GET("/categories/listing.json")
    void getCategoryAsync(Callback<DynamicCategories> cb);

    /**
     * /textmessages/findnearbyoffers/\(UserID)/\(Latitude)/\(longitude).json
     */
    //@saqib
    @GET("/textmessages/findnearbyoffers/{UserID}/{Latitude}/{longitude}.json")
    void updateUserLocation(@Path("UserID") String userId, @Path("Latitude") String lat, @Path("longitude") String lang, Callback<Object> cb);

    @FormUrlEncoded
    @POST("/checkins/checkins/handler.json")
    void doUserCheckIn(@Field("data[Checkin][entity_id]") String listingId, @Field("data[Checkin][comment]") String comment, @Query("LOGIN_TOKEN") String loginToken, Callback<Object> callback);

    @GET("/users/users/profile/{userId}.json")
    void userProfile(@Path("userId") String userId, @Query("LOGIN_TOKEN") String loginToken, Callback<UserProfile> userProfile);

    @GET("/users/users/myProfile.json")
    void myProfile(@Query("LOGIN_TOKEN") String loginToken, Callback<UserProfile> userProfile);

    //Dummy, because else have error method POST must have a request body
    @POST("/users/register_stripe/{id}.json")
    void registerInStripe(@Path("id") String userId, @Query("LOGIN_TOKEN") String loginToken, @Body Object dummy, Callback<RegisterStripeResponse> stripeResponseCallback);

    //Dummy, because else have error method POST must have a request body
    @POST("/users/generate_ephemeral_key/{api_version}/{customer_id}.json")
    void generateEphemeralKey(@Path("api_version") String apiVersion, @Path("customer_id") String customerId, @Query("LOGIN_TOKEN") String loginToken, @Body Object dummy, Callback<GenerateEphemeralResponse> ephemeralResponseCallback);

    @GET("/checkins/checkins/userCheckins/{userId}/page:{page}.json")
    void userCheckins(@Path("userId") String userId, @Path("page") Integer page, Callback<UserCheckins> cb);

    @GET("/deals/deal_orders/myDeals.json")
    void myPurchasedDeals(@Header("X-Location") String location, @Query("is_used") boolean isUsed, @Query("LOGIN_TOKEN") String loginToken, Callback<PurchasedDeals> cb);

    @FormUrlEncoded
    @POST("/deals/deal_orders/pay.json")
    void requestToPay(
            @Field("data[card_id]") String cardId,
            @Field("data[deal_id]") int dealId,
            @Field("data[amount]") double amount,
            @Field("data[currency]") String currency,
            @Field("data[currency_symbol]") String currencySymbol,
            @Field("data[quantity]") int quantity,
            @Field("data[tax]") double tax,
            @Field("data[bonuses]") int points,
            @Query("LOGIN_TOKEN") String loginToken, Callback<PayResultResponse> cb);

    @GET("/deals/get_info_for_order/{dealId}.json")
    void getOrderInfo(@Header("X-Location") String location, @Path("dealId") String dealId, @Query("LOGIN_TOKEN") String loginToken, Callback<DealOrderResponse> cb);

    @GET("/listings/view/{listingId}.json")
    void listingDetail(@Path("listingId") String listingId, @Query("location") String location, @Query("LOGIN_TOKEN") String loginToken, Callback<ListingResponse> listingCallback);

    @GET("/api/listings/{listingId}/deals.json")
    void getListOfDeals(@Path("listingId") String listingId, @Query("page") int page,  Callback<DealInfoObj> dealsResponse);

    //09-05-17-(call while logout)
    @GET("/users/users/appLogout/{userId}.json")
    void logoutProfile(@Header("X-Location") String location, @Path("userId") String userId, @Query("LOGIN_TOKEN") String loginToken, Callback<JSONObject> userProfile);

    @GET("/api/coupons/code/{coupon_code}.json")
    void searchCouponCode(@Path("coupon_code") String couponCode, @Query("LOGIN_TOKEN") String loginToken, Callback<MerchantPurchaseCoupons.Coupon> cb);

    @GET("/api/merchant/purchases.json")
    void getMerchantPurchases(@Query("page") int page,  @Query("LOGIN_TOKEN") String loginToken, Callback<MerchantPurchases> cb);

    @GET("/api/merchant/listings.json")
    void getMerchantListings(@Query("page") int page,  @Query("LOGIN_TOKEN") String loginToken, Callback<ListingsPaging> cb);

    @GET("/api/merchant/deals.json")
    void getMerchantDeals(@Query("page") int page,  @Query("LOGIN_TOKEN") String loginToken, Callback<DealsPaging> cb);

    @GET("/api/merchant/purchases/{deal_id}/coupons.json")
    void getMerchantPurchaseCoupons(@Path("deal_id") String dealId, @Query("is_used") boolean isUsed, @Query("page") int page, @Query("LOGIN_TOKEN") String loginToken, Callback<MerchantPurchaseCoupons> cb);

    @PATCH("/api/coupons/{deal_id}/redeem.json")
    void redeemCouponById(@Path("deal_id") String dealId, @Body Object dummy, @Query("LOGIN_TOKEN") String loginToken, Callback<Code> cb);

    @GET("/api/users/bonuses.json")
    void getBonuses(@Query("LOGIN_TOKEN") String loginToken, Callback<BonusInfo> callback);
}
