package com.github.dkharrat.nexusdialog.validations.validator;

import android.content.res.Resources;

import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.R;
import com.github.dkharrat.nexusdialog.validations.Validator;
import com.github.dkharrat.nexusdialog.validations.errors.*;
import com.github.dkharrat.nexusdialog.validations.errors.Error;

/**
 * Created by shrey on 17/6/15.
 */
public class EmailValidator implements Validator
{
    @Override
    public com.github.dkharrat.nexusdialog.validations.errors.Error getValidationError(FormElementController fieldController, Object value)
    {
        return new Error(fieldController.getName())
        {
            @Override
            public String getMessage(Resources resources, String fieldLabel)
            {
                return resources.getString(R.string.valid_email);
            }
        };
    }

    @Override
    public boolean validate(Object value, String fieldName)
    {
        return commons.validator.routines.EmailValidator.getInstance().isValid((String) value);
    }
}
