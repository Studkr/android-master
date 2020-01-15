package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import android.os.AsyncTask;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewAddResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewPaging;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.LoadReviewFormEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewAddRequestEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewAddResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewFormLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewPaginateLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewPaginateLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewViewLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewViewLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by shrey on 27/5/15.
 */
public class ReviewApiRequest extends ApiRequest
{
    private HashMap<String,ReviewAddResult> mReviewResult = new HashMap<>();
    private HashMap<String,Integer> mRequestStatus = new HashMap<>();
    static int STATUS_RUNNING=1;
    static int STATUS_COMPLETED=2;
    static int STATUS_ERROR=3;
    public ReviewApiRequest(IOBus bus)
    {
        super(bus);
    }

    @Subscribe
    public void paginateReviews(ReviewPaginateLoadEvent event)
    {
        getRequestApi().paginateReviews(event.getReviewPaginationQuery(),new Callback<ReviewPaging>(){
            @Override
            public void failure(RetrofitError error)
            {
                IOBus.getInstance().post(new ApiRequestFailedEvent(error));
            }

            @Override
            public void success(ReviewPaging reviewPaging, Response response)
            {
                getBus().post(new ReviewPaginateLoadedEvent(reviewPaging));
            }
        });
    }

    @Subscribe
    public void getReview(ReviewViewLoadEvent event)
    {
        final int position = event.getPosition();
        getRequestApi().reviewViewByPosition(event.getPosition(),event.getQuery(),new Callback<ReviewPaging>() {
            @Override
            public void success(ReviewPaging reviewPaging, Response response)
            {
                Timber.tag("review url").d(response.getUrl());
                getBus().post(new ReviewViewLoadedEvent(reviewPaging.getSingleReview(),position));
            }

            @Override
            public void failure(RetrofitError error)
            {
                getBus().post(new ApiRequestFailedEvent(error));
            }
        });
    }
    @Subscribe
    public void loadReviewForm(LoadReviewFormEvent event)
    {
        final String listingId = event.getListingId();
        AsyncTask<Void,Void,FieldsProperties> task = new AsyncTask<Void, Void, FieldsProperties>() {
            @Override
            protected FieldsProperties doInBackground(Void... params)
            {
                JsonCacheManager cacheManager = JsonCacheManager.
                        getInstance(CrowdvoxApplication.getAppInstance());
                FieldsProperties fieldsProperties = (FieldsProperties)cacheManager.getCachedObject("ReviewForm", listingId, FieldsProperties.class);
                if(fieldsProperties == null)
                {
                    try {
                        fieldsProperties = getRequestApi().getReviewFormSync(listingId);
                    }catch (RetrofitError error)
                    {

                    }
                    Timber.tag("loaded").d("review form loaded");
                    if(fieldsProperties != null)
                    {
                        cacheManager.cacheObject("ReviewForm", listingId, fieldsProperties);
                    }
                }
                return fieldsProperties;
            }

            @Override
            protected void onPostExecute(FieldsProperties fieldProperties)
            {
                if(fieldProperties != null)
                {
                    IOBus.getInstance().post(new ReviewFormLoadedEvent(fieldProperties,listingId));
                }
                super.onPostExecute(fieldProperties);
            }
        };
        task.execute();
    }
    @Subscribe
    public void postNewReview(final ReviewAddRequestEvent event)
    {
        String requestKey = event.getRequestKey();
        if(mRequestStatus.containsKey(requestKey) && mRequestStatus.get(event.getRequestKey()) == STATUS_COMPLETED && mReviewResult.containsKey(requestKey))
        {
            ReviewAddResult reviewAddResult = mReviewResult.get(event.getRequestKey());
            getBus().post(new ReviewAddResultEvent(requestKey,reviewAddResult,event.getListingId()));

        }else if(mRequestStatus.containsKey(requestKey) == false)
        {
            mRequestStatus.put(event.getRequestKey(),STATUS_RUNNING);
            String loginToken = UserSession.getUserSession().getAuthToken();
            getRequestApi().addReview(event.getListingId(),event.getReviewData(),loginToken,new Callback<ReviewAddResult>() {
                @Override
                public void success(ReviewAddResult reviewAddResult, Response response)
                {
                    mRequestStatus.put(event.getRequestKey(),STATUS_COMPLETED);
                    mReviewResult.put(event.getRequestKey(),reviewAddResult);
                    if(mReviewResult.size() > 4)
                    {
                        int i=0;
                        for(String key : mReviewResult.keySet())
                        {
                            if(i < 2)
                            {
                                mReviewResult.remove(key);
                                i++;
                            }
                        }
                    }
                    getBus().post(new ReviewAddResultEvent(event.getRequestKey(),reviewAddResult,event.getListingId()));
                }

                @Override
                public void failure(RetrofitError error)
                {
                    IOBus.getInstance().post(new ApiRequestFailedEvent(error));
                }
            });
        }else if(mRequestStatus.get(requestKey) == STATUS_ERROR)
        {

        }
    }
}
