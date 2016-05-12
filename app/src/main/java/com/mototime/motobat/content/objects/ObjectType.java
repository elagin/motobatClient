package com.mototime.motobat.content.objects;

import com.mototime.motobat.R;

/**
 * Created by rjhdby on 06.05.16.
 */
public enum ObjectType {
    CAMERA("CAMERA", R.drawable.camera_small, "Камера"),
    UNKNOWN("", R.drawable.camera_small, "НЁХ");
    public final String code;
    public final int    icon;
    public final String text;

    ObjectType(String code, int icon, String text) {
        this.code = code;
        this.icon = icon;
        this.text = text;
    }

    public static ObjectType parse(String code) {
        switch (code) {
            case "CAMERA":
                return CAMERA;
            default:
                return UNKNOWN;
        }
    }
}
