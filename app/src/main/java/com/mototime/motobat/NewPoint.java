package com.mototime.motobat;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class NewPoint implements Serializable {

    private       int     alignment;
    private       int     transport;
    private       double  lat;
    private       double  lon;
    private String text;

    public NewPoint() {
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
        return new LatLng(lat, lon);
    }

    public void setLatLng(LatLng latLng) {
        this.lon = latLng.longitude;
        this.lat = latLng.latitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
