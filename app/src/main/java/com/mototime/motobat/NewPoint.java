package com.mototime.motobat;

import android.content.Context;
import android.widget.Toast;

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
    private String text;
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
        if(myApp.getSession().isCloseMember())
            new CreatePointRequest(this, context, this, myApp.CLOSE_GROUP_ID);
        else if(myApp.getSession().isOpenMember())
            new CreatePointRequest(this, context, this, myApp.OPEN_GROUP_ID);
        else
            Toast.makeText(context, "Вы не состоите в группе имеющей право создавать точки", Toast.LENGTH_LONG).show();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onTaskComplete(JSONObject response) throws JSONException {
        if (RequestErrors.isError(response)) RequestErrors.showError(context, response);
        myApp.getPoints().requestPoints(myApp);
        myApp.updateMap(context);
    }
}
