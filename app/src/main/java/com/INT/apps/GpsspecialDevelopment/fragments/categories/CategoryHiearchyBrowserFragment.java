package com.INT.apps.GpsspecialDevelopment.fragments.categories;


import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.INT.apps.GpsspecialDevelopment.data.models.CategoryBrowser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryHiearchyBrowserFragment extends ListFragment
{
    public final static String PARENT_CATEGORY_ID = "parent_category_id";
    private ArrayList<Category> mCategories;
    private OnCategoryFragmentInteractionListener mListener;

    public CategoryHiearchyBrowserFragment()
    {
        super();
    }

    protected ArrayList<Category> getCategories()
    {
        int parentId = getArguments().getInt(PARENT_CATEGORY_ID);
        if(mCategories == null)
        {
            mCategories = CategoryBrowser.getInstance(getActivity().getApplicationContext()).getChildrenCategories(parentId);
        }
        return mCategories;
    }

    public static CategoryHiearchyBrowserFragment newInstance(Integer parentId)
    {
        CategoryHiearchyBrowserFragment categoryHiearchyBrowserFragment = new CategoryHiearchyBrowserFragment();
        Bundle args = new Bundle();
        args.putInt(PARENT_CATEGORY_ID,parentId);
        categoryHiearchyBrowserFragment.setArguments(args);
        return categoryHiearchyBrowserFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        attachListAdapter();
        super.onAttach(activity);
        try
        {
            mListener = (OnCategoryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void attachListAdapter()
    {
        setListAdapter(new CategoryListAdapter());
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onCategoryFragmentInteraction(getCategories().get(position));
        }
    }

    class CategoryListAdapter extends ArrayAdapter<Category>
    {
        CategoryRowView mCategoryRowView;
        public CategoryListAdapter()
        {
            super(getActivity(),0,getCategories());
            mCategoryRowView = new CategoryRowView(true,false,true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_category,null);
            }
            return mCategoryRowView.getView(getActivity(),getItem(position),convertView,parent);
        }
    }
    public interface OnCategoryFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onCategoryFragmentInteraction(Category category);
    }
}
