package com.mototime.motobat.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RoleRequest extends HTTPClient  {

    public RoleRequest(AsyncTaskCompleteListener listener, Context context, String userID, String userName) {
        this.listener = listener;
        this.context = context;

        post = new HashMap<>();
        post.put("method", "getRole");
        post.put("userid", userID);
        post.put("username", userName);
        execute(post);
    }
}
