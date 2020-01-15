package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.BonusInfoCallback;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses.BonusInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.Deal;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.Order;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PayResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.RegisterResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;
import com.INT.apps.GpsspecialDevelopment.fragments.BonusPayDialogFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.OnBonusPointsPayListenter;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingRowViewHolderDealsMarker;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.BonusesApi;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadInfoOrderEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnRequestToPayResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OrderInfoLoadError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OrderInformationLoaded;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.RequestToPayEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.OnUserRegisteredInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe.RegisterUserInStripeEvent;
import com.INT.apps.GpsspecialDevelopment.io.stripe.StripeManager;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.CustomerSource;
import com.stripe.android.model.Source;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.INT.apps.GpsspecialDevelopment.utils.FormatUtils.format;

/**
 * @author Michael Soyma (Created on 9/11/2017).
 * Company: Thinkmobiles
 * Email:  michael.soyma@thinkmobiles.com
 */
public final class BuyDealActivity extends BaseActivity implements OnBonusPointsPayListenter, BonusInfoCallback {

    private static final String KEY_DEAL = "key_deal";
    public static final String KEY_ARG_DEAL_ID = "arg_deal_id";
    public static final int RC_AUTH = 1009;
    public static final int RC_SELECT_CARD_FOR_PAYMENT = 1010;

    private String mCurrentDealId;
    private Deal.Deal_ mCurrentDeal;
    private Listing_ mCurrentListing;
    private double mTax;
    private double mFee;
    private int mQuantity, mQuantityTotal;

    private UserProfile.User mCurrentUser;
    private ProgressDialog progressDialog;
    private Double convertedMoneyFromPoints;
    private int availablePoints;
    private double bonusMoneyAmount = 0;
    private double moneyToBonusFactor = 0;

    private final ListingRowViewHolderDealsMarker viewHolderDeal = new ListingRowViewHolderDealsMarker();

    @InjectView(R.id.llContentContainer_ABDL) protected View containerView;
    @InjectView(R.id.pbProgress_ABD) protected View progressView;
    @InjectView(R.id.viewDealHeader_ABD) protected View headerDealView;
    @InjectView(R.id.tvQuantityValue_ABD) protected TextView quantityView;
    @InjectView(R.id.tvActionDownQuantity_ABD) protected TextView downQuantityView;
    @InjectView(R.id.tvActionUpQuantity_ABD) protected TextView upQuantityView;
    @InjectView(R.id.tvSubtotalValue_ABD) protected TextView subtotalValueView;
    @InjectView(R.id.tvTaxLabel_ABD) protected TextView taxLabelView;
    @InjectView(R.id.tvTaxValue_ABD) protected TextView taxValueView;
    @InjectView(R.id.tvFeeLabel_ABD) protected TextView feeLabelView;
    @InjectView(R.id.tvFeeValue_ABD) protected TextView feeValueView;
    @InjectView(R.id.tvTotalValue_ABD) protected TextView totalValueView;
    @InjectView(R.id.btnPayBonus) Button btnPayBonus;
    @InjectView(R.id.rlPayWithBonus) RelativeLayout rlPayWithBonus;
    @InjectView(R.id.tvAvailableBonuses) TextView tvAvailableBonuses;
    @InjectView(R.id.tvBonusPointsAmount) TextView tvBonusPointsAmount;
    @InjectView(R.id.llBonusPoints) LinearLayout llBonusPoints;

    private void readArgs() {
        if (getIntent() != null && getIntent().hasExtra(KEY_ARG_DEAL_ID)) {
            mCurrentDealId = getIntent().getStringExtra(KEY_ARG_DEAL_ID);
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArgs();
        setContentView(R.layout.activity_buy_deal);
        ButterKnife.inject(this);

        setToolbar(true);
        getToolbar().setTitle(R.string.buy_deal);

        if (savedInstanceState != null) {
            mCurrentDeal = (Deal.Deal_) savedInstanceState.getSerializable(KEY_DEAL);
        }

        if (!UserSession.getUserSession().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, RC_AUTH);
        } else {
            progressView.setVisibility(View.VISIBLE);
            User user = UserSession.getUserSession().getSessionUser();
            if (user.isMerchantGranted()) {
                loadInfoForOrder();
            } else {
                BonusesApi.getInstance().getBonusInfo(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_DEAL, mCurrentDeal);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.btnPayBonus)
    void onPayBonusClicked() {
        if (mCurrentDeal != null) {
            double price = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
            double maxAmount = price > convertedMoneyFromPoints ? convertedMoneyFromPoints : price;
            BonusPayDialogFragment dialog = BonusPayDialogFragment.getInstance(availablePoints, convertedMoneyFromPoints, maxAmount);
            dialog.show(getSupportFragmentManager(), "bonusPayDialog");
        }
    }

    @Override
    public void onBonusPointsReceivingError(Throwable t) {
        rlPayWithBonus.setVisibility(View.GONE);
        loadInfoForOrder();
    }

    @Override
    public void onBonusPointsReceived(BonusInfo info) {
        convertedMoneyFromPoints = info.getMoney();
        availablePoints = info.getBonuses();
        moneyToBonusFactor = info.getMoneyPerBonuses();
        try {
            String convertedMoneyTitle = format(convertedMoneyFromPoints);
            String availablePoinsTilte = format(availablePoints);
            String availablePointsTitle = getString(R.string.pay_deal_available_bonus_points, convertedMoneyTitle, availablePoinsTilte);
            tvAvailableBonuses.setText(availablePointsTitle);
            rlPayWithBonus.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadInfoForOrder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_AUTH:
                if (UserSession.getUserSession().isLoggedIn())
                    loadInfoForOrder();
                else finish();
                break;
            case RC_SELECT_CARD_FOR_PAYMENT:
                if (resultCode == RESULT_OK) {
                    String selectedSource = data.getStringExtra(StripePaymentMethodsActivity.EXTRA_SELECTED_PAYMENT);
                    CustomerSource source = CustomerSource.fromString(selectedSource);
                    if (source != null && source.getSourceType().equalsIgnoreCase(Source.CARD)) {
                        sendRequestToPayEvent(source.getId());
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendRequestToPayEvent(final String cardId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.pay_progress));
        progressDialog.setCancelable(false);
        progressDialog.show();

        int points = 0;
        if (moneyToBonusFactor != 0) {
            double finalPrice = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
            double bonusSubtotalMoney = getBonusMoney(finalPrice);
            BigDecimal moneyPerBonus = new BigDecimal(moneyToBonusFactor);
            BigDecimal discountMoney = new BigDecimal(bonusSubtotalMoney).setScale(2, BigDecimal.ROUND_CEILING);
            Timber.i("discount money: %s", discountMoney.floatValue());
            BigDecimal roundedPoints = discountMoney.divide(moneyPerBonus, BigDecimal.ROUND_CEILING);
            points = roundedPoints.setScale(0, BigDecimal.ROUND_CEILING).intValue();
            Timber.i("points: %d", points);
        }

        final RequestToPayEvent requestToPayEvent = new RequestToPayEvent();
        requestToPayEvent.setCardId(cardId);
        requestToPayEvent.setDealId(Integer.parseInt(mCurrentDeal.getId()));
        requestToPayEvent.setAmount(calculateFinalPrice());
        requestToPayEvent.setCurrency(mCurrentDeal.getCurrency());
        requestToPayEvent.setCurrencySymbol(mCurrentDeal.getCurrencySymbol());
        requestToPayEvent.setQuantity(mQuantity);
        requestToPayEvent.setTax(mTax);
        requestToPayEvent.setPoints(points);

        IOBus.getInstance().post(requestToPayEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IOBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IOBus.getInstance().unregister(this);
    }

    private void loadInfoForOrder() {
        progressView.setVisibility(View.VISIBLE);
        final Double[] lastLocation = LocationDetector.getInstance(this).getLastLocation();
        IOBus.getInstance().post(new LoadInfoOrderEvent(mCurrentDealId, lastLocation[0], lastLocation[1]));
    }

    @Subscribe
    public void infoForOrderLoaded(final OnRequestToPayResult requestToPayResult) {
        progressDialog.dismiss();
        progressDialog = null;

        boolean isFinish = false;
        String resultMsg;
        if (requestToPayResult.isSuccess()) {
            final PayResult payResult = requestToPayResult.getPayResult();
            if (payResult.status.equalsIgnoreCase("succeeded")) {
                resultMsg = getString(R.string.success_pay);
                isFinish = true;
            } else resultMsg = payResult.status;
        } else resultMsg = requestToPayResult.getErrorMsg();

        Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show();
        if (isFinish) finish();
    }

    @Subscribe
    public void infoForOrderLoaded(final OrderInformationLoaded orderInformationLoaded) {
        final Order order = orderInformationLoaded.getOrder();
        mCurrentDeal = order.getDeal();
        mCurrentListing = order.getListing();
        mTax = order.getTax();
        mFee = order.getFee();
        mCurrentUser = order.getUser();

        mQuantityTotal = Integer.parseInt(mCurrentDeal.getTotalQuantity());
        mQuantity = mQuantityTotal > 0 ? 1 : 0;

        progressView.setVisibility(View.GONE);
        initUI();
    }

    @Subscribe
    public void infoForOrderLoaded(final OrderInfoLoadError errorEvent) {
        progressView.setVisibility(View.GONE);
        Toast.makeText(this, errorEvent.getMsg(), Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.tvActionDownQuantity_ABD)
    protected void downQuantity() {
        if (mQuantity == 0) return;
        mQuantity = Math.max(mQuantity - 1, 1);
        checkQuantityState();
        calculatePrices();
        initTaxUI();
        initFeeUI();
        quantityView.setText(String.valueOf(mQuantity));
    }

    @OnClick(R.id.tvActionUpQuantity_ABD)
    protected void upQuantity() {
        if (mQuantity == 0) return;
        mQuantity = Math.min(mQuantity + 1, mQuantityTotal);
        checkQuantityState();
        calculatePrices();
        initTaxUI();
        initFeeUI();
        quantityView.setText(String.valueOf(mQuantity));
    }

    @OnClick(R.id.tvProceedToPay_ABDL)
    protected void proceedToPay() {
        final double finalPrice = calculateFinalPrice();
        if (finalPrice > 0d) {
            showPaymentsScreen();
        }
    }

    private void checkQuantityState() {
        downQuantityView.setTextColor(ContextCompat.getColor(this,
                mQuantity == 1 ?
                        R.color.taupe_black_invisible :
                        R.color.theme_color));
        upQuantityView.setTextColor(ContextCompat.getColor(this,
                mQuantity == mQuantityTotal ?
                        R.color.taupe_black_invisible :
                        R.color.theme_color));
    }

    private void initUI() {
        containerView.setVisibility(View.VISIBLE);
        viewHolderDeal.setViewContent(this, mCurrentListing, headerDealView, "LIST_TYPE");
        quantityView.setText(String.valueOf(mQuantity));
        checkQuantityState();
        calculatePrices();
        initTaxUI();
        initFeeUI();
    }

    private void initTaxUI() {
        final double finalPrice = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
        String tax = String.valueOf(mTax);
        String taxTilte = getString(R.string.tax_formatted_title, tax);
        taxLabelView.setText(taxTilte);
        taxValueView.setText(String.format("%s", format((mTax / 100) * finalPrice)));
    }

    private void initFeeUI() {
        final double finalPrice = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
        String fee = String.valueOf(mFee);
        String feeTitle = getString(R.string.fee_format, fee);
        feeLabelView.setText(feeTitle);
        feeValueView.setText(String.format("%s", format((mFee / 100) * finalPrice)));
    }

    private void calculatePrices() {
        final double finalPriceWithoutTax = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
        subtotalValueView.setText(String.format("%s", format(finalPriceWithoutTax)));
        totalValueView.setText(String.format("%s", format(calculateFinalPrice())));
    }

    private double calculateFinalPrice() {
        if (mCurrentDeal == null) return 0;
        final double finalPrice = mQuantity * Double.parseDouble(mCurrentDeal.getFinalPrice());
        double finalDealPrice = (finalPrice + finalPrice * (mTax / 100) + finalPrice * (mFee / 100));
        double bonusSubtotalMoney = bonusMoneyAmount > finalPrice ? finalPrice : bonusMoneyAmount;
        setBonusDiscountTitle(bonusSubtotalMoney);
        return finalDealPrice - bonusSubtotalMoney;
    }

    private double getBonusMoney(double finalPrice) {
        return bonusMoneyAmount > finalPrice ? finalPrice : bonusMoneyAmount;
    }

    ////////////////////////////////////////////////// STRIPE

    @Subscribe
    public void onUserRegisteredInStripe(OnUserRegisteredInStripeEvent event) {
        Timber.i("user registered in stripe");
        final RegisterResult registerResult = event.getRegisterResult();
        if (registerResult != null) {
            mCurrentUser.setStripeAccount(registerResult.stripe_account);
            initStripe();
        } else {
            progressDialog.dismiss();
            progressDialog = null;
            Toast.makeText(getApplicationContext(), R.string.error_register_stripe, Toast.LENGTH_SHORT).show();
        }
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

    private void initStripe() {
        final StripeManager stripeManager = StripeManager.getInstance(getApplicationContext());
        stripeManager.setInitializeListener(stripeInitializeListener);
        stripeManager.initCustomer(mCurrentUser.getStripeAccount());
    }

    private void showPaymentsScreen() {
        Timber.i("showPaymentsScreen");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.init_stripe));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (!TextUtils.isEmpty(mCurrentUser.getStripeAccount())) {
            initStripe();
        } else {
            final User currentUser = UserSession.getUserSession().getSessionUser();
            IOBus.getInstance().post(new RegisterUserInStripeEvent(currentUser.getId()));
        }
    }

    private void displayPaymentsActivity() {
        Timber.i("display payments activity");
        Intent payIntent = StripePaymentMethodsActivity.newIntent(this);
        startActivityForResult(payIntent, RC_SELECT_CARD_FOR_PAYMENT);
    }

    @Override
    public void onBonusPay(double bonusPointsMoney) {
        bonusMoneyAmount = bonusPointsMoney;
        if (bonusPointsMoney > 0) {
            Timber.d("bonusMoneyAmount = %s", bonusPointsMoney);
            llBonusPoints.setVisibility(View.VISIBLE);
            setBonusDiscountTitle(bonusPointsMoney);
        } else {
            llBonusPoints.setVisibility(View.GONE);
        }
        calculatePrices();
    }

    private void setBonusDiscountTitle(double bonusPointsMoney) {
        String bonusMoneyTitle = format(bonusPointsMoney);
        String moneyDiscount = getString(R.string.pay_deal_bonus_points_amount, bonusMoneyTitle);
        tvBonusPointsAmount.setText(moneyDiscount);
    }
}
