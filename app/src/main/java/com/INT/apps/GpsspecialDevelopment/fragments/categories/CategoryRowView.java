package com.INT.apps.GpsspecialDevelopment.fragments.categories;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Created by shrey on 28/4/15.
 */
class CategoryRowView
{
    boolean mIsNavigable = false;
    boolean mShowIcons = true;
    boolean mIsListView = false;
    public CategoryRowView()
    {

    }
    public CategoryRowView(boolean isNav,boolean showIcons,boolean isListView)
    {
           mIsNavigable = isNav;
           mShowIcons = showIcons;
           mIsListView = isListView;
    }
    public View getView(Activity activity,Category category,View convertView,ViewGroup parent)
    {
        TextView textView = (TextView)convertView.findViewById(R.id.row_title);
        textView.setText(category.getTitle());
        String logo = category.getLogoPath();
        ImageView imageView = (ImageView) convertView.findViewById(R.id.category_icon);
        imageView.setAdjustViewBounds(true);
        convertView.findViewById(R.id.category_icon_wrap).setVisibility(mShowIcons == true ? View.VISIBLE: View.GONE);
        if(logo != null && mShowIcons)
        {
            Timber.d("----** path :%s", logo);
            //"Dynamic"
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(logo).placeholder(R.drawable.category_more).into(imageView);

            /*Drawable res = getCategoryIconDrawable(activity,logo);
            if(res != null)
            {
                imageView.setImageDrawable(res);
                imageView.setVisibility(View.VISIBLE);
            }else
            {
                //imageView.setImageDrawable();
                imageView.setVisibility(View.GONE);
            }*/
        }else
        {
            imageView.setVisibility(View.GONE);
        }
        if(mIsNavigable && category.getChildrenCount() > 0)
        {
            ((ImageView)convertView.findViewById(R.id.category_nav)).setVisibility(View.VISIBLE);
        }
        if(mIsListView)
        {
            convertView.findViewById(R.id.row_divider).setVisibility(View.GONE);
        }
        return convertView;
    }
    private  Drawable getCategoryIconDrawable(Activity activity,String iconPath)
    {
        iconPath = "@drawable/"+iconPath;
        int resource = activity.getResources().getIdentifier(iconPath,null,activity.getPackageName());
        Drawable res = null;
        try {
            res = activity.getResources().getDrawable(resource);
            return res;
        }catch (Resources.NotFoundException e)
        {

        }
        return  res;
    }

    public void setIsNavigable(boolean isNavigable)
    {
        mIsNavigable = isNavigable;
    }

    public void setShowIcons(boolean showIcons)
    {
        mShowIcons = showIcons;
    }
}
