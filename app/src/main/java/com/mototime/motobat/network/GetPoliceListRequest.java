package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

public class GetPoliceListRequest extends HTTPClient  {

    public GetPoliceListRequest(Context context) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "getpolice");
    }
}
