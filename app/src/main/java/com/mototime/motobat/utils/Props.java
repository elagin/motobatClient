package com.mototime.motobat.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by pavel on 09.07.15.
 */
public class Props {
    private AssetManager as = null;
    private final Properties summary = new Properties();
    private Properties global = new Properties();

    public Props(Context context) {
        as = context.getAssets();
        makeGlobal();
        makeSummary();
    }

    private void makeSummary() {
        summary.clear();
        summary.putAll(global);
    }

    private Properties read(InputStream is) {
        Reader reader;
        Properties current = new Properties();
        try {
            reader = new InputStreamReader(is, "UTF-8");
            current.load(reader);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return current;
    }

    private void makeGlobal() {
        InputStream is;
        try {
            String GLOBAL = "global.properties";
            is = as.open(GLOBAL);
            global = read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return summary.getProperty(key);
    }

    public List<String> keys() {
        return this.keys("");
    }

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public List<String> keys(String part) {
        List<String> keys = new ArrayList<>();
        Enumeration<Object> e = summary.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            if (key.matches("^" + part + ".*$")) {
                keys.add(key);
            }
        }
        return keys;
    }

    public boolean contains(String obj) {
        return summary.contains(obj);
    }

    public boolean containsKey(String key) {
        return summary.containsKey(key);
    }
}
