package com.mototime.motobat.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavel on 09.07.15.
 */
public class RoleRequest extends HTTPClient  {

    private final String userID;

    public RoleRequest(AsyncTaskCompleteListener listener, Context context, String userID) {
        this.listener = listener;
        this.context = context;
        this.userID = userID;

        post = new HashMap<>();
        post.put("method", "getRole");
        post.put("userid", userID);
        execute(post);
    }
}
