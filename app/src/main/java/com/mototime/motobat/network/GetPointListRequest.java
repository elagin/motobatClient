package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class GetPointListRequest extends HTTPClient  {

    public GetPointListRequest(Context context) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "getlist");
    }
}
