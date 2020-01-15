package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by shrey on 13/5/15.
 */
public class Suggestions
{
    ArrayList<Suggestion> suggestions;

    public ArrayList<Suggestion> getSuggestions()
    {
        Timber.tag("abc").d("get suggestions");
        Timber.tag("abc").d(String.valueOf(suggestions));
        return suggestions;
    }
}
