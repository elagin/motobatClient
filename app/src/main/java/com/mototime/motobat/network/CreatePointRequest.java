package com.mototime.motobat.network;

import android.content.Context;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.NewPoint;

import java.util.HashMap;

public class CreatePointRequest extends HTTPClient {
    public CreatePointRequest(Context context, NewPoint point, String memberGroup) {
        this.context = context;
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
    }
}
