package com.mototime.motobat.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavel on 11.07.15.
 */
public class GetPointListRequest extends HTTPClient  {

    public GetPointListRequest (AsyncTaskCompleteListener listener, Context context) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        post.put("method", "getlist");
        execute(post);
    }
}
