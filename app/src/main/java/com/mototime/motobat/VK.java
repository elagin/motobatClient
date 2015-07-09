package com.mototime.motobat;

import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

/**
 * Created by pavel on 09.07.15.
 */
public class VK {

    private final String MOTO_TIMES_GROUP_ID = "68397238";

    public void getVKUserId() {
        VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                Log.d("User name", user.first_name + " " + user.last_name);
            }
        });
    }


    public void checkIsMember() {
        VKParameters params = new VKParameters();
        params.put("gid", MOTO_TIMES_GROUP_ID);
        VKApi.groups().isMember(params).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                int isMember = (int) response.parsedModel;
                isMember = 10;
            }
        });
    }

    public void getGroups() {
        VKParameters params = new VKParameters();
        params.put("group_id", MOTO_TIMES_GROUP_ID);
        params.put("fields", "description, members_count");
        VKApi.groups().getById(params).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiGroups groups = ((VKApiGroups) response.parsedModel)/*.get(0)*/;
            }
        });
    }

    public String getGroupID() { return MOTO_TIMES_GROUP_ID; }
}
