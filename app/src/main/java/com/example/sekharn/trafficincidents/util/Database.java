package com.example.sekharn.trafficincidents.util;

import android.os.SystemClock;
import android.util.Log;

public class Database {

    private static final String TAG = Database.class.getSimpleName();

    public static String readValue() {
        SystemClock.sleep(50);

        for (int i = 0; i < 100; i++) {
            Log.i(TAG, String.format("Reading value: %d%%", i));
            SystemClock.sleep(20);
        }
        Log.i(TAG, "Reading value: 100%");

        return "String from Database";
    }
}