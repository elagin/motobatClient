package com.mototime.motobat.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetPointListRequest extends HTTPClient  {

    public GetPointListRequest (AsyncTaskCompleteListener listener, Context context) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        post.put("method", "getlist");
        execute(post);
    }
}
