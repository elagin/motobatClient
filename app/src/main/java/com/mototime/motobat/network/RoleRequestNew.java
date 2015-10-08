package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class RoleRequestNew extends HTTPClient_new {
    public RoleRequestNew(Context context, String userID, String userName, String versionName) {
        this.context = context;

        post = new HashMap<>();
        post.put("method", "getRole");
        post.put("userid", userID);
        post.put("name", userName);
        post.put("versionName", versionName);
    }
}
