package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Suggestion;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.ListingApiRequest;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.INT.apps.GpsspecialDevelopment.views.decorator.ClearableEditTextDecorator;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SearchBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchBoxFragment extends Fragment {
    private static final String ARG_SEARCH_QUERY = "search_query";
    private static final String ARG_FOCUS_LOCATION = "focus_location";

    private boolean mFocusOnLocation = false;
    private boolean mBackEventAdded = false;
    private QuerySelectListener mQuerySelectListener;

    @InjectView(R.id.listing_keyword)
    AutoCompleteTextView listingSearchBox;

    @InjectView(R.id.listing_search_wrapper)
    LinearLayout searchWrapper;

    @InjectView(R.id.location_search_box)
    EditText locationSearchBox;

    @InjectView(R.id.search_button)
    ImageButton searchButton;

    ListingPaginationQueryBuilder mQueryBuilder;
    ListingPaginationQueryBuilder mQueryBuilderClone;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchboxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchBoxFragment newInstance(HashMap<String, String> searchQuery, boolean focusOnLocation) {
        SearchBoxFragment fragment = new SearchBoxFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_QUERY, searchQuery);
        args.putBoolean(ARG_FOCUS_LOCATION, focusOnLocation);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchBoxFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HashMap<String, String> mSearchQuery = (HashMap<String, String>) getArguments().getSerializable(ARG_SEARCH_QUERY);
            mFocusOnLocation = getArguments().getBoolean(ARG_FOCUS_LOCATION, false);
            mQueryBuilder = ListingPaginationQueryBuilder.newInstance(mSearchQuery);
            mQueryBuilderClone = ListingPaginationQueryBuilder.newInstance(mSearchQuery);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchbox, container, false);
        ButterKnife.inject(this, view);
        handleListingSearchBox();
        ViewTreeObserver observer = searchWrapper.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mFocusOnLocation) {
                        locationSearchBox.setText("");
                        locationSearchBox.requestFocus();
                    } else {
                        listingSearchBox.requestFocus();
                    }
                    searchWrapper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    listingSearchBox.setDropDownAnchor(R.id.dp_anchor);
//                    listingSearchBox.setDropDownHorizontalOffset(0);
                    listingSearchBox.setDropDownHeight(800);
                }
            });
        }
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (savedInstanceState != null) {
            listingSearchBox.setText(savedInstanceState.getString("listingSearchBox"));
            locationSearchBox.setText(savedInstanceState.getString("locationSearchBox"));
        }
        return view;
    }

    private void handleListingSearchBox() {
        listingSearchBox.setThreshold(1);
        listingSearchBox.setAdapter(new SuggestionAdapter(this, ""));
        listingSearchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Suggestion suggestion = (Suggestion) listingSearchBox.getAdapter().getItem(position);
                listingSearchBox.setText(suggestion.getName());
                listingSearchBox.setSelection(listingSearchBox.getText().length());
                if (suggestion.isCategory()) {
                    mQueryBuilderClone.setSearchListingCategory(suggestion.getCategoryId(), suggestion.getName());
                    mQueryBuilderClone.setSearchKeyword("");
                } else if (suggestion.isKeyword()) {
                    mQueryBuilderClone.removeSearchListingCategory();
                    mQueryBuilderClone.setSearchKeyword(suggestion.getName());
                }
                postSearchResult(mQueryBuilderClone);
            }
        });
        String text = mQueryBuilder.getSearchKeyword();
        if (mQueryBuilder.getSearchListingCategory().length() > 0) {
            text = mQueryBuilder.getSearchListingCategory();
        }
        if (text.length() == 0) {
            text = getResources().getString(R.string.everything);
        }
        listingSearchBox.setText(text);
        new ClearableEditTextDecorator(listingSearchBox);
        new ClearableEditTextDecorator(locationSearchBox);
        listingSearchBox.setSelectAllOnFocus(true);
        //SAQIBSEARCH
        String locationKeyword = mQueryBuilder.getLocationSearchKeyword();
        if (locationKeyword != null && locationKeyword.length() > 0) {
            locationSearchBox.setText(locationKeyword);
        } else {
            locationSearchBox.setText(getResources().getString(R.string.my_location));
            locationSearchBox.setSelectAllOnFocus(true);
            locationSearchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (locationSearchBox.getText().equals(getResources().getString(R.string.my_location))) {
                        locationSearchBox.setSelectAllOnFocus(true);
                    } else {
                        locationSearchBox.setSelectAllOnFocus(false);
                    }
                }
            });
        }
        locationSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    locationSearchBox.clearFocus();
                    locationSearchBox.setSelectAllOnFocus(false);
                    OnSearchButtonClick();
                    return true;
                }
                return false;
            }
        });
        listingSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    listingSearchBox.setSelectAllOnFocus(false);
                    listingSearchBox.clearFocus();
                    OnSearchButtonClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void setLocationValue(ListingPaginationQueryBuilder queryBuilder) {
        String locationValue = locationSearchBox.getText().toString();
        if (locationValue.equals(getResources().getString(R.string.my_location))) {
            queryBuilder.setSearchLocationKeyword("");
        } else {
            queryBuilder.setSearchLocationKeyword(locationValue);
        }
    }

    @Override
    public void onDetach() {
        mQuerySelectListener = null;
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof QuerySelectListener) {
            mQuerySelectListener = (QuerySelectListener) activity;
        } else {
            Fragment fragment = getTargetFragment();
            if (fragment != null && fragment instanceof QuerySelectListener) {
                mQuerySelectListener = (QuerySelectListener) fragment;
            }
        }
        if (mQuerySelectListener == null) {
            throw new RuntimeException("No QuerySelectListener instance found, either activity or target fragment should implment it.");
        }
        super.onAttach(activity);
    }

    //dirty workaround called when searchbox is reappeared
    public void onReAppear() {
        if (listingSearchBox != null) {
            listingSearchBox.requestFocus();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void listenToBackEvent() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        if (mBackEventAdded) {
            return;
        }
        mBackEventAdded = true;
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (getView().getVisibility() == View.VISIBLE && mQuerySelectListener != null && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    mQuerySelectListener.onSearchCancel(mQueryBuilderClone);
                    return true;
                }
                return false;
            }
        });
    }

    protected void postSearchResult(ListingPaginationQueryBuilder queryBuilder) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        setLocationValue(queryBuilder);
        mQuerySelectListener.onQueryResult(queryBuilder);
    }

    @OnClick(R.id.search_button)
    public void OnSearchButtonClick() {
        String searchText = listingSearchBox.getText().toString();
        if (searchText.equals(getResources().getString(R.string.everything))) {
            searchText = "";
        }
        if (searchText.length() > 0) {
            if (!mQueryBuilderClone.getSearchKeyword().equalsIgnoreCase(searchText) && !mQueryBuilderClone.getSearchListingCategory().equalsIgnoreCase(searchText)) {
                mQueryBuilderClone.setSearchKeyword(searchText).removeSearchListingCategory();
            }
        } else {
            mQueryBuilderClone.setSearchKeyword("").removeSearchListingCategory();
        }
        postSearchResult(mQueryBuilderClone);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (listingSearchBox != null) {
            outState.putString("listingSearchBox", listingSearchBox.getText().toString());
            outState.putString("locationSearchBox", locationSearchBox.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    public interface QuerySelectListener {
        void onQueryResult(ListingPaginationQueryBuilder query);

        void onSearchCancel(ListingPaginationQueryBuilder query);
    }
}

class SuggestionAdapter extends ArrayAdapter<Suggestion> {
    String mType;
    Fragment mFragment;
    Filter mFilter;

    SuggestionAdapter(Fragment fragment, String type) {
        super(fragment.getActivity().getApplicationContext(), 0);
        mFragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (mFragment.getActivity()).getLayoutInflater().inflate(R.layout.listing_suggestion, parent, false);
        }
        Suggestion item = getItem(position);
        ((TextView) convertView.
                findViewById(R.id.keyword))
                .setText(item.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SuggestionFilter(this, mType);
        }
        return mFilter;
    }
}

class SuggestionFilter extends Filter {
    String mTtype;
    SuggestionAdapter mSearchAdapter;

    SuggestionFilter(SuggestionAdapter searchAdapter, String type) {
        mSearchAdapter = searchAdapter;
        mTtype = type;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint == null) {
            return null;
        }
        FilterResults results = new FilterResults();
        ListingApiRequest request = new ListingApiRequest(IOBus.getInstance());
        String search = constraint.toString();
        List<Suggestion> suggestions = request.listingKeywordSuggestionSync(search);
        results.count = suggestions.size();
        results.values = suggestions;
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results != null) {
            mSearchAdapter.clear();
        }
        if (results != null && results.count > 0) {
            for (Suggestion suggestion : (List<Suggestion>) results.values) {
                mSearchAdapter.add(suggestion);
            }
        }
    }
}


