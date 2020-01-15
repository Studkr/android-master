package com.INT.apps.GpsspecialDevelopment.utils;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by shrey on 19/8/15.
 */
public class ReviewPaginationQuery
{
    private HashMap<String,String> parameters = new HashMap<>();
    public  ReviewPaginationQuery()
    {

    }
    public  ReviewPaginationQuery(String query)
    {
        initFromString(query);
    }

    private void initFromString(String query)
    {
        String[] parameters = TextUtils.split(query, ";");
        for(int i=0;i<parameters.length;i++)
        {
            String[] parametersPart = TextUtils.split(parameters[i],":");
            if(parametersPart.length == 2)
            {
                addParameter(parametersPart[0], parametersPart[1]);
            }
        }
    }
    protected void addParameter(String key,String value)
    {
        parameters.put(key, value);
    }

    public void setMethod(String method)
    {
        addParameter("method",method);
    }
    public void setPage(String page)
    {
        addParameter("page",page);
    }

    public void setLimit(String limit)
    {
        addParameter("limit",limit);
    }

    public void setNamedParameter(String parameter,String value)
    {
        addParameter(parameter,value);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(String key:parameters.keySet())
        {
            sb.append(key);
            sb.append(":");
            sb.append(parameters.get(key));
            sb.append(";");
        }
        return sb.toString();
    }

    public String getParameter(String key)
    {
        return parameters.get(key);
    }
}
