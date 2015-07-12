package com.mototime.motobat.network;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestErrors {
    final static public String VALID_RESULT = "RESULT";
    final static public String INVALID_RESULT = "ERROR";

    final static public String PREREQUISITES = "PREREQUISITES";
    final static public String NO_USER = "NO USER";
    final static public String ALREADY_IN_ROLE = "ALREADY IN ROLE";
    final static public String NO_RIGHTS = "NO RIGHTS";
    final static public String UNKNOWN_ERROR = "UNKNOWN ERROR";

    public static String getError(JSONObject response) {
        if (response.has(VALID_RESULT)) return "OK";
        if (!response.has(INVALID_RESULT)) return "Ошибка соединения " + response.toString();
        try {
            JSONObject errorJson = response.getJSONObject(INVALID_RESULT);
            String text = errorJson.getString("text");
            String object = errorJson.getString("object");
            switch (text) {
                case PREREQUISITES:
                    return "Ошибка в параметрах запроса " + object;
                case NO_USER:
                    return "Пользователь отсутствует";
                case ALREADY_IN_ROLE:
                    return "Роль уже назначена";
                case NO_RIGHTS:
                    return "Недостаточно прав";
                case UNKNOWN_ERROR:
                    return "Неизвестная ошибка";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }

    public static boolean isError(JSONObject response) {
        return !response.has(RequestErrors.VALID_RESULT);
    }

    public static void showError(Context context, JSONObject response) {
        Toast.makeText(context, getError(response), Toast.LENGTH_LONG).show();
    }
}
