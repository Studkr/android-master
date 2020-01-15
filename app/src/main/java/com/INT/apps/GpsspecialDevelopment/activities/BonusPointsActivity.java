package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.INT.apps.GpsspecialDevelopment.utils.FormatUtils.format;

public class BonusPointsActivity extends BaseActivity {

    private static final String EXTRA_BONUS_AMOUNT = "extra_bonus_amount";
    private static final String EXTRA_CONVERTED_MONEY_AMOUNT = "extra_money_amount";
    private static final String EXTRA_CONVERSION_FACTOR = "extra_conversion_factor";
    private static final int EMPTY_VALUE = -1;

    public static Intent getIntent(Context context, int bonusAmount, double convertedMoneyAmount, int conversionFactor) {
        Intent intent = new Intent(context, BonusPointsActivity.class);
        intent.putExtra(EXTRA_BONUS_AMOUNT, bonusAmount);
        intent.putExtra(EXTRA_CONVERTED_MONEY_AMOUNT, convertedMoneyAmount);
        intent.putExtra(EXTRA_CONVERSION_FACTOR, conversionFactor);
        return intent;
    }

    @InjectView(R.id.screen_toolbar) Toolbar toolbar;
    @InjectView(R.id.bonusPointsAmount) TextView tvPointsAmount;
    @InjectView(R.id.bonusPointsConversion) TextView tvPointsConversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_points);
        ButterKnife.inject(this);
        setupToolbar();

        Intent intent = getIntent();
        int bonusAmount = intent.getIntExtra(EXTRA_BONUS_AMOUNT, EMPTY_VALUE);
        int bonusConversion = intent.getIntExtra(EXTRA_CONVERSION_FACTOR, EMPTY_VALUE);
        double convertedMoneyAmount = intent.getDoubleExtra(EXTRA_CONVERTED_MONEY_AMOUNT, EMPTY_VALUE);
        String bonusAmountTitle = getString(R.string.bonus_points_format, format(bonusAmount), format
                (convertedMoneyAmount));
        String conversionTitle = getString(R.string.bonus_points_exchange, format(bonusConversion));

        tvPointsAmount.setText(bonusAmountTitle);
        tvPointsConversion.setText(conversionTitle);
    }

    private void setupToolbar() {
        toolbar.setTitle(R.string.bonus_points_activity_title);
        toolbar.setTitleTextAppearance(this, R.style.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }
}
