package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import android.net.Uri;
import android.os.AsyncTask;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingResponse;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listings;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Suggestion;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Suggestions;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.AssetAddResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Assets;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.CheckinInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.UserCheckins;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.DoBusinessCheckIn;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingsLoadingFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ListingPaginationDataEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ListingPaginationFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.RequestListingPaginationEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ToggleBookMarkEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.UserCheckedInEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.ListingImageUploadFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.ListingImagesPostedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.PostListingImagesEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins.FetchUserCheckinsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins.OnUserCheckinsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingGalleryLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingGalleryLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingLoadFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.ListingViewLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view.LoadListingViewEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.DevicePicture;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by shrey on 23/4/15.
 */
public class ListingApiRequest extends ApiRequest {
    private HashMap<String, AssetAddResult> mAssetResult = new HashMap<>();
    private HashMap<String, Integer> mRequestStatus = new HashMap<>();
    static int STATUS_RUNNING = 1;
    static int STATUS_COMPLETED = 2;
    static int STATUS_ERROR = 3;

    public ListingApiRequest(IOBus bus) {
        super(bus);
    }

    @Subscribe
    public void loadHotListings(HotListingLoadEvent loadEvent) {
        String location = loadEvent.getLatitude() + "," + loadEvent.getLongitude();
        String loginToken = UserSession.getUserSession().getAuthToken();
        getRequestApi().getHotListings(loginToken, new Callback<Listings>() {
            @Override
            public void success(final Listings listings, Response response) {
                AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
                    List<Listing_> listing_s;

                    @Override
                    protected Void doInBackground(Integer... params) {
                        listings.setListingCategories();
                        listing_s = new ArrayList<>();
                        for (Listing listingInfo : listings.getListings()) {
                            listing_s.add(listingInfo.getListing());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        cacheListings(listing_s);
                        getBus().post(new HotListingLoadedEvent(listings));
                        super.onPostExecute(aVoid);
                    }
                };
                if (listings != null)
                    task.execute(1);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d(error, "----** failure(RetrofitError error)");
                getBus().post(new HotListingsLoadingFailedEvent(error));
            }
        });
    }


    @Subscribe
    public void paginateListings(final RequestListingPaginationEvent requestEvent) {
        AsyncTask<Integer, Void, ListingPaging> task = new AsyncTask<Integer, Void, ListingPaging>() {
            @Override
            protected ListingPaging doInBackground(Integer... params) {
                JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
                //
                Object result = cacheManager
                        .getCacheObjectByKey(requestEvent.getRequestKey(), ListingPaging.ListingPagingCache.class);
                if (result != null) {
                    ListingPaging.ListingPagingCache cache = (ListingPaging.ListingPagingCache) result;
                    ListingPaging listingPaging = new ListingPaging();
                    listingPaging.setPaging(cache.getPaging());
                    ArrayList<Listing> listings = new ArrayList<>();
                    for (Integer listingId : cache.getListingIds()) {
                        Listing_ listing_ = (Listing_) cacheManager.getCachedObject("Listing", listingId.toString(), Listing_.class);
                        if (listing_ != null) {
                            Listing listing = new Listing();
                            listing.setListing(listing_);
                            listings.add(listing);
                        }
                    }
                    listingPaging.setListings(listings);
                    return listingPaging;
                }
                return null;
            }

            @Override
            protected void onPostExecute(ListingPaging listingPaging) {
                if (listingPaging == null) {
                    sendPaginationRequest(requestEvent);
                } else {
                    getBus().post(new ListingPaginationDataEvent(listingPaging, requestEvent.getRequestKey()));
                }
            }
        };
        if (requestEvent.getRequestKey() != null) {
            task.execute(1);
        } else {
            sendPaginationRequest(requestEvent);
        }
    }

    private void sendPaginationRequest(RequestListingPaginationEvent requestEvent) {
        String loginToken = UserSession.getUserSession().getAuthToken();
        getRequestApi().paginateListings(requestEvent.getQueryBuilder().buildForApi(), loginToken, new Callback<ListingPaging>() {
            @Override
            public void success(final ListingPaging listingPaging, Response response) {
                AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        //running in background thread
                        //for performance purpose.
                        List<Listing> listings = listingPaging.getListings();
                        Listings listingsWrapper = new Listings();
                        listingsWrapper.setListings(listings);
                        listingsWrapper.setListingCategories();
                        listings = listingsWrapper.getListings();
                        listingPaging.setListings(listings);
                        ArrayList<Integer> listingIds = new ArrayList<Integer>();
                        JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
                        List<Listing_> listing_s = new ArrayList<Listing_>();
                        for (Listing listing : listings) {
                            Integer listingId = Integer.parseInt(listing.getListing().getId());
                            listingIds.add(listingId);
                            listing_s.add(listing.getListing());
                        }
                        cacheListings(listing_s);
                        ListingPaging.ListingPagingCache pagingCache = new ListingPaging.ListingPagingCache();
                        pagingCache.setPaging(listingPaging.getPaging());
                        pagingCache.setListingIds(listingIds);
                        String requestKey = cacheManager.cacheObject("ListingPaging", "1", pagingCache);
                        return requestKey;
                    }

                    @Override
                    protected void onPostExecute(String requestKey) {
                        getBus().post(new ListingPaginationDataEvent(listingPaging, requestKey));
                        super.onPostExecute(requestKey);
                    }
                };
                task.execute(1);
            }

            @Override
            public void failure(RetrofitError error) {
                getBus().post(new ListingPaginationFailedEvent(error));
            }
        });
    }

    private void cacheListings(final List<Listing_> listings) {
        JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
        for (Listing_ listing : listings) {
            Integer listingId = Integer.parseInt(listing.getId());
            cacheManager.cacheObject("Listing", listingId.toString(), listing);
        }
    }

    public List<Suggestion> listingKeywordSuggestionSync(String matchWith) {
        List<Suggestion> srList = new ArrayList<>();
        try {
            Suggestions sr = getRequestApi().suggestListingKeywordsSync(matchWith);
            return sr.getSuggestions();
        } catch (RetrofitError error) {

        }
        return srList;

    }

    @Subscribe
    public void getListingInformation(final LoadListingViewEvent event) {
        final AsyncTask<Void, Void, Listing_> task = new AsyncTask<Void, Void, Listing_>() {
            @Override
            protected Listing_ doInBackground(Void... params) {
                String listingId = event.getListingId();
                Listing_ listing_ = (Listing_) JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                        .getCachedObject("Listing", listingId, Listing_.class);
                return listing_;
            }

            @Override
            protected void onPostExecute(Listing_ listing) {
                String listingId = event.getListingId();
                if (listing == null) {
                    getListingFromServer(event);
                } else {
                    getBus().post(new ListingViewLoadedEvent(listing));
                }
                super.onPostExecute(listing);
            }
        };
        if (event.shouldLoadFromServer()) {
            getListingFromServer(event);
        } else {
            task.execute();
        }
    }

    private void cacheListingInThread(Listing listing) {
        final AsyncTask<Listing_, Void, Void> task = new AsyncTask<Listing_, Void, Void>() {
            @Override
            protected Void doInBackground(Listing_... params) {
                JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance()).cacheObject("Listing", params[0].getId(), params[0]);
                return null;
            }
        };
        task.execute(listing.getListing());
    }

    private void getListingFromServer(LoadListingViewEvent event) {
        String loginToken = UserSession.getUserSession().getAuthToken();
        getRequestApi().listingDetail(event.getListingId(), event.getLatLng(), loginToken, new Callback<ListingResponse>() {
            @Override
            public void success(ListingResponse listing, Response response) {
                List<Listing_> listings = new ArrayList<>();
                listings.add(listing.getListing().getListing());
                Listings.setListingCategories(listings);
                getBus().post(new ListingViewLoadedEvent(listing.getListing().getListing()));
                cacheListingInThread(listing.getListing());

            }

            @Override
            public void failure(RetrofitError error) {
                IOBus.getInstance().post(new ListingLoadFailedEvent(error));
            }
        });
    }

    @Subscribe
    public void getListingAsset(ListingGalleryLoadEvent event) {
        getRequestApi().getListingAssets(event.getListingId(), event.getLimit(), new Callback<Assets>() {
            @Override
            public void success(Assets assets, Response response) {
                getBus().post(new ListingGalleryLoadedEvent(assets));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe
    public void uploadListingImages(final PostListingImagesEvent event) {
        final String requestKey = event.getRequestKey();
        if (mRequestStatus.containsKey(requestKey) && mRequestStatus.get(event.getRequestKey()) == STATUS_COMPLETED && mAssetResult.containsKey(requestKey)) {
            AssetAddResult assetAddResult = mAssetResult.get(requestKey);
            getBus().post(new ListingImagesPostedEvent(assetAddResult));
        } else if (mRequestStatus.containsKey(requestKey) == false) {
            mRequestStatus.put(event.getRequestKey(), STATUS_RUNNING);
            final ArrayList<String> compressedFiles = new ArrayList();
            AsyncTask<Void, Void, MultipartTypedOutput> task = new AsyncTask<Void, Void, MultipartTypedOutput>() {
                @Override
                protected MultipartTypedOutput doInBackground(Void... params) {

                    ArrayList<String> filesPath = event.getFilesPath();
                    MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
                    for (int i = 0; i < filesPath.size(); i++) {
                        String filePath = filesPath.get(i);
                        if (filePath != null) {
                            String compressedFile = DevicePicture.compressImage(Uri.parse(filePath));
                            compressedFiles.add(compressedFile);
                            TypedFile file = new TypedFile("image/jpg", new File(compressedFile));
                            multipartTypedOutput.addPart("data[ListingAsset][image][" + i + "]", file);
                        }
                    }
                    return multipartTypedOutput;
                }

                @Override
                protected void onPostExecute(MultipartTypedOutput multipartTypedOutput) {
                    String loginToken = UserSession.getUserSession().getAuthToken();
                    getRequestApi().uploadListingImages(event.getListingId(), multipartTypedOutput, loginToken, new Callback<AssetAddResult>() {
                        @Override
                        public void success(AssetAddResult assetResult, Response response) {
                            mRequestStatus.put(requestKey, STATUS_COMPLETED);
                            mAssetResult.put(requestKey, assetResult);
                            getBus().post(new ListingImagesPostedEvent(assetResult));
                            deleteFilesInThread(compressedFiles);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            getBus().post(new ListingImageUploadFailedEvent(error));
                            deleteFilesInThread(compressedFiles);
                        }
                    });
                }
            };
            task.execute();

        }
    }

    private void deleteFilesInThread(ArrayList<String> files) {
        AsyncTask<ArrayList<String>, Void, Void> task = new AsyncTask<ArrayList<String>, Void, Void>() {
            @Override
            protected Void doInBackground(ArrayList<String>... files) {
                for (String file : files[0]) {
                    File fileObject = new File(file);
                    fileObject.delete();

                }
                return null;
            }
        };
        task.execute(files);
    }

    @Subscribe
    public void toggleBookMark(ToggleBookMarkEvent event) {
        if (UserSession.getUserSession().isLoggedIn() == false) {
            return;
        }
        String loginToken = UserSession.getUserSession().getAuthToken();
        final Listing_ listing = event.getListing();
        if (event.isBookMarked()) {
            getRequestApi().bookMarkListing(event.getListing().getId(), loginToken, new Callback<Object>() {
                @Override
                public void success(Object o, Response response) {
                    listing.setBookMarked(true);
                    JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
                    cacheManager.cacheObject("Listing", listing.getId(), listing);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            getRequestApi().unBookMarkListing(event.getListing().getId(), loginToken, new Callback<Object>() {
                @Override
                public void success(Object o, Response response) {
                    JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
                    listing.setBookMarked(false);
                    cacheManager.cacheObject("Listing", listing.getId(), listing);

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    @Subscribe
    public void doBusinessCheckIn(DoBusinessCheckIn doBusinessCheckIn) {
        final String listingId = doBusinessCheckIn.getListingId();
        getRequestApi().doUserCheckIn(doBusinessCheckIn.getListingId(), doBusinessCheckIn.getComment(), UserSession.getUserSession().getAuthToken(), new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                IOBus.getInstance().post(new UserCheckedInEvent());
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        JsonCacheManager cacheManager = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance());
                        Listing_ listing = (Listing_) cacheManager.getCachedObject("Listing", listingId, Listing_.class);
                        if (listing != null) {
                            listing.setCheckedIn(true);
                        }
                        cacheManager.cacheObject("Listing", listing.getId(), listing);
                        return null;
                    }
                };
                task.execute();
            }

            @Override
            public void failure(RetrofitError error) {
                IOBus.getInstance().post(new UserCheckedInEvent());
            }
        });
    }

    @Subscribe
    public void getUserCheckins(FetchUserCheckinsEvent event) {
        Timber.d("----** Checking request received");
        Timber.d("----** Checking request user id: %s", event.getUserId());
        Timber.d("----** Checking request page: %d", event.getPage());
        String userId = event.getUserId();
        Timber.d("----** Checking request received 1");
        getRequestApi().userCheckins(event.getUserId(), event.getPage(), new Callback<UserCheckins>() {
            @Override
            public void success(final UserCheckins userCheckins, Response response) {
                Timber.d("----** Checking request received success");
                Timber.d("----** Checkin size %d", userCheckins.getCheckins().size());

                for(int i=0;i<userCheckins.getCheckins().size();i++){

                }
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        List<Listing_> listings = new ArrayList<Listing_>();
                        for (CheckinInfo checkinInfo : userCheckins.getCheckins()) {
                            listings.add(checkinInfo.getListing());
                        }
                        Listings.setListingCategories(listings);
                        cacheListings(listings);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        IOBus.getInstance().post(new OnUserCheckinsEvent(userCheckins));
                        super.onPostExecute(aVoid);
                    }
                };
                task.execute();
            }

            @Override
            public void failure(RetrofitError error) {
                final int status = error.getResponse().getStatus();
                if (status == 401 || status == 403)
                    return;

                IOBus.getInstance().post(new ApiRequestFailedEvent(error));
            }
        });
    }
}
