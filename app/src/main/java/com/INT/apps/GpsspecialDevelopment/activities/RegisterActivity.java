package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperty;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterData;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterResult;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadRegisterFormEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnRegisterResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RegisterFormLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RequestRegisterEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.SetUserLoginEvent;
import com.INT.apps.GpsspecialDevelopment.views.FormFieldFactory;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.FormModel;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.utils.MessageUtil;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import timber.log.Timber;

public class RegisterActivity extends BaseActivity {

    private static final String PHONE_FIELD = "phone";
    private static final int PHONE_NUMBER_MAX_LENGTH = 15;

    FormController mFormController;
    HashMap<String, FieldsProperty> mFormFieldsCollection = new HashMap<>();
    ProgressDialog mProgressDialog;
    static String CODE_REGISTER_LOGIN = "register_login";

    @InjectView(R.id.fb_register_button)
    LoginButton mLoginButton;
    CallbackManager callbackManager;
    FormSectionController section;

    private CountryCodePicker countryCodePicker;
    private AppCompatEditText phoneInputView;
    private PhoneNumberUtil phoneNumberUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phoneNumberUtil = PhoneNumberUtil.createInstance(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mFormController = new FormController(this);
        IOBus.getInstance().post(new LoadRegisterFormEvent());
        ButterKnife.inject(this);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(R.string.register);

        mLoginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {
                //String token = loginResult.getAccessToken().getToken();
                //showLoginDialog();
                //@saqib
                /**
                 * Disable IOBus post for facebook
                 * */
                //IOBus.getInstance().post(new SendFbLoginEvent(token));
                Timber.d("##Success");
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    Timber.d("ERROR");
                                } else {
                                    Timber.d("Success");
                                    try {
                                        String jsonresult = String.valueOf(json);
                                        Timber.d("JSON Result %s", jsonresult);
                                        String str_email = json.getString("email");
                                        Toast.makeText(RegisterActivity.this, "email :" + str_email, Toast.LENGTH_SHORT).show();

                                        //  section.getModel().setValue("email", str_email);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Subscribe
    public void onRegisterFormLoaded(RegisterFormLoadedEvent event) {

        mFormFieldsCollection = new HashMap<>();
        FieldsProperties fieldsProperties = event.getFieldsProperties();
        Timber.d("----** size : %d", fieldsProperties.getFieldsProperties().size());
        List<FieldsProperty> fieldsPropertyList = fieldsProperties.getFieldsProperties();
        section = new FormSectionController(this, "");
        for (FieldsProperty fieldsProperty : fieldsPropertyList) {
            Timber.d("----** field : %s", fieldsProperty.getField());
            FormElementController elementController = FormFieldFactory.getField(fieldsProperty, this);
            section.addElement(elementController);
            mFormFieldsCollection.put(fieldsProperty.getField(), fieldsProperty);
        }
        mFormController.addSection(section);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.register_form);
        mFormController.addFormElementsToView(containerView);
        //section.getModel().setValue("email", "demo@intotality.co");

        if (section.getElement(PHONE_FIELD) != null && section.getElement(PHONE_FIELD).getView() != null) {
            section.getModel().setValue(PHONE_FIELD, "+");
            FrameLayout phoneValueContainer = section.getElement(PHONE_FIELD).getView().findViewById(R.id.field_container);
            phoneValueContainer.removeAllViews();
            View phoneView = LayoutInflater.from(this).inflate(R.layout.view_input_phone_number, phoneValueContainer, true);
            countryCodePicker = phoneView.findViewById(R.id.codePicker);
            phoneInputView = phoneView.findViewById(R.id.phoneNumberInput);

            countryCodePicker.setDefaultCountryUsingNameCode(Locale.getDefault().getCountry());
            countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected(Country country) {
                    setMaxLengthForNumberDependsCountry(country.getIso().toUpperCase());
                }
            });
            setMaxLengthForNumberDependsCountry(countryCodePicker.getSelectedCountryNameCode());
        }
    }

    private void setMaxLengthForNumberDependsCountry(final String country) {
        int maxLength;
        Phonenumber.PhoneNumber exampleNumber = phoneNumberUtil.getExampleNumber(country);
        if (exampleNumber != null) {
            maxLength = String.valueOf(exampleNumber.getNationalNumber()).length();
        } else maxLength = 15;

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(PHONE_NUMBER_MAX_LENGTH);
        phoneInputView.setFilters(fArray);
    }

    private void displayErrorAboutInvalidPhone() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.validity_phone));

        Phonenumber.PhoneNumber exampleNumber = phoneNumberUtil.getExampleNumber(countryCodePicker.getSelectedCountryNameCode());
        if (exampleNumber != null) {
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.for_example)).append("\n");
            stringBuilder.append(phoneNumberUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
        }

        MessageUtil.showAlertMessage(getString(com.github.dkharrat.nexusdialog.R.string.validation_error_title), stringBuilder.toString(), this);
    }

    @OnClick(R.id.submit_register)
    public void submitRegistration() {
        FormSectionController section = mFormController.getSections().get(0);
        FormModel model = mFormController.getModel();

        if (countryCodePicker != null && phoneInputView != null) {
            model.setValue(PHONE_FIELD, phoneInputView.getText().toString());
        }
        if (mFormController.showValidationErrors()) {
            return;
        } else if (countryCodePicker != null && phoneInputView != null) {
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneInputView.getText().toString(), countryCodePicker.getSelectedCountryNameCode());
                model.setValue(PHONE_FIELD, phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
            } catch (NumberParseException e) {
                displayErrorAboutInvalidPhone();
                return;
            }
        }
        HashMap<String, String> registerData = new HashMap<>();
        for (FormElementController elementController : section.getElements()) {
            Object valueModel = model.getValue(elementController.getName());
            String value = valueModel == null ? "" : valueModel.toString();
            registerData.put(elementController.getName(), value);
            Timber.d("## Name : %s", elementController.getName());
            Timber.d("## Value : %s", value);
        }
        IOBus.getInstance().post(new RequestRegisterEvent(new RegisterData(registerData)));
        mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.sending_request), true);
    }

    @Subscribe
    public void onRegisterResult(OnRegisterResultEvent event) {
        RegisterResult registerResult = event.getRegisterResult();
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (registerResult.isConnectionError()) {
            Toast.makeText(this, R.string.connectivity_failure, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.check_your_connection, Toast.LENGTH_LONG).show();
            return;
        }
        if (!registerResult.getRegisterResult().getResult()) {
            List<RegisterResult.Error> errors = registerResult.getRegisterResult().getErrors();
            StringBuilder builder = new StringBuilder();
            int errorCount = 0;
            for (RegisterResult.Error error : errors) {
                FieldsProperty fieldsProperty = mFormFieldsCollection.get(error.getField());
                if (fieldsProperty != null) {
                    builder.append(fieldsProperty.getLabel() + ": " + error.getError() + "\n");
                    errorCount++;
                }
            }
            if (errorCount > 0) {
                AlertDialog alertDialog = MessageUtil.showAlertMessage(getResources().getString(R.string.errors), builder.toString(), this);
            }
        } else {
            registerSuccess(event.getRegisterResult());
        }
    }

    private void registerSuccess(RegisterResult registerResult) {
        Intent intent = new Intent();
        if (registerResult.getRegisterResult().getLoginResult() != null) {
            LoginResult loginResult = new LoginResult(registerResult.getRegisterResult().getLoginResult());
            IOBus.getInstance().post(new SetUserLoginEvent(loginResult));
            IOBus.getInstance().post(new SetUserLoginEvent(loginResult));
            intent.putExtra(CODE_REGISTER_LOGIN, true);
        } else {
            intent.putExtra(CODE_REGISTER_LOGIN, false);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
