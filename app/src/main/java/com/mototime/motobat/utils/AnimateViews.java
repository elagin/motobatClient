package com.mototime.motobat.utils;

import android.animation.ObjectAnimator;
import android.view.View;

public class AnimateViews {
    public static final byte TOP    = 0;
    public static final byte BOTTOM = 1;
    public static final byte LEFT   = 2;
    public static final byte RIGHT  = 3;

    private static void animate(View view, byte direction, boolean type) {
        ObjectAnimator animate;
        int            fromWidth, fromHeight, toWidth, toHeight;
        if (type) {
            fromWidth = 0;
            fromHeight = 0;
            toWidth = view.getWidth();
            toHeight = view.getHeight();
        } else {
            fromWidth = view.getWidth();
            fromHeight = view.getHeight();
            toWidth = 0;
            toHeight = 0;
        }
        switch (direction) {
            case TOP:
                animate = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -fromHeight, -toHeight);
                break;
            case BOTTOM:
                animate = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, fromHeight, toHeight);
                break;
            case RIGHT:
                animate = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, fromWidth, toWidth);
                break;
            case LEFT:
                animate = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -fromWidth, -toWidth);
                break;
            default:
                animate = null;
        }
        if (animate != null)
            animate.setDuration(200).start();
    }

    public static void hide(View view, byte direction) {
        animate(view, direction, true);
    }

    public static void hide(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void show(View view, byte direction) {
        animate(view, direction, false);
    }

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }
}
