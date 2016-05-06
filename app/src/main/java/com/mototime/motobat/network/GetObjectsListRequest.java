package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class GetObjectsListRequest extends HTTPClient {

    public GetObjectsListRequest(Context context) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "getobjects");
    }
}
