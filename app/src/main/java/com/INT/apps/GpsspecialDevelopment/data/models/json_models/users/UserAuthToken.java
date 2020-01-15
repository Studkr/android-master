package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by shrey on 10/6/15.
 */
public class UserAuthToken
{
    String token;
    @SerializedName("user_id")
    String userId;
    String expires;

    public String getExpires()
    {
        return expires;
    }

    public String getToken()
    {
        return token;
    }

    public String getUserId()
    {
        return userId;
    }

    public boolean isExpired()
    {
        return DateParser.fromStringToDate(expires).compareTo(new Date()) < 0;
    }
}
