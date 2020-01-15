package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;

import java.util.List;

/**
 * Created by shrey on 24/4/15.
 */
public class ListingListViewAdapter extends ArrayAdapter<Listing_>
{
    static class ViewHolder
    {
        TextView title;
        TextView category;
        TextView reviewCount;

    }
    ListingListViewAdapter(Context context,int resource,List<Listing_> list)
    {
        super(context,resource,list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.listings_featured_row,parent,false);
        }
        Listing_ listing = getItem(position);

        ((TextView)convertView.findViewById(R.id.listing_title)).setText(listing.getTitle());

        String categories = TextUtils.join(",",listing.getCategoryTitles().values());
        ((TextView)convertView.findViewById(R.id.category_list)).setText(categories);

        String reviewsCount = getContext().getResources().getQuantityString(R.plurals.review_count,listing.getReviewCount(),listing.getReviewCount());
        ((TextView)convertView.findViewById(R.id.review_count)).setText(reviewsCount);

        return convertView;
    }
}
