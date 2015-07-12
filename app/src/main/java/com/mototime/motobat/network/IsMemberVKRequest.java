package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class IsMemberVKRequest extends VKHTTPClient {
    public IsMemberVKRequest(AsyncTaskCompleteListener listener, Context context, String access_token) {
        this.listener = listener;
        this.context = context;

        post = new HashMap<>();
        post.put("method", "groups.isMember");
        post.put("group_id", "68397238");
        post.put("access_token", access_token);
        execute(post);
    }
}
