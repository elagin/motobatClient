package com.mototime.motobat.content.police;

import com.google.android.gms.maps.model.LatLng;
import com.mototime.motobat.content.police.PointType;
import com.mototime.motobat.content.police.PoliceAlignment;

import java.io.Serializable;

import static com.mototime.motobat.content.police.PointType.POLICE_CAR;
import static com.mototime.motobat.content.police.PointType.POLICE_GS;
import static com.mototime.motobat.content.police.PointType.POLICE_RT;
import static com.mototime.motobat.content.police.PoliceAlignment.EVIL;
import static com.mototime.motobat.content.police.PoliceAlignment.GOOD;
import static com.mototime.motobat.content.police.PoliceAlignment.NEUTRAL;

public class NewPoint implements Serializable {

    private PoliceAlignment alignment;
    private PointType       transport;
    private double          lat;
    private double          lon;
    private String          text;

    public NewPoint() {
        setNormal();
        setGS();
    }

    public void setEvil() {
        alignment = EVIL;
    }

    public void setGood() {
        alignment = GOOD;
    }

    public void setNormal() {
        alignment = NEUTRAL;
    }

    public void setGS() {
        transport = POLICE_GS;
    }

    public void setRT() {
        transport = POLICE_RT;
    }

    public void setCar() {
        transport = POLICE_CAR;
    }

    public PoliceAlignment getAlignment() {
        return alignment;
    }

    public PointType getTransport() {
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
