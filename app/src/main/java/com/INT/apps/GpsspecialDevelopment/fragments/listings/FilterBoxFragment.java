package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;

import java.util.HashMap;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FilterBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterBoxFragment extends DialogFragment {
    private static final String ARG_QUERY = "query";

    // TODO: Rename and change types of parameters
    private ListingPaginationQueryBuilder mQueryBuilder;

    private HashMap<String, FormElementController> mFieldsMap = new HashMap<>();

    View mainView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilterBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterBoxFragment newInstance(ListingPaginationQueryBuilder mQuery) {
        FilterBoxFragment fragment = new FilterBoxFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUERY, mQuery.build());
        fragment.setArguments(args);
        return fragment;
    }

    public FilterBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_QUERY))
                mQueryBuilder = ListingPaginationQueryBuilder.newInstance((HashMap<String, String>) getArguments().getSerializable(ARG_QUERY));
        }
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_filter_box, container, false);
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_filter_box, null);
        mainView = v;
        setFilterForm();
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.filters)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = getActivity();
                        if (activity instanceof SearchFilteringResult) {
                            ((SearchFilteringResult) activity).onSearchFilteringCancel();
                        }
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sort = (String) mFieldsMap.get("sort").getModel().getValue(mFieldsMap.get("sort").getName());
                        String distance = (String) mFieldsMap.get("distance").getModel().getValue(mFieldsMap.get("distance").getName());
                        mQueryBuilder.setDistance(distance).setSorting(sort);
                        Timber.tag("got result").d(mQueryBuilder.toString());
                        Activity activity = getActivity();
                        if (activity instanceof SearchFilteringResult) {
                            ((SearchFilteringResult) activity).onSearchFilteringResult(mQueryBuilder);
                        }
                    }
                })
                .create();

        return alertDialog;
    }

    private void setFilterForm() {
        String sortByLabel = getString(R.string.filter_label_sort_by);
        String distanceLabel = getString(R.string.filter_label_search_radius);

        mFieldsMap.clear();
        FormController formController = new FormController(getActivity());
        FormSectionController formSectionController = new FormSectionController(getActivity());

        SelectionController sortField = new SelectionController(getActivity(), "sort", sortByLabel, false, mQueryBuilder.getSortingOptions(getResources()));
        sortField.setDefaultValue(mQueryBuilder.getSorting());
        SelectionController distanceField = new SelectionController(getActivity(), "distance", distanceLabel, false, mQueryBuilder.getDistanceOptions(getResources()));
        distanceField.setDefaultValue(mQueryBuilder.getDistance());
        formSectionController.addElement(sortField);
        formSectionController.addElement(distanceField);
        formController.addSection(formSectionController);
        formController.addFormElementsToView((ViewGroup) mainView.findViewById(R.id.filter_form));
        mFieldsMap.put("sort", sortField);
        mFieldsMap.put("distance",distanceField);
    }

    public interface SearchFilteringResult {
        void onSearchFilteringResult(ListingPaginationQueryBuilder builder);

        void onSearchFilteringCancel();
    }
}