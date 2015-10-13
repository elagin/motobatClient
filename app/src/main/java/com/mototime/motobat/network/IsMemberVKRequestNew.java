package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class IsMemberVKRequestNew extends VKHTTPClientNew {
    public IsMemberVKRequestNew(Context context, String access_token, String groupId) {
        this.context = context;

        post = new HashMap<>();
        post.put("method", "groups.isMember");
        post.put("group_id", groupId);
        post.put("access_token", access_token);
    }
}
