package com.INT.apps.GpsspecialDevelopment.io.api_service.converters;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.Postable;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit.converter.GsonConverter;
import retrofit.mime.FormUrlEncodedTypedOutput;
import retrofit.mime.TypedOutput;

public class CvGsonConverter extends GsonConverter {

    public CvGsonConverter(Gson gson) {
        super(gson);
    }

    @Override
    public TypedOutput toBody(Object object) {
        if (object instanceof Postable) {
            HashMap<String, String> fieldValues = ((Postable) object).getFieldValues();
            String wrapperName = ((Postable) object).getPostModelName();
            FormUrlEncodedTypedOutput encodeOutput = new FormUrlEncodedTypedOutput();
            encodeOutput.addField("_method", "POST");
            for (String fieldName : fieldValues.keySet()) {
                String value = fieldValues.get(fieldName);
                fieldName = "data[" + wrapperName + "][" + fieldName + "]";
                encodeOutput.addField(fieldName, true, value, true);
            }
            return encodeOutput;
        }
        return super.toBody(object);
    }
}
