package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by pavel on 08.10.15.
 */
public class GetPointListRequestNew  extends HTTPClient_new {
    public GetPointListRequestNew(Context context) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "getlist");
    }
}
