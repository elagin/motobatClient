package com.mototime.motobat.content.police;

import com.mototime.motobat.R;

/**
 * Created by rjhdby on 29.04.16.
 */
public enum PointType {
    POLICE_GS(1, R.drawable.map_gs, "Гусь"),
    POLICE_RT(2, R.drawable.map_rt, "RT"),
    POLICE_CAR(3, R.drawable.map_car, "Коробка"),
    CAMERA(10, R.drawable.camera_small, "Камера"),
    UNKNOWN(255, R.drawable.map_car, "Не известно");
    //TODO иконки
    public final int    type;
    public final int    icon;
    public final String text;

    PointType(int type, int icon, String text) {
        this.type = type;
        this.icon = icon;
        this.text = text;
    }

    public static PointType parse(int transport) {
        switch (transport) {
            case 1:
                return POLICE_GS;
            case 2:
                return POLICE_RT;
            case 3:
                return POLICE_CAR;
            case 10:
                return CAMERA;
            default:
                return UNKNOWN;
        }
    }
}
