package com.mototime.motobat.content.police;

/**
 * Created by rjhdby on 29.04.16.
 */
public enum PoliceAlignment {
    GOOD(2, "добрый"),
    NEUTRAL(1, "нейтральный"),
    EVIL(3, "злой");

    public final int    alignment;
    public final String text;

    PoliceAlignment(int alignment, String text) {
        this.alignment = alignment;
        this.text = text;
    }

    public static PoliceAlignment parse(int alignment) {
        switch (alignment) {
            case 1:
                return NEUTRAL;
            case 2:
                return GOOD;
            case 3:
                return EVIL;
            default:
                return NEUTRAL;
        }
    }
}
