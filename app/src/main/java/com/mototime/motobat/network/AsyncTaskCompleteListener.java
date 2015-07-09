package com.mototime.motobat.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pavel on 09.07.15.
 */
public interface AsyncTaskCompleteListener {
    void onTaskComplete(JSONObject result) throws JSONException;
}
