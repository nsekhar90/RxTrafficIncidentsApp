package com.example.sekharn.trafficincidents.util;

import android.util.Log;

import com.example.sekharn.trafficincidents.model.LocationAddress;

public class ApiUtils {

    public static String getFormattedLatLongForTrafficDetailsApi(LocationAddress sourceLocation, LocationAddress destinationAddress) {
        StringBuilder builder = new StringBuilder();
        builder.append("")
                .append(sourceLocation.getLatitude())
                .append(",")
                .append(sourceLocation.getLongitude())
                .append(",")
                .append(destinationAddress.getLatitude())
                .append(",")
                .append(destinationAddress.getLongitude())
                .append("");
        Log.e("findMe", "latLong = " + builder.toString());
        return builder.toString();
    }
}
