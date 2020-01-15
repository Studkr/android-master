package com.INT.apps.GpsspecialDevelopment.views;

import android.app.Activity;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperty;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.EmailController;
import com.github.dkharrat.nexusdialog.controllers.LabeledFieldController;
import com.github.dkharrat.nexusdialog.controllers.RatingController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;

/**
 * Created by shrey on 15/6/15.
 */
public class FormFieldFactory {
    static private String sLabelOrientation = LabeledFieldController.LABEL_ORIENTATION_HORIZONTAL;

    static public FormElementController getField(FieldsProperty fieldsProperty, Activity activity) {
        return getField(fieldsProperty, activity, true);
    }

    static public void setLabelOrientationConfig(String orientationConfig) {
        sLabelOrientation = orientationConfig;
    }

    static public FormElementController getField(FieldsProperty fieldsProperty, Activity activity, boolean showLabel) {
        FormElementController controller = null;
        String label = null, hintText = null;
        label = fieldsProperty.getLabel();
        if (showLabel == false) {
            hintText = fieldsProperty.getLabel();
        }

        if (fieldsProperty.getType().equals(FieldsProperty.TYPE_TEXT) || fieldsProperty.getType().equals(FieldsProperty.TYPE_PASSWORD) || fieldsProperty.getType().equals(FieldsProperty.TYPE_TEXTAREA)) {
            controller = new EditTextController(activity, fieldsProperty.getField(), label, hintText, fieldsProperty.getRequired());
            if (fieldsProperty.getType().equals(FieldsProperty.TYPE_PASSWORD)) {
                ((EditTextController) controller).setSecureEntry(true);
            } else if (fieldsProperty.getType().equals(FieldsProperty.TYPE_TEXTAREA)) {
                ((EditTextController) controller).setMultiLine(true);
            }
        } else if (fieldsProperty.getType().equals(FieldsProperty.TYPE_EMAIL)) {
            controller = new EmailController(activity, fieldsProperty.getField(), label, hintText, fieldsProperty.getRequired());
        } else if (fieldsProperty.getType().equals(FieldsProperty.TYPE_PHONE_NO)) {
            controller = new EmailController(activity, fieldsProperty.getField(), label, hintText, fieldsProperty.getRequired());
        } else if (fieldsProperty.getType().equals(FieldsProperty.TYPE_ZIPCODE)) {
            controller = new EmailController(activity, fieldsProperty.getField(), label, hintText, fieldsProperty.getRequired());
        } else if (fieldsProperty.getType().equals(FieldsProperty.TYPE_RATING)) {
            controller = new RatingController(activity, fieldsProperty.getField(), label, fieldsProperty.getRequired());
        } else if (FieldsProperty.isSelectField(fieldsProperty.getType())) {
            controller = new SelectionController(activity, fieldsProperty.getField(), fieldsProperty.getLabel(), fieldsProperty.getRequired(), fieldsProperty.getOptions());

        }
        if (controller instanceof LabeledFieldController) {
            if (controller instanceof SelectionController == false) {
                ((LabeledFieldController) controller).setShowLabel(showLabel);
            }
            ((LabeledFieldController) controller).setLabelOrientation(sLabelOrientation);
        }
        return controller;
    }
}
