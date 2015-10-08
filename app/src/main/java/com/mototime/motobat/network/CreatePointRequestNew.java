package com.mototime.motobat.network;

import android.content.Context;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.NewPointSerializable;

import java.util.HashMap;


public class CreatePointRequestNew extends HTTPClient_new {
    public CreatePointRequestNew(/*AsyncTaskCompleteListener listener, */Context context, NewPointSerializable point, String memberGroup) {
        this.context = context;
        //this.listener = listener;
        post = new HashMap<>();
        MyApp myApp = (MyApp) context.getApplicationContext();
        post.put("method", "create");
        post.put("userid", myApp.getPreferences().getUserID());
        post.put("alignment", String.valueOf(point.getAlignment()));
        post.put("transport", String.valueOf(point.getTransport()));
        post.put("lat", String.valueOf(point.getLatLng().latitude));
        post.put("lng", String.valueOf(point.getLatLng().longitude));
        post.put("text", String.valueOf(point.getText()));
        post.put("memberGroup", memberGroup);
        //request(post);
        //execute(post);
    }
}
