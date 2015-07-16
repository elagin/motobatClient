package com.mototime.motobat;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.CreatePointRequest;
import com.mototime.motobat.network.RequestErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPoint implements AsyncTaskCompleteListener {

    private       int     alignment;
    private       int     transport;
    private       LatLng  latLng;
    private final Context context;
    private final MyApp myApp;

    public NewPoint(MyApp myApp) {
        this.context = myApp.getApplicationContext();
        this.myApp = myApp;
        setNormal();
        setGS();
    }

    public void setEvil() {
        alignment = Point.EVIL_POLICE;
    }

    public void setGood() {
        alignment = Point.GOOD_POLICE;
    }

    public void setNormal() {
        alignment = Point.NORMAL_POLICE;
    }

    public void setGS() {
        transport = Point.GS;
    }

    public void setRT() {
        transport = Point.RT;
    }

    public void setCar() {
        transport = Point.CAR;
    }

    public int getAlignment() {
        return alignment;
    }

    public int getTransport() {
        return transport;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void sendRequest() {
        new CreatePointRequest(this, context, this);
    }

    @Override
    public void onTaskComplete(JSONObject response) throws JSONException {
        if (RequestErrors.isError(response)) RequestErrors.showError(context, response);
        myApp.getPoints().requestPoints(myApp);
        myApp.updateMap(context);
    }
}
