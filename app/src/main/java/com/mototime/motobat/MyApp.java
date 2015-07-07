package com.mototime.motobat;

import android.app.Application;

/**
 * Created by pavel on 07.07.15.
 */
public class MyApp extends Application {

    private final MyApp instance;

    public MyApp() {
        instance = this;
    }
}
