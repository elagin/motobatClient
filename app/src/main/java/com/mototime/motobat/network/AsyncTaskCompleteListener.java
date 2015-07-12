package com.mototime.motobat.network;

import org.json.JSONException;
import org.json.JSONObject;

public interface AsyncTaskCompleteListener {
    void onTaskComplete(JSONObject response) throws JSONException;
}
