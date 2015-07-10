package com.mototime.motobat.network;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavel on 09.07.15.
 */
public class RoleRequest extends HTTPClient  {

    private final String userID;

    public RoleRequest(AsyncTaskCompleteListener listener, Context context, String userID) {
        this.listener = listener;
        this.context = context;
        this.userID = userID;

        post = new HashMap<>();
        post.put("method", "getRole");
        post.put("userid", userID);
        execute(post);
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("result")) return true;
        try {
            String result = response.getString("result");
            if (result.equals("OK")) return false;
        } catch (JSONException e) {
            return true;
        }
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("result")) return "Ошибка соединения "  + response.toString();
        try {
            String result = response.getString("result");
            switch (result) {
                case "OK":
                    return "Статус изменен";
                case "ERROR PREREQUISITES":
                    return "Неизвестная ошибка " + response.toString();
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
