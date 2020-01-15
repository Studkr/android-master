package com.github.dkharrat.nexusdialog.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.dkharrat.nexusdialog.R;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.validations.Validator;
import com.github.dkharrat.nexusdialog.validations.validator.RequiredTextValidator;

import java.util.List;

/**
 * An abstract class that represents a generic form field with an associated label.
 */
public abstract class LabeledFieldController extends FormElementController
{
    public static String LABEL_ORIENTATION_VERTICAL="vertical";
    public static String LABEL_ORIENTATION_HORIZONTAL="horizontal";
    private View fieldView;
    private String mLabelOrientation=LABEL_ORIENTATION_HORIZONTAL;
    private boolean showLabel = true;

    /**
     * Creates a labeled field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. If null, no label is displayed and the field will
     *                      occupy the entire length of the row.
     * @param isRequired    indicates whether this field is required. If true, this field checks for a non-empty or
     *                      non-null value upon validation. Otherwise, this field can be empty.
     */
    public LabeledFieldController(Context ctx, String name, String labelText, boolean isRequired)
    {
        super(ctx, name,labelText,isRequired);
    }

    /**
     * Returns the associated view for the field (without the label view) of this element.
     *
     * @return          the view for this element
     */
    public View getFieldView() {
        if (fieldView == null) {
            fieldView = createFieldView();
        }
        return fieldView;
    }

    /**
     * Constructs the view associated with this field without the label. It will be used to combine with the label.
     *
     * @return          the newly created view for this field
     */
    protected abstract View createFieldView();

    @Override
    protected View createView() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layout = R.layout.form_labeled_element;
        if(mLabelOrientation == LABEL_ORIENTATION_VERTICAL)
        {
            layout = R.layout.form_labeled_element_vertical;
        }
        View view = inflater.inflate(layout, null);

        TextView label = (TextView)view.findViewById(R.id.field_label);
        if (getLabel() == null || this.showLabel == false)
        {
            label.setVisibility(View.GONE);
        } else
        {
            label.setText(getLabel());
        }

        FrameLayout container = (FrameLayout)view.findViewById(R.id.field_container);
        container.addView(getFieldView());

        return view;
    }
    public void setLabelOrientation(String orientation)
    {
        mLabelOrientation = orientation;
    }

    @Override
    public List<Validator> getValidationRules()
    {
        List<Validator> rules = super.getValidationRules();
        if(isRequired())
        {
            rules.add(new RequiredTextValidator());
        }
        return rules;
    }

    public void setShowLabel(boolean showLabel)
    {
        this.showLabel = showLabel;
    }
}