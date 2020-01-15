package com.github.dkharrat.nexusdialog;

import android.content.Context;
import android.view.View;

import com.github.dkharrat.nexusdialog.validations.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all form elements, such as text fields, buttons, sections, etc. Each {@code FormElementController}
 * is referred by a name and has an associated {@link FormModel}.
 */
public abstract class FormElementController {
    private final Context context;
    private final String name;
    private FormModel model;
    private View view;
    private List<Validator> mValidatorList = new ArrayList<>();
    private String labelText;
    private boolean required;

    /**
     * Constructs a new instance with the specified name.
     *
     * @param ctx   the Android context
     * @param name  the name of this instance
     */
    protected FormElementController(Context ctx, String name) {
        this.context = ctx;
        this.name = name;
    }
    protected FormElementController(Context ctx, String name, String labelText, boolean isRequired)
    {
        this.context = ctx;
        this.name = name;
        this.labelText = labelText;
        this.required = isRequired;

    }

    /**
     * Returns the associated label for this field.
     *
     * @return the associated label for this field
     */
    public String getLabel() {
        return labelText;
    }

    /**
     * Sets whether this field is required to have user input.
     *
     * @param required  if true, this field checks for a non-empty or non-null value upon validation. Otherwise, this
     *                  field can be empty.
     */
    public void setIsRequired(boolean required) {
        this.required = required;
    }

    /**
     * Indicates whether this field requires an input value.
     *
     * @return  true if this field is required to have input, otherwise false
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Returns the Android context associated with this element.
     *
     * @return the Android context associated with this element
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns the name of this form element.
     *
     * @return  the name of the element
     */
    public String getName() {
        return name;
    }

    void setModel(FormModel model) {
        this.model = model;
    }

    /**
     * Returns the associated model of this form element.
     *
     * @return the associated model of this form element
     */
    public FormModel getModel() {
        return model;
    }

    /**
     * Returns the associated view for this element.
     *
     * @return          the view for this element
     */
    public View getView() {
        if (view == null) {
            view = createView();
        }
        return view;
    }

    /**
     * Indicates if the view has been created.
     *
     * @return true if the view was created, or false otherwise.
     */
    public boolean isViewCreated() {
        return view != null;
    }

    /**
     * Constructs the view for this element.
     *
     * @return          a newly created view for this element
     */
    protected abstract View createView();

    /**
     * Refreshes the view of this element to reflect current model.
     */
    public abstract void refresh();

    public void addValidationRule(Validator validator)
    {
        mValidatorList.add(validator);
    }

    public List<Validator> getValidationRules()
    {
        return mValidatorList;
    }


}