package com.github.dkharrat.nexusdialog.controllers;

import android.content.Context;
import android.text.InputType;
import android.util.Log;

import com.github.dkharrat.nexusdialog.validations.Validator;
import com.github.dkharrat.nexusdialog.validations.validator.EmailValidator;

import java.util.List;

/**
 * Created by shrey on 17/6/15.
 */
public class EmailController extends EditTextController
{
    public EmailController(Context ctx, String name, String labelText, String placeholder, boolean isRequired)
    {
        super(ctx, name, labelText, placeholder, isRequired, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    @Override
    public List<Validator> getValidationRules()
    {
        List<Validator> rules = super.getValidationRules();
        rules.add(new EmailValidator());
        return rules;
    }
}
