package com.github.dkharrat.nexusdialog.validations;

import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.validations.errors.Error;

/**
 * Created by shrey on 16/6/15.
 */
public interface Validator
{
    public boolean validate(Object value,String fieldName);

    public Error getValidationError(FormElementController fieldController,Object value);
}
