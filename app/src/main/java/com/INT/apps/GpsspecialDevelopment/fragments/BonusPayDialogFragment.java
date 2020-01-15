package com.INT.apps.GpsspecialDevelopment.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.utils.DecimalDigitsInputFilter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static com.INT.apps.GpsspecialDevelopment.utils.FormatUtils.format;

public class BonusPayDialogFragment extends DialogFragment {

    private static final String ARG_AVAILABLE_BONUSES_AMOUNT = "arg_bonuses_amount";
    private static final String ARG_AVAILABLE_CONVERTED_MONEY = "arg_converted_money_amount";
    private static final String ARG_MAX_AMOUNT = "arg_max_amount";
    private static final String CURRENCY_SIGN = "$";

    public static BonusPayDialogFragment getInstance(int availableBonusPoints, double convertedMoney, double maxAmount) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AVAILABLE_BONUSES_AMOUNT, availableBonusPoints);
        bundle.putDouble(ARG_AVAILABLE_CONVERTED_MONEY, convertedMoney);
        bundle.putDouble(ARG_MAX_AMOUNT, maxAmount);
        BonusPayDialogFragment fragment = new BonusPayDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @InjectView(R.id.tvAvailable) TextView tvAvailableBonuses;
    @InjectView(R.id.edtBonusesAmount) EditText edtBonusesAmount;

    private OnBonusPointsPayListenter onPayListener;


    public BonusPayDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPayListener = (OnBonusPointsPayListenter) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBonusPayListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pay_with_bonus, null);
        ButterKnife.inject(this, view);

        Bundle args = getArguments();
        int bonuses = args.getInt(ARG_AVAILABLE_BONUSES_AMOUNT);
        double money = args.getDouble(ARG_AVAILABLE_CONVERTED_MONEY);
        double maxAmount = args.getDouble(ARG_MAX_AMOUNT);

        String availableBonusesTitle = getString(R.string.dialog_bonus_pay_available_points, format(money), format(bonuses));
        tvAvailableBonuses.setText(availableBonusesTitle);
        edtBonusesAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtBonusesAmount.setCursorVisible(true);
            }
        });

        edtBonusesAmount.setText(CURRENCY_SIGN);
        Selection.setSelection(edtBonusesAmount.getText(), edtBonusesAmount.getText().length());
        edtBonusesAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.i("onTextChanged: %s", s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                Timber.i("before %s", s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Timber.i("after: %s", s.toString());
                if (!s.toString().startsWith(CURRENCY_SIGN)) {
                    edtBonusesAmount.setText(CURRENCY_SIGN);
                    Selection.setSelection(edtBonusesAmount.getText(), edtBonusesAmount.getText().length());
                }
            }
        });

        edtBonusesAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(maxAmount, false, true)});

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String raw = edtBonusesAmount.getEditableText().toString();
                        try {
                            setPayBonusesMoney(raw);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void setPayBonusesMoney(String raw) {
        String moneyAmount = raw.replace(CURRENCY_SIGN, "");
        double input = Double.parseDouble(moneyAmount);
        onPayListener.onBonusPay(input);
    }
}
