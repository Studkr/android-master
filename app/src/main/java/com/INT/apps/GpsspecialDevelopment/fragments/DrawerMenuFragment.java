package com.INT.apps.GpsspecialDevelopment.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.BaseActivity;
import com.INT.apps.GpsspecialDevelopment.activities.BookMarkedListingsActivity;
import com.INT.apps.GpsspecialDevelopment.activities.HomeActivity;
import com.INT.apps.GpsspecialDevelopment.activities.ListingCheckinActivity;
import com.INT.apps.GpsspecialDevelopment.activities.ListingSearchBoxActivity;
import com.INT.apps.GpsspecialDevelopment.activities.LoginActivity;
import com.INT.apps.GpsspecialDevelopment.activities.UserProfileActivity;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.MerchantActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserServiceCall;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserServiceCallResponce;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserLoggedOutEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserSessionLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DrawerMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrawerMenuFragment extends Fragment {
    @InjectView(R.id.login_button)
    Button loginButton;

    @InjectView(R.id.user_profile_wrapper)
    View userProfileWrapper;

    @InjectView(R.id.user_reviews_count)
    TextView userReviewCount;
    @InjectView(R.id.user_photos_count)
    TextView userPhotoCount;
    @InjectView(R.id.user_avatar1)
    ImageView userAvatarView;

    @InjectView(R.id.drawer_bookmarked_business)
    View viewBookmarkedBusiness;
    @InjectView(R.id.drawer_my_checkins)
    View viewMyChecking;
    @InjectView(R.id.drawer_merchant_account)
    View viewMerchantAccount;
    @InjectView(R.id.dividerBetweenUser)
    View dividerBetweenUserView;

    @InjectView(R.id.drawer_logout_button)
    Button logoutButton;

    HashMap<String, Class> listActivity = new HashMap<>();
    HashMap<String, Integer> listTitle = new HashMap<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrawerMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DrawerMenuFragment newInstance() {
        DrawerMenuFragment fragment = new DrawerMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DrawerMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawer_menu, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.login_button)
    public void openLoginPage() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        ((BaseActivity) getActivity()).closeDrawer();
        startActivity(intent);
    }

    private void setLoginButtons() {
        if (UserSession.getUserSession().isLoggedIn()) {
            User user = UserSession.getUserSession().getSessionUser();
            loginButton.setVisibility(View.GONE);
            userProfileWrapper.setVisibility(View.VISIBLE);

            userReviewCount.setText(getResources().getString(R.string.reviews_count, user.getReviewCount()));
            userPhotoCount.setText(getResources().getString(R.string.photos_count, user.getListingAssetCount()));
            ((TextView) getView().findViewById(R.id.username)).setText(user.getDisplayName());
            String avatarUrl = CvUrls.getImageUrlForSize(user.getAvatar(), 50, 50, true);
            Picasso.get().load(avatarUrl).fit().into(userAvatarView);

            logoutButton.setVisibility(View.VISIBLE);
            viewBookmarkedBusiness.setVisibility(View.VISIBLE);
            viewMyChecking.setVisibility(View.VISIBLE);
            if (user.isMerchantGranted())
                viewMerchantAccount.setVisibility(View.VISIBLE);
            dividerBetweenUserView.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            userProfileWrapper.setVisibility(View.GONE);

            logoutButton.setVisibility(View.GONE);
            viewBookmarkedBusiness.setVisibility(View.GONE);
            viewMyChecking.setVisibility(View.GONE);
            viewMerchantAccount.setVisibility(View.GONE);
            dividerBetweenUserView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        IOBus.getInstance().register(this);
        setLoginButtons();
        super.onResume();
    }

    @Subscribe
    public void onLogin(UserSessionLoadedEvent event) {
        setLoginButtons();
    }

    @Override
    public void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @OnClick(R.id.drawer_open_nearby)
    public void openNearBy() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!getActivity().getClass().equals(HomeActivity.class)) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.drawer_search_business)
    public void openBusinessSearch() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!getActivity().getClass().equals(ListingSearchBoxActivity.class)) {
            Intent intent = new Intent(getActivity(), ListingSearchBoxActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.drawer_logout_button)
    public void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.logout_confirmation);
        builder.setMessage(R.string.logout_confirmation_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final LogoutUserServiceCall logoutUser = new LogoutUserServiceCall(UserSession.getUserSession().getSessionUser());
                        final Double[] lastPosition = LocationDetector.getInstance(getActivity()).getLastLocation();
                        logoutUser.setLastLatitude(lastPosition[0]);
                        logoutUser.setLastLongitude(lastPosition[1]);
                        IOBus.getInstance().post(logoutUser);
                    }
                }).setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    @OnClick(R.id.drawer_bookmarked_business)
    public void openMyBookmarked() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!getActivity().getClass().equals(BookMarkedListingsActivity.class)) {
            Intent intent = new Intent(getActivity(), BookMarkedListingsActivity.class);
            User user = UserSession.getUserSession().getSessionUser();
            if (user == null) {
                ((BaseActivity) getActivity()).openLoginPage();
                return;
            }
            intent.putExtra(BookMarkedListingsActivity.ARG_USER_ID, user.getId());
            intent.putExtra(BookMarkedListingsActivity.ARG_USER_NAME, user.getDisplayName());
            startActivity(intent);
        }
    }

    @OnClick(R.id.drawer_merchant_account)
    public void openMerchantAccountDashboard() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!getActivity().getClass().equals(MerchantActivity.class)) {
            Intent intent = new Intent(getActivity(), MerchantActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.drawer_my_checkins)
    public void openMyCheckins() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!getActivity().getClass().equals(BookMarkedListingsActivity.class)) {
            User user = UserSession.getUserSession().getSessionUser();
            if (user == null) {
                ((BaseActivity) getActivity()).openLoginPage();
                return;
            }
            Intent intent = new Intent(getActivity(), ListingCheckinActivity.class);
            intent.putExtra(ListingCheckinActivity.ARG_USER_ID, user.getId());
            intent.putExtra(ListingCheckinActivity.ARG_USERNAME, user.getDisplayName());
            startActivity(intent);
        }
    }

    @Subscribe
    public void onUserLogoutServiceResponce(LogoutUserServiceCallResponce event) {
        ((BaseActivity) getActivity()).closeDrawer();
        IOBus.getInstance().post(new LogoutUserEvent(false));
    }

    @Subscribe
    public void onUserLogout(UserLoggedOutEvent event) {
        if (event.isAutoLogOut()) {
            CrowdvoxApplication.getAppInstance().restartApp();
        } else {
            Toast.makeText(getActivity(), R.string.logged_out_successfully, Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof HomeActivity)
                setLoginButtons();
            else {
                Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
            }
        }
    }

    @OnClick(R.id.user_profile_wrapper)
    public void openUserProfile() {
        ((BaseActivity) getActivity()).closeDrawer();
        if (!(getActivity() instanceof UserProfileActivity)) {
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(intent);
        }
    }
}
