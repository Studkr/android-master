package com.github.dkharrat.nexusdialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewGroup;

import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.utils.MessageUtil;
import com.github.dkharrat.nexusdialog.validations.Validator;
import com.github.dkharrat.nexusdialog.validations.errors.Error;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>FormController</code> is the main class that manages the form elements of NexusDialog. It provides simple APIs
 * to quickly create and manage form fields. Typically, an instance of this class is created within an Activity or Fragment.
 * <p/>
 * The form's data is backed by a model represented by {@link FormModel}, which provides a generic interface to access
 * the data. Form elements use the model to retrieve current field values and set them upon user input. By default,
 * <code>FormController</code> uses a default Map-based model keyed by the element's names. You can also use a custom
 * implementation of a <code>FormModel</code> if desired.
 */
public class FormController {
    private final List<FormSectionController> sectionControllers = new ArrayList<FormSectionController>();

    private final Context context;

    public FormController(Context context) {
        this.context = context;
    }

    /**
     * Returns the associated model of this form.
     *
     * @return the associated model of this form
     */
    public FormModel getModel() {
        return model;
    }

    /**
     * Sets the model to use for this form
     *
     * @param formModel the model to use
     */
    public void setModel(FormModel formModel) {
        this.model = formModel;
        registerFormModelListener();
    }

    private void registerFormModelListener() {
        // unregister listener first to make sure we only have one listener registered.
        getModel().removePropertyChangeListener(modelListener);
        getModel().addPropertyChangeListener(modelListener);
    }

    /**
     * Returns a list of the sections of this form.
     *
     * @return a list containing all the <code>FormSectionController</code>'s of this form
     */
    public List<FormSectionController> getSections() {
        return sectionControllers;
    }

    /**
     * Returns the corresponding <code>FormSectionController</code> from the specified name.
     *
     * @param name  the name of the section
     * @return      the instance of <code>FormSectionController</code> with the specified name, or null if no such
     *              section exists
     */
    public FormSectionController getSection(String name) {
        for (FormSectionController section : getSections()) {
            if (section.getName().equals(name)) {
                return section;
            }
        }
        return null;
    }

    /**
     * Adds the specified section to the form.
     *
     * @param section the form section to add
     */
    public void addSection(FormSectionController section) {
        sectionControllers.add(section);
    }

    /**
     * Returns the corresponding <code>FormElementController</code> from the specified name.
     * @param name  the name of the form element
     * @return      the instance of <code>FormElementController</code> with the specified name, or null if no such
     *              element exists
     */
    public FormElementController getElement(String name) {
        for (FormSectionController section : getSections()) {
            FormElementController element = section.getElement(name);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    /**
     * Returns the total number of elements in this form, not including sections.
     *
     * @return  the total number of elements in this form, not including sections
     */
    public int getNumberOfElements() {
        int count = 0;
        for (FormSectionController section : getSections()) {
            count += section.getElements().size();
        }

        return count;
    }

    /**
     * Refreshes the view of all elements in this form to reflect current model values
     */
    public void refreshElements() {
        for (FormSectionController section : getSections()) {
            section.refresh();
        }
    }

    /**
     * Returns a list of validation errors of the form's input
     *
     * @return a list of validation errors of the form's input
     */
    public HashMap<FormElementController,Error> validateInput() {
        HashMap<FormElementController,Error> errors = new HashMap<>();

        for (FormSectionController section : getSections()) {
            for (FormElementController element : section.getElements())
            {
                Error error = validateField(element);
                if (error != null)
                {
                    errors.put(element,error);
                }
            }
        }

        return errors;
    }
    private Error validateField(FormElementController controller)
    {
        Error error = null;
        for(Validator validator : controller.getValidationRules())
        {
            Object value = getModel().getValue(controller.getName());
            if(validator.validate(value,controller.getName()) == false)
            {
                error = validator.getValidationError(controller,value);
                break;
            }
        }
        return error;
    }

    /**
     * Indicates if the current user input is valid.
     *
     * @return  true if the current user input is valid, otherwise false
     */
    public boolean isValidInput() {
        return validateInput().isEmpty();
    }

    /**
     * Shows an appropriate error message if there are validation errors in the form's input.
     */
    public boolean showValidationErrors() {
        StringBuilder sb = new StringBuilder();
        Resources res = context.getResources();
        HashMap<FormElementController,Error> errors = validateInput();
        for (FormElementController elementController : errors.keySet())
        {
            Error error = errors.get(elementController);
            String fieldLabel = elementController.getLabel();
            sb.append(error.getMessage(res,fieldLabel) + "\n");
        }
        boolean error = false;
        if(errors.size() > 0)
        {
            error = true;
            MessageUtil.showAlertMessage(context.getString(R.string.validation_error_title), sb.toString(), context);
        }
        return error;
    }

    /**
     * Adds all the form elements that were added to this <code>FormController</code> inside the specified
     * <code>ViewGroup</code>. This method should be called once the form elements have been added to this controller.
     *
     * @param containerView the view container to add the form elements within
     */
    public void addFormElementsToView(ViewGroup containerView) {
        for (FormSectionController section : getSections()) {
            ((FormElementController)section).setModel(getModel());
            // TODO: 6/21/18 add check for view existence
            containerView.addView(section.getView());

            for (FormElementController element : section.getElements()) {
                element.setModel(getModel());

                containerView.addView(element.getView());
            }
        }

        // now that the view is setup, register a listener of the model to update the view on changes
        registerFormModelListener();
    }

    private FormModel model = new FormModel() {

        private final Map<String,Object> data = new HashMap<String,Object>();

        @Override
        public Object getBackingValue(String name) {
            return data.get(name);
        }

        @Override
        public void setBackingValue(String name, Object value) {
            data.put(name, value);
        }
    };

    private PropertyChangeListener modelListener = new PropertyChangeListener() {
        @Override public void propertyChange(PropertyChangeEvent event) {
            getElement(event.getPropertyName()).refresh();
        }
    };
}
