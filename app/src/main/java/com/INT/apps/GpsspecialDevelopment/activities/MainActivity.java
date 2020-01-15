package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.Installer;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.DynamicCategoryErrorModel;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.GetDynamicCategory;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category.SetDynamicCategory;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.CheckUserSessionEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadRegisterFormEvent;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import timber.log.Timber;
//Relic performace tool(22-9-2016)


public class MainActivity extends BaseActivity {
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //SAQIB (22-9-2016)
        //New Relic Performance tool
        //------------------------------------
        NewRelic.withApplicationToken(
                "AA807caf17b0de601e5d113748bae7cfd737cac345"
        ).start(this.getApplication());
        //------------------------------------
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.main_activity_progressbar);

     /*   SQLiteDatabase db1= openOrCreateDatabase(
                "categories.sqlite"
                , SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE
                , null
        );

        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " +  "categories" + "( _id INTEGER," +
                "category_name TEXT ,image_name TEXT ,cv_id INTEGER ,parent_id INTEGER , children_count INTEGER)";
        db1.execSQL(CREATE_CONTACTS_TABLE);*/
        try {
            Installer.install(this.getApplicationContext());
        } catch (IOException e) {
            Timber.tag("crowdvoxinstall").d(e);
        }

        // "Dynamic"
        // System.out.println("#### category --1");
        IOBus.getInstance().post(new GetDynamicCategory(true));
        progressBar.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new CheckUserSessionEvent());
        IOBus.getInstance().post(new LoadRegisterFormEvent());

       /* Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_DISPLAY_LENGTH);

                    //DynamicCategoryModel model = new DynamicCategoryModel(12345,"category_name","image_name",12345,null,0);
                    //DynamicCategoryHelper helper = new DynamicCategoryHelper(MainActivity.this);
                    //helper.addDatabaseDetail(model);



                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        timerThread.start();*/


    }

    @Subscribe
    public void dynamicCategoryError(DynamicCategoryErrorModel error) {
        Toast.makeText(MainActivity.this, R.string.connectivity_failure, Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, R.string.check_your_connection, Toast.LENGTH_LONG).show();
        openHomeButton();
    }

    @Subscribe
    public void setDynamicCategory(final SetDynamicCategory category) {
        ///System.out.println("#### category --4");
        //System.out.println("#### category -- data main-> " + category.getDynamicCategories().getCategories().get(0).getCategory_name());

//        int size = category.getDynamicCategories().getCategories().size();
//
//        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
//            DynamicCategoryHelper helper = new DynamicCategoryHelper(MainActivity.this);
//
//            @Override
//            protected String doInBackground(String... params) {
//                helper.removeOldCategory();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String string) {
//                super.onPostExecute(string);
//
//                ArrayList<DynamicCategoryModel> list = category.getDynamicCategories().getCategories();
//                //System.out.println("## category size -> " + list.size());
//                try {
//                    helper.addNewCategory(list);
//                } catch (Exception e) {
//                    openHomeButton();
//                }
//                // System.out.println("## category in db -> " + helper.getCategoryCount());
//                openHomeButton();
//
//                //Following code is via direct model
//                /*for(int i=0;i<list.size();i++){
//                    helper.addNewCategoryViaModel(category.getDynamicCategories().getCategories().get(i));
//                    System.out.println("## category insert -> " +i);
//                }*/
//            }
//        };
//        task.execute();

        openHomeButton();
    }

    public void openHomeButton() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        //Intent intent = new Intent(MainActivity.this, ListingSearchBoxActivity.class);
        //intent.putExtra(HomeActivity.ARG_AS_ROOT, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            openHomeButton();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        IOBus.getInstance().post(new CheckUserSessionEvent());
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IOBus.getInstance().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
