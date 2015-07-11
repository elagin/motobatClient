package com.mototime.motobat.network;

import android.content.Context;
import android.location.Location;

import com.mototime.motobat.R;
import com.mototime.motobat.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class CreatePointRequest extends HTTPClient {

    public CreatePointRequest(AsyncTaskCompleteListener listener, Context context, String userId) {
        this.context = context;
        this.listener = listener;
        post = new HashMap<>();
        post.put("method", "create");
        post.put("userid", userId);
    }

    public void execute() {
        super.execute(post);
    }

    public void setLocation(Location location) {
        post.put("lat", String.valueOf(location.getLatitude()));
        post.put("lng", String.valueOf(location.getLongitude()));
    }

    public void setAddress(String address) {
        post.put("address", address);
    }

    public void setDescription(String description) {
        post.put("descr", description);
    }

    public void setCreated(Date created) {
        post.put("created", Const.dateFormat.format(created));
    }

    @Override
    public boolean error(JSONObject response) {
        if (!response.has("result")) return true;
        try {
            JSONObject result = response.getJSONObject("result");
            if (result.has("ID")) return false;
        } catch (JSONException e) {
            return true;
        }
        return true;
    }

    @Override
    public String getError(JSONObject response) {
        if (!response.has("result")) return "Ошибка соединения " + response.toString();
        try {
            if (response.getJSONObject("result").has("ID")) return "Сообщение отправлено";
            String result = response.getString("result");
            switch (result) {
                case "AUTH ERROR":
                    return "Вы не авторизованы";
                case "NO RIGHTS":
                case "READONLY":
                    return "Недостаточно прав";
                case "PROBABLY SPAM":
                    return "Нельзя создавать события так часто";
            }
        } catch (JSONException ignored) {

        }
        return "Неизвестная ошибка " + response.toString();
    }
}
