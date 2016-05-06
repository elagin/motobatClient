package com.mototime.motobat.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rjhdby on 29.04.16.
 */
public enum RequestStatus {
    OK("OK"),
    PREREQUISITES("Ошибка в параметрах запроса"),
    NO_USER("Пользователь отсутствует"),
    ALREADY_IN_ROLE("Роль уже назначена"),
    NO_RIGHTS("Недостаточно прав"),
    UNKNOWN_ERROR("Неизвестная ошибка"),
    TIMEOUT("Создать новую точку можно будет через несколько минут"),
    CONNECT_ERROR("Ошибка соединения");

    private static final String VALID_VK_RESULT = "response";
    private static final String VALID_RESULT    = "RESULT";
    private static final String INVALID_RESULT  = "ERROR";

    public final String text;

    RequestStatus(String text) {
        this.text = text;
    }

    public static RequestStatus parse(JSONObject response) {
        if (response.has(VALID_RESULT) || response.has(VALID_VK_RESULT)) return OK;
        if (!response.has(INVALID_RESULT)) return CONNECT_ERROR;
        try {
            JSONObject errorJson = response.getJSONObject(INVALID_RESULT);
            String text = errorJson.getString("text");
//            String object = errorJson.getString("object");
            switch (text) {
                case "PREREQUISITES":
                    return PREREQUISITES;
                case "NO USER":
                    return NO_USER;
                case "ALREADY IN ROLE":
                    return ALREADY_IN_ROLE;
                case "NO RIGHTS":
                    return NO_RIGHTS;
                case "TIMEOUT":
                    return TIMEOUT;
                case "UNKNOWN ERROR":
                    return UNKNOWN_ERROR;
            }
        } catch (JSONException ignored) {

        }
        return UNKNOWN_ERROR;
    }
}
