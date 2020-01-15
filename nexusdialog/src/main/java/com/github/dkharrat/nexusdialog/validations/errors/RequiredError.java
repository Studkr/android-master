package com.github.dkharrat.nexusdialog.validations.errors;

import android.content.res.Resources;

import com.github.dkharrat.nexusdialog.R;

/**
 * Created by shrey on 16/6/15.
 */
public class RequiredError extends Error
{
    public RequiredError(String fieldName)
    {
        super(fieldName);
    }
    @Override
    public String getMessage(Resources resources,String fieldLabel)
    {
        return resources.getString(R.string.required_field_error_msg,fieldLabel);
    }
}
