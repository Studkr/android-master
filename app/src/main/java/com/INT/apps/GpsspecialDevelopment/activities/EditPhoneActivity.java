package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditProfileEntity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditResult;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnEditResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnLoginResultEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditPhoneActivity extends BaseActivity {

    private static final String PHONE_NUMBER = "phone_number";

    @InjectView(R.id.edit_text) EditText editText;

    public static Intent newInstance(Context context, String phoneNumber) {
        Intent intent = new Intent(context, EditPhoneActivity.class);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);
        ButterKnife.inject(this);
        if (getIntent() != null) {
            String phoneNumber = getIntent().getExtras().getString(PHONE_NUMBER);
            editText.setText(phoneNumber);
        }
    }

    @OnClick(R.id.save)
    public void onSave() {
        String phoneNumber = editText.getText().toString();
        EditProfileEntity data = new EditProfileEntity();
        data.setPhone(phoneNumber);
        IOBus.getInstance().post(data);
    }

    @Subscribe
    public void onEditResult(OnEditResultEvent event) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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
}
