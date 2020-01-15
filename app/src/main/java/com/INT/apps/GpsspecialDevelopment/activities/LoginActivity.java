package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnLoginResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.SendLoginEvent;
import com.INT.apps.GpsspecialDevelopment.views.decorator.ClearableEditTextDecorator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.user_email)
    EditText mUserEmail;

    @InjectView(R.id.user_password)
    EditText mUserPassword;

    @InjectView(R.id.fb_button)
    LoginButton mLoginButton;

    CallbackManager callbackManager;

    ProgressDialog mProgressDialog;
    static int CODE_LOGIN_RESULT = 10001;
    static int CODE_REQUEST_REGISTER = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(R.string.login);
        new ClearableEditTextDecorator(mUserEmail);
        new ClearableEditTextDecorator(mUserPassword);
        mUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginSubmit();
                }
                return false;
            }
        });
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
                                        Toast.makeText(LoginActivity.this, "email :" + str_email, Toast.LENGTH_SHORT).show();
                                        mUserEmail.setText(str_email);
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

    @OnClick(R.id.submit)
    protected void onLoginSubmit() {
        String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();
        IOBus.getInstance().post(new SendLoginEvent(email, password));
        showLoginDialog();

    }

    private void showLoginDialog() {
        mProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.login_in_message), true);
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onLoginResult(OnLoginResultEvent event) {
        LoginResult.LoginResult_ loginResult = event.getLoginResult().getLoginResult();
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (loginResult.getResult()) {
            Toast.makeText(this, R.string.logged_in_message, Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.login_failed)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            dialog.show();
        }
    }

    @Subscribe
    public void onLoginError(ApiRequestFailedEvent event) {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Problem in login. Please try again")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
            Boolean loggedIn = false;
            loggedIn = data.getBooleanExtra(RegisterActivity.CODE_REGISTER_LOGIN, false);
            if (loggedIn) {
                Toast.makeText(this, R.string.logged_in_message, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.login_after_register)
                        .setTitle(R.string.login).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
            }

        }
    }

    @OnClick(R.id.register_button)
    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, CODE_REQUEST_REGISTER);
    }

    @OnClick(R.id.forgot_password)
    public void forgotPasswordActivity() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }
}
