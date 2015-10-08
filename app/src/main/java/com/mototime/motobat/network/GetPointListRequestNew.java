package com.mototime.motobat.network;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by pavel on 08.10.15.
 */
public class GetPointListRequestNew  extends HTTPClient_new {
    public GetPointListRequestNew (/*AsyncTaskCompleteListener listener,*/ Context context) {
        this.context = context;
        //this.listener = listener;
        post = new HashMap<>();
        post.put("method", "getlist");
        //execute(post);
    }
}
