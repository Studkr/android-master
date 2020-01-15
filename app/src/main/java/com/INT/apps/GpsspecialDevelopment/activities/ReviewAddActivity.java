package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperty;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewAddResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewData;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.LoadReviewFormEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewAddRequestEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewAddResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews.ReviewFormLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.views.FormFieldFactory;
import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.FormModel;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.LabeledFieldController;
import com.github.dkharrat.nexusdialog.utils.MessageUtil;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ReviewAddActivity extends BaseActivity {

    public static String ARG_LISTING_ID = "listing_id";
    public static String ARG_LISTING_TITLE = "listing_title";
    private String mListingId;
    private String mListingTitle;
    private FieldsProperties mFieldsProperties;
    private FormController mFormController;
    private ProgressDialog mProgressDialog;
    HashMap<String, FieldsProperty> mFormFieldsCollection = new HashMap<>();
    private boolean loadingForm = false;
    private String mRequestKey = null;
    public static String RESULT_REVIEW_ID = "review_id";
    public static String RESULT_PUBLISHED = "pubished";

    @InjectView(R.id.review_section)
    public View reviewSection;

    @InjectView(R.id.loading)
    public View loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_add);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(R.string.add_review);
        if (UserSession.getUserSession().isLoggedIn() == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
        mListingId = getIntent().getStringExtra(ARG_LISTING_ID);
        mListingTitle = getIntent().getStringExtra(ARG_LISTING_TITLE);
        mFormController = new FormController(this);
        ButterKnife.inject(this);
        reviewSection.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (UserSession.getUserSession().isLoggedIn() == false) {
                setResult(1);
                finish();
            } else {
                if (mFieldsProperties != null) {
                    renderReviewForm();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onRegisterFormLoaded(ReviewFormLoadedEvent event) {
        if (event.getListingId() == mListingId) {
            reviewSection.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.GONE);
            mFieldsProperties = event.getFieldsProperties();
            if (UserSession.getUserSession().isLoggedIn()) {
                renderReviewForm();
            }
        }
    }

    private void renderReviewForm() {
        mFormFieldsCollection = new HashMap<>();
        List<FieldsProperty> fieldsPropertyList = mFieldsProperties.getFieldsProperties();
        FormSectionController section = new FormSectionController(this, "");
        FormFieldFactory.setLabelOrientationConfig(LabeledFieldController.LABEL_ORIENTATION_VERTICAL);
        for (FieldsProperty fieldsProperty : fieldsPropertyList) {
            FormElementController elementController = FormFieldFactory.getField(fieldsProperty, this, false);
            if (elementController != null) {
                section.addElement(elementController);
                mFormFieldsCollection.put(fieldsProperty.getField(), fieldsProperty);
            }
        }
        mFormController.addSection(section);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.review_form);
        mFormController.addFormElementsToView(containerView);
        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        if (mFieldsProperties == null) {
            IOBus.getInstance().post(new LoadReviewFormEvent(mListingId));
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @OnClick(R.id.submit_form)
    public void submitReview() {
        if (mFormController.showValidationErrors() == true) {
            return;
        }
        FormSectionController section = mFormController.getSections().get(0);
        FormModel model = mFormController.getModel();
        HashMap<String, String> reviewData = new HashMap<>();
        for (FormElementController elementController : section.getElements()) {
            Object modelValue = model.getValue(elementController.getName());
            String value = "";
            if (modelValue != null) {
                value = modelValue.toString();
            }
            if (value == null) {
                value = "";
            }
            reviewData.put(elementController.getName(), value);
        }
        ReviewData reviewDateInstance = new ReviewData(reviewData);
        mRequestKey = UUID.randomUUID().toString();
        IOBus.getInstance().post(new ReviewAddRequestEvent(mListingId, reviewDateInstance, mRequestKey));
        mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.sending_request), true);
    }


    @Subscribe
    public void onReviewFormResult(ReviewAddResultEvent event) {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (!event.getRequestKey().equals(mRequestKey)) {
            return;
        }
        ReviewAddResult.ReviewResult reviewAddResult = event.getReviewAddResult().getReviewResult();
        if (reviewAddResult.getResult() == false) {
            if (reviewAddResult.getRedirectBack()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeActivity();
                            }
                        }).setCancelable(true).setTitle(R.string.errors)
                        .setMessage(reviewAddResult.getMessage()).create();
                alertDialog.show();
            } else {
                List<ReviewAddResult.Error> errors = reviewAddResult.getErrors();
                StringBuilder builder = new StringBuilder();
                int errorCount = 0;
                for (ReviewAddResult.Error error : errors) {
                    FieldsProperty fieldsProperty = mFormFieldsCollection.get(error.getField());
                    if (fieldsProperty != null) {
                        builder.append(fieldsProperty.getLabel() + ": " + error.getError() + "\n");
                        errorCount++;
                    } else {
                        builder.append(error.getError() + "\n");
                        errorCount++;
                    }
                }
                if (errorCount > 0) {
                    AlertDialog alertDialog = MessageUtil.showAlertMessage(getResources().getString(R.string.errors), builder.toString(), this);
                }
            }
        } else {
            Toast.makeText(this, reviewAddResult.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra(RESULT_REVIEW_ID, reviewAddResult.getId());
            intent.putExtra(RESULT_PUBLISHED, reviewAddResult.getPublished());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Subscribe
    public void onNetworkError(ApiRequestFailedEvent error) {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        AlertDialog alertDialog = MessageUtil.showAlertMessage(getResources().getString(R.string.errors), getResources().getString(R.string.please_try_again), this);
    }

    private void closeActivity() {
        finish();
    }
}
