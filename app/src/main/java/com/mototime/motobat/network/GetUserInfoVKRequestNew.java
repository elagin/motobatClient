package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class GetUserInfoVKRequestNew extends VKHTTPClientNew {
    public GetUserInfoVKRequestNew(Context context, String access_token) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "users.get ");
        post.put("access_token", access_token);
    }
}
