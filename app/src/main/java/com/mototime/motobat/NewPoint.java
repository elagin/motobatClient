package com.mototime.motobat;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.CreatePointRequest;
import com.mototime.motobat.network.RequestErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPoint implements AsyncTaskCompleteListener {
    public final static int NORMAL_POLICE = 1;
    public final static int GOOD_POLICE   = 2;
    public final static int EVIL_POLICE   = 3;

    public final static int GS  = 1;
    public final static int RT  = 2;
    public final static int CAR = 3;

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
        alignment = EVIL_POLICE;
    }

    public void setGood() {
        alignment = GOOD_POLICE;
    }

    public void setNormal() {
        alignment = NORMAL_POLICE;
    }

    public void setGS() {
        transport = GS;
    }

    public void setRT() {
        transport = RT;
    }

    public void setCar() {
        transport = CAR;
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
