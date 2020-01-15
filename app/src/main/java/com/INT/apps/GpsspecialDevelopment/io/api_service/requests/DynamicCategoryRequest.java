package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.INT.apps.GpsspecialDevelopment.data.models.DynamicCategoryHelper;
import com.INT.apps.GpsspecialDevelopment.data.models.DynamicCategoryModel;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.DynamicCategories;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.DynamicCategoryErrorModel;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.GetDynamicCategory;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.SetDynamicCategory;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by msaqib on 7/14/2016.
 */
public class DynamicCategoryRequest extends ApiRequest {

    Context context;

    // "Dynamic"
    public DynamicCategoryRequest(IOBus bus) {
        super(bus);
    }

    public DynamicCategoryRequest(Context context, IOBus bus) {
        super(bus);
        this.context = context;
    }

    @Subscribe
    public void getAllCategory(GetDynamicCategory category) {
        Timber.d("getAllCategories");
        Timber.d("#### category --2");
        getRequestApi().getCategoryAsync(new Callback<DynamicCategories>() {
            @Override
            public void success(DynamicCategories dynamicCategories, Response response) {
                Timber.d("#### category --3");
                handleResult(dynamicCategories);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d("#### category --3 with error");
                handleError(error);
            }
        });
    }

    private void handleResult(final DynamicCategories dynamicCategories) {

        Timber.d("#### category -- data -> %d", dynamicCategories.getCategories().size());

        int size = dynamicCategories.getCategories().size();

        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            DynamicCategoryHelper helper = new DynamicCategoryHelper(context);

            @Override
            protected String doInBackground(String... params) {
                helper.removeOldCategory();
                return null;
            }

            @Override
            protected void onPostExecute(String string) {
                super.onPostExecute(string);

                ArrayList<DynamicCategoryModel> list = dynamicCategories.getCategories();
                //System.out.println("## category size -> " + list.size());
                try {
                    helper.addNewCategory(list);
                } catch (Exception e) {
                    getBus().post(new SetDynamicCategory(dynamicCategories));
                }
                // System.out.println("## category in db -> " + helper.getCategoryCount());
                getBus().post(new SetDynamicCategory(dynamicCategories));

                //Following code is via direct model
                /*for(int i=0;i<list.size();i++){
                    helper.addNewCategoryViaModel(category.getDynamicCategories().getCategories().get(i));
                    System.out.println("## category insert -> " +i);
                }*/
            }
        };
        task.execute();
    }

    private void handleError(RetrofitError error) {
        getBus().post(new DynamicCategoryErrorModel(error));
    }
}
