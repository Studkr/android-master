package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.BonusInfoCallback;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses.BonusInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.RegisterResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.BonusesApi;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnUserRegisteredInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.RegisterUserInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadUserProfileEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserProfileLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.stripe.StripeManager;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.INT.apps.GpsspecialDevelopment.views.LinearButtonGroupView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static com.INT.apps.GpsspecialDevelopment.utils.FormatUtils.format;

public class UserProfileActivity extends BaseActivity implements BonusInfoCallback {

    public static final String KEY_BONUS = "bonuses";

    public static String ARG_USER_ID = "user_id";

    private static final int RC_EDIT_PHONE = 303;

    @InjectView(R.id.progress_bar) ProgressBar mProgressBar;
    @InjectView(R.id.last_login_date) TextView mLastLoginDate;
    @InjectView(R.id.registration_date) TextView mRegistrationDate;
    @InjectView(R.id.first_last_name) TextView first_last_name;
    @InjectView(R.id.email) TextView email;
    @InjectView(R.id.user_avatar) ImageView mUserAvatar;
    @InjectView(R.id.count_buttons_group) LinearButtonGroupView mCountsButtons;
    @InjectView(R.id.custom_fields_group) LinearButtonGroupView mCustomFieldsButtons;
    @InjectView(R.id.profile_wrapper) ScrollView mProfileWrapper;

    String mUserId;
    boolean mIsMyProfile = false;
    private UserProfile.User currentUser;
    private ProgressDialog progressDialog;
    private Double moneyAmount;
    private Integer pointsAmount;
    private Integer bonusesPerMoney;
    private boolean isMerchant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUserId = getIntent().getStringExtra(ARG_USER_ID);
        if (mUserId == null && UserSession.getUserSession().isLoggedIn() == false) {
            Toast.makeText(getApplicationContext(), "No User for profile", Toast.LENGTH_SHORT);
            finish();
        }
        setToolbar();
        ButterKnife.inject(this);
        mProfileWrapper.setVisibility(View.GONE);

        BonusesApi.getInstance().getBonusInfo(this);
        mProgressBar.setVisibility(View.VISIBLE);

        User user = UserSession.getUserSession().getSessionUser();
        isMerchant = user.isMerchantGranted();
    }

    @Override
    public void onBonusPointsReceivingError(Throwable t) {
        Timber.e(t);
        loadProfileInfo();
    }

    @Override
    public void onBonusPointsReceived(BonusInfo info) {
        moneyAmount = info.getMoney();
        pointsAmount = info.getBonuses();
        bonusesPerMoney = info.getBonusesPerMoney();
        loadProfileInfo();
    }

    private void loadProfileInfo() {
        IOBus.getInstance().post(new LoadUserProfileEvent(mUserId));
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IOBus.getInstance().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IOBus.getInstance().register(this);
    }

    private final StripeManager.InitializeListener stripeInitializeListener = new StripeManager.InitializeListener() {
        @Override
        public void onInitialized() {
            progressDialog.dismiss();
            progressDialog = null;
            displayPaymentsActivity();
        }

        @Override
        public void onError(String msg) {
            progressDialog.dismiss();
            progressDialog = null;
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void initStripe(final String stripeCustomerAccount) {
        final StripeManager stripeManager = StripeManager.getInstance(getApplicationContext());
        stripeManager.setInitializeListener(stripeInitializeListener);
        stripeManager.initCustomer(stripeCustomerAccount);
    }

    private void showPaymentsScreen() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.init_stripe));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (!TextUtils.isEmpty(currentUser.getStripeAccount())) {
            initStripe(currentUser.getStripeAccount());
        } else {
            IOBus.getInstance().post(new RegisterUserInStripeEvent(currentUser.getId()));
        }
    }

    private void displayPaymentsActivity() {
        Intent payIntent = StripePaymentMethodsActivity.newIntent(UserProfileActivity.this);
        startActivity(payIntent);
    }

    @Subscribe
    public void onUserRegisteredInStripe(OnUserRegisteredInStripeEvent event) {
        final RegisterResult registerResult = event.getRegisterResult();
        if (registerResult != null) {
            currentUser.setStripeAccount(registerResult.stripe_account);
            initStripe(registerResult.stripe_account);
        } else {
            progressDialog.dismiss();
            progressDialog = null;
            Toast.makeText(getApplicationContext(), R.string.error_register_stripe, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onProfileInfoLoaded(UserProfileLoadedEvent event) {
        currentUser = event.getUser();

        mProfileWrapper.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        email.setText(currentUser.getEmail());
        first_last_name.setText(currentUser.getFirst_name() + " " + currentUser.getLast_name());

        if (UserSession.getUserSession().isLoggedIn() && UserSession.getUserSession().getSessionUser().getId().equals(currentUser.getId())) {
            getToolbar().setTitle(getString(R.string.my_profile));
            mIsMyProfile = true;
        } else {
            getToolbar().setTitle(getString(R.string.user_profile, currentUser.getUsername()));
            mIsMyProfile = false;
        }
        Date lastLoginDate = DateParser.fromStringToDate(currentUser.getLastLoginDate());
        if (lastLoginDate != null) {
            DateFormat df = new SimpleDateFormat(getString(R.string.simple_date_format));
            String dateString = df.format(lastLoginDate);
            //"regdate"
            mLastLoginDate.setText(" " + dateString);
            findViewById(R.id.last_login_date_row).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.last_login_date_row).setVisibility(View.GONE);
        }
        Date registerDate = DateParser.fromStringToDate(currentUser.getCreated());
        if (registerDate != null) {
            DateFormat df = new SimpleDateFormat(getString(R.string.simple_date_format));
            String dateString = df.format(registerDate);
            mRegistrationDate.setText(dateString);
            findViewById(R.id.registration_date_row).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.registration_date_row).setVisibility(View.GONE);
        }
        Picasso.get().load(CvUrls.getImageUrlForSize(currentUser.getAvatar(), 100, 100, true)).fit().into(mUserAvatar);
        final LinkedHashMap<String, Integer> countButtons = new LinkedHashMap<>();
        countButtons.put("review_count", currentUser.getReviewCount());
        countButtons.put("fav_listing_count", currentUser.getFavListingCount());
        countButtons.put("checkin_count", currentUser.getCheckinCount());
        countButtons.put("purchased_deal_count", currentUser.getPurchasedDealCount());

        LinkedHashMap<String, String> labels = new LinkedHashMap<>();
        labels.put("review_count", getString(R.string.review_count_label));
        labels.put("fav_listing_count", getString(R.string.bookmarked_business));
        labels.put("checkin_count", getString(R.string.checkin_count));
        if (mIsMyProfile) {
            labels.put("purchased_deal_count", getString(R.string.deals_purchased));
        }
        mCountsButtons.setListAdapter(new CountArrayAdapter(this, countButtons, labels));
        mCountsButtons.setOnItemClickListner(new LinearButtonGroupView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = ((CountArrayAdapter) mCountsButtons.getListAdapter()).getItem(position);
                Integer count = countButtons.get(item);
                if (count > 0) {
                    if (item.equals("review_count")) {
                        Intent intent = new Intent(UserProfileActivity.this, UserReviewPaginationActivity.class);
                        intent.putExtra(UserReviewPaginationActivity.ARG_USER_ID, currentUser.getId());
                        intent.putExtra(UserReviewPaginationActivity.ARG_USERNAME, currentUser.getUsername());
                        startActivity(intent);
                        return;
                    } else if (item.equals("fav_listing_count")) {
                        Intent intent = new Intent(UserProfileActivity.this, BookMarkedListingsActivity.class);
                        intent.putExtra(BookMarkedListingsActivity.ARG_USER_ID, currentUser.getId());
                        intent.putExtra(BookMarkedListingsActivity.ARG_USER_NAME, currentUser.getUsername());
                        startActivity(intent);
                        return;
                    } else if (item.equals("checkin_count")) {
                        Intent intent = new Intent(UserProfileActivity.this, ListingCheckinActivity.class);
                        intent.putExtra(ListingCheckinActivity.ARG_USER_ID, currentUser.getId());
                        intent.putExtra(ListingCheckinActivity.ARG_USERNAME, currentUser.getUsername());
                        startActivity(intent);
                    } else if (item.equals("purchased_deal_count") && UserSession.getUserSession().isLoggedIn()) {
                        Intent intent = new Intent(UserProfileActivity.this, MyPurchasedDealsActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        showCustomFields(currentUser);
    }

    private void showCustomFields(final UserProfile.User user) {
        LinkedHashMap<String, String> labels = new LinkedHashMap<>();
        LinkedHashMap<String, String> values = new LinkedHashMap<>();

        //Phone
        if (!TextUtils.isEmpty(user.getPhone())) {
            labels.put("phone", getString(R.string.phone));
            values.put("phone", user.getPhone());
        }
        //Payments
        labels.put("payments", getString(R.string.payments));
        if (!isMerchant) {
            labels.put(KEY_BONUS, getString(R.string.profile_bonus_points_label));
            String bonusValue = getString(R.string.profile_bonus_points_amount_unknown);

            if (moneyAmount != null && pointsAmount != null) {
                bonusValue = getString(R.string.bonus_points_format, format(pointsAmount), format(moneyAmount));
            }
            values.put(KEY_BONUS, bonusValue);
        }

        CountArrayAdapter countArrayAdapter = new CountArrayAdapter(this, values, labels);
        mCustomFieldsButtons.setListAdapter(countArrayAdapter);
        mCustomFieldsButtons.setOnItemClickListner(new LinearButtonGroupView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = ((CountArrayAdapter) mCustomFieldsButtons.getListAdapter()).getItem(position);
                if (item == null) return;
                if (item.equals("payments") && UserSession.getUserSession().isLoggedIn()) {
                    showPaymentsScreen();
                } else if (item.equals(KEY_BONUS) && pointsAmount != null && bonusesPerMoney != null && moneyAmount != null) {
                    Intent intent = BonusPointsActivity.getIntent(
                            mCountsButtons.getContext(),
                            pointsAmount,
                            moneyAmount,
                            bonusesPerMoney
                    );

                    startActivity(intent);
                } else {
                    startActivityForResult(EditPhoneActivity.newInstance(UserProfileActivity.this, user.getPhone()), RC_EDIT_PHONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_EDIT_PHONE) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected boolean useNavigationDrawer() {
        return super.useNavigationDrawer();
    }
}

class CountArrayAdapter extends ArrayAdapter<String> {
    LinkedHashMap<String, ? extends Object> mCountButtons;
    LinkedHashMap<String, String> mLabels;
    boolean mShowNextButton = true;

    CountArrayAdapter(Context context, LinkedHashMap<String, ? extends Object> countButtons, LinkedHashMap<String, String> labels) {
        super(context, 0, new ArrayList<String>());
        for (String field : labels.keySet()) {
            add(field);
        }
        mLabels = labels;
        mCountButtons = countButtons;
    }

    public void hideNextButton() {
        mShowNextButton = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String field = getItem(position);
        String label = mLabels.get(field);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_counts_button, parent, false);
        ((TextView) view.findViewById(R.id.button_label)).setText(label);

        if (mCountButtons.containsKey(field)) {
            ((TextView) view.findViewById(R.id.button_count)).setText(mCountButtons.get(field).toString());
            if (!mShowNextButton && (field == null || !field.equals(UserProfileActivity.KEY_BONUS)))
                view.findViewById(R.id.next_item).setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
