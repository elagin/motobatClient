package com.mototime.motobat.network;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestErrors {
    final static public String VALID_RESULT = "RESULT";
    final static public String INVALID_RESULT = "ERROR";

    final static public String PREREQUISITES = "PREREQUISITES";
    final static public String NOUSER = "NO USER";
    final static public String ALREADYINROLE = "ALREADY IN ROLE";
    final static public String NORIGHTS = "NO RIGHTS";
    final static public String UNKNOWNERROR = "UNKNOWN ERROR";

    public static String getErrorText(JSONObject response) {
        if (response.has(VALID_RESULT)) return "OK";
        if (!response.has(INVALID_RESULT)) return "Ошибка соединения " + response.toString();
        try {
            JSONObject errorJson = response.getJSONObject(INVALID_RESULT);
            String text = errorJson.getString("text");
            String object = errorJson.getString("object");
            switch (text) {
                case PREREQUISITES:
                    return "Ошибка в параметрах запроса " + object;
                case NOUSER:
                    return "Пользователь отсутствует";
                case ALREADYINROLE:
                    return "Роль уже назначена";
                case NORIGHTS:
                    return "Недостаточно прав";
                case UNKNOWNERROR:
                    return "Неизвестная ошибка";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }

    public static boolean isError(JSONObject response) {
        return response.has(RequestErrors.INVALID_RESULT);
    }
}
