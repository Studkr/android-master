package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListDealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListDealsFragment extends Fragment {
    List<DealInfo> mDeals = new ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListDealsFragment newInstance() {
        ListDealsFragment fragment = new ListDealsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListDealsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("ListDeals fragment");
    }

    private View showDeal(final DealInfo dealsInfo, int count, int total) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.listing_fragment_deal_template, null, false);
        //SAQIB---------------------------
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y ;
        //Get action bar height from dimen and convert into pixle
        int diduction = (int) (getResources().getDimension(R.dimen.abc_action_bar_default_height_material) / getResources().getDisplayMetrics().density);
        diduction = diduction + getStatusBarHeight();
        int finalHeight = height - diduction ;
        int imageHeight = finalHeight * 70/100;
        int detailLayoutHeight = finalHeight * 30/100;
        Timber.d("^^^^ -------------------> ");
        Timber.d("^^^^ screen height :-> %d", height);
        Timber.d("^^^^ statusbar :-> %d", getStatusBarHeight());
        Timber.d("^^^^ final :-> %d", finalHeight);
        Timber.d("^^^^ image :-> %d", imageHeight);
        Timber.d("^^^^ detail layout :-> %d", detailLayoutHeight);
        Timber.d("^^^^ image url :-> %s", dealsInfo.getImage());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,finalHeight);
        view.setLayoutParams(params);
        //SAQIB---------------------------


        /*LinearLayout dealDetailLayout = (LinearLayout)view.findViewById(R.id.deal_detail_layout_main);
        LinearLayout.LayoutParams paramsDetailLayout = new LinearLayout.LayoutParams(width,finalHeight);
        dealDetailLayout.setLayoutParams(paramsDetailLayout);*/

        ImageView deal_image = (ImageView) view.findViewById(R.id.deal_image);
        /*LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(width,imageHeight);
        deal_image.setLayoutParams(paramsImage);*/

        setDealImage(dealsInfo.getImage(), deal_image);

        Button buy_deal = (Button) view.findViewById(R.id.buy_deal);

        ((TextView) view.findViewById(R.id.deal_title)).setText(dealsInfo.getTitle());
        ((TextView) view.findViewById(R.id.discount)).setText(dealsInfo.getDiscount());
        ((TextView) view.findViewById(R.id.bought)).setText(dealsInfo.getQuantityConsumed());
        TextView originalPrice = view.findViewById(R.id.original_price);
        originalPrice.setText(dealsInfo.getCurrencySymbol() + dealsInfo.getRegularPrice());
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ((TextView) view.findViewById(R.id.discounted_price)).setText(dealsInfo.getCurrencySymbol()
                + dealsInfo.getFinalPrice());
        if (dealsInfo.getTotalQuantity().equals("0")) {
            ((TextView) view.findViewById(R.id.availability)).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.availability)).setVisibility(View.VISIBLE);
        }

        buy_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(getActivity(),BuyDealActivity.class);
                intent.putExtra(BuyDealActivity.ARG_DEAL_ID, dealsInfo.getId());
                startActivity(intent);*/
                Intent intent = new Intent(getActivity(), DealInformationActivity.class);
                intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealsInfo.getId());
                intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealsInfo.getTitle());
                intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealsInfo.getImage());
                startActivity(intent);
            }
        });
        if (count == total) {
            view.findViewById(R.id.bottom_border).setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DealInformationActivity.class);
                intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealsInfo.getId());
                intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealsInfo.getTitle());
                intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealsInfo.getImage());
                startActivity(intent);
            }
        });
        return view;
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private void setDealImage(String dealImage, ImageView imgView) {
        dealImage = CvUrls.getImageUrlForSize(dealImage, 640, 400, true);
        Picasso.get().load(dealImage).placeholder(R.drawable.placeholder600).into(imgView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layoutView = inflater.inflate(R.layout.listing_fragment_deals, container, false);
        if (mDeals != null && mDeals.size() > 0) {
            int i = 1;
            for (DealInfo dealsInfo : mDeals) {
                View view = showDeal(dealsInfo, i, mDeals.size());
                ((ViewGroup) layoutView).addView(view);
                i++;
            }
        }
        return layoutView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDeals = ((DealsListProvider) getActivity()).getDeals();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface DealsListProvider {
        // TODO: Update argument type and name
        public List<DealInfo> getDeals();
    }

}
