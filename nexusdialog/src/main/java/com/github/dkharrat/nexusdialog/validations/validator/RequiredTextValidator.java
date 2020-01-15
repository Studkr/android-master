package com.github.dkharrat.nexusdialog.validations.validator;

import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.validations.Validator;
import com.github.dkharrat.nexusdialog.validations.errors.Error;
import com.github.dkharrat.nexusdialog.validations.errors.RequiredError;

/**
 * Created by shrey on 16/6/15.
 */
public class RequiredTextValidator implements Validator
{
    @Override
    public Error getValidationError(FormElementController fieldController, Object value)
    {
        return new RequiredError(fieldController.getName());
    }

    @Override
    public boolean validate(Object value, String fieldName)
    {
        //Log.d("validation field",fieldName+ " value %"+value+ "% "+value.toString().length());
        if(value == null)
        {
            return false;
        }

        return !(value.toString()).isEmpty();
    }
}
