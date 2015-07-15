package com.mototime.motobat;

public class NewPoint {
    public final static int NORMAL_POLICE = 1;
    public final static int GOOD_POLICE = 2;
    public final static int EVIL_POLICE = 3;

    public final static int GS = 1;
    public final static int RT = 2;
    public final static int CAR = 3;

    private int type;
    private int transport;

    public NewPoint() {
        setNormal();
        setGS();
    }

    public void setEvil() {
        type = EVIL_POLICE;
    }

    public void setGood() {
        type = GOOD_POLICE;
    }

    public void setNormal() {
        type = NORMAL_POLICE;
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
}
