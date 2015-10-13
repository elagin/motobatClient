package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.mototime.motobat.network.CreatePointRequest;
import com.mototime.motobat.network.GetPointListRequest;
import com.mototime.motobat.network.GetUserInfoVKRequest;
import com.mototime.motobat.network.IsMemberVKRequest;
import com.mototime.motobat.network.RequestErrors;
import com.mototime.motobat.network.RoleRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    MyApp myApp = null;

    public static final String ACTION = "action";
    public static final String ACTION_GET_POINT_LIST = "com.mototime.motobat.action.GetPointList";
    public static final String ACTION_CREATE_POINT = "com.mototime.motobat.action.CreatePoint";
    public static final String ACTION_GET_ROLE = "com.mototime.motobat.action.GetRole";
    public static final String ACTION_GET_USER_INFO_VK = "com.mototime.motobat.action.GetUserInfoVK";
    public static final String ACTION_IS_OPEN_MEMBER_VK = "com.mototime.motobat.action.IsOpenMemberVK";
    public static final String ACTION_IS_CLOSE_MEMBER_VK = "com.mototime.motobat.action.IsCloseMemberVK";

    public static final String RESUIL_CODE = "result_code";

    public final static int RESULT_SUCCSESS = 0;
    public final static int RESULT_ERROR = 1;

    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String VERSION_NAME = "versionName";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String GROUP_ID = "group_id";

    public static final String POINT = "point";
    public static final String MEMBER_GROUP = "memberGroup";

    public static final String RESULT = "RESULT";

    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    public MyIntentService() {
        super("MyIntentService");
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    private static Intent newIntent(Context context, String action) {
        Intent res = new Intent(context, MyIntentService.class);
        res.setAction(action);
        return res;
    }

    public static void startActionCreatePoint(Context context, NewPoint point, String memberGroup) {
        Intent intent = newIntent(context, ACTION_CREATE_POINT);
        intent.putExtra(POINT, point);
        intent.putExtra(MEMBER_GROUP, memberGroup);
        context.startService(intent);
    }

    public static void startActionGetPointList(Context context) {
        Intent intent = newIntent(context, ACTION_GET_POINT_LIST);
        context.startService(intent);
    }

    public static void startActionGetRole(Context context, String userID, String userName, String versionName) {
        Intent intent = newIntent(context, ACTION_GET_ROLE);
        intent.putExtra(USER_ID, userID);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(VERSION_NAME, versionName);
        context.startService(intent);
    }

    public static void startActionGetUserInfoVKRequest(Context context, String token) {
        Intent intent = newIntent(context, ACTION_GET_USER_INFO_VK);
        intent.putExtra(ACCESS_TOKEN, token);
        context.startService(intent);
    }

    public static void startActionIsOpenMemberVKRequest(Context context, String token, String group_id) {
        Intent intent = newIntent(context, ACTION_IS_OPEN_MEMBER_VK);
        intent.putExtra(ACCESS_TOKEN, token);
        intent.putExtra(GROUP_ID, group_id);
        context.startService(intent);
    }

    public static void startActionIsCloseMemberVKRequest(Context context, String token, String group_id) {
        Intent intent = newIntent(context, ACTION_IS_CLOSE_MEMBER_VK);
        intent.putExtra(ACCESS_TOKEN, token);
        intent.putExtra(GROUP_ID, group_id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        Bundle bundle = new Bundle();
        bundle.putString(ACTION, action);

        switch (action) {
            case ACTION_CREATE_POINT:
                JSONObject response = handleActionCreatePoint(intent);
                if (RequestErrors.isError(response)) {
                    returnError(response, action);
                } else {
                    mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                }
                break;
            case ACTION_GET_POINT_LIST:
                JSONObject pointList = handleActionGetPointList();
                if (RequestErrors.isError(pointList)) {
                    returnError(pointList, action);
                } else {
                    try {
                        myApp.getPoints().updatePointsList(pointList.getJSONArray(RESULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                }
                break;
            case ACTION_GET_ROLE:
                JSONObject roleResponse = handleRoleRequest(intent);
                if (RequestErrors.isError(roleResponse)) {
                    returnError(roleResponse, action);
                } else {
                    String role = "readonly";
                    try {
                        JSONObject result = roleResponse.getJSONObject(RESULT);
                        role = result.getString("role");
                        myApp.getSession().setRole(role);
                        if (role != "readonly") {
                            //TODO Отобразить кнопку
                            //addPointBtn
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                }
                break;
            case ACTION_GET_USER_INFO_VK:
                JSONObject userInfo = handleGetUserInfoVKRequest(intent);
                try {
                    JSONArray resArr = (JSONArray) userInfo.get("response");
                    if (resArr != null) {
                        JSONObject resp = (JSONObject) resArr.get(0);
                        String userName = resp.getString("first_name") + " " + resp.getString("last_name");
                        myApp.getSession().setUserName(userName);

                        String versionName = "0";
                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            versionName = pInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        startActionGetRole(this, myApp.getPreferences().getUserID(), userName, versionName);
                        //mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_IS_OPEN_MEMBER_VK:
                JSONObject resultOpen = handleIsMemberVKRequest(intent);
                try {
                    Boolean isMember = (resultOpen.getInt("response") != 0);
                    myApp.getSession().setIsOpenMember(isMember);
                    if (isMember) {
                        startActionIsCloseMemberVKRequest(this, myApp.getPreferences().getVkToken(), myApp.CLOSE_GROUP_ID);
                        //new IsMemberVKRequest(new IsMemberCloseGroupVKCallback(), context, myApp.getPreferences().getVkToken(), myApp.CLOSE_GROUP_ID);
                    }
//                    else {
//                        getVKUserInfo();
//                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), e.getLocalizedMessage());
                    //Toast.makeText(context, "Ошибка при проверке членства в группе: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case ACTION_IS_CLOSE_MEMBER_VK:
                JSONObject resultClose = handleIsMemberVKRequest(intent);
//                        try {
                Boolean isMember = null;
                try {
                    isMember = (resultClose.getInt("response") != 0);
                    myApp.getSession().setIsCloseMember(isMember);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "Ошибка при проверке членства в группе: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                break;
        }
    }

    private void returnError(JSONObject response, String action) {
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }

    private JSONObject handleActionCreatePoint(Intent intent) {
        final NewPoint point = (NewPoint) intent.getSerializableExtra(POINT);
        final String group = intent.getStringExtra(MEMBER_GROUP);
        JSONObject result = new CreatePointRequest(this, point, group).request(myApp.getPreferences().getServerURI());
        return result;
    }

    private JSONObject handleActionGetPointList() {
        JSONObject result = new GetPointListRequest(this).request(myApp.getPreferences().getServerURI());
        return result;
    }

    private JSONObject handleRoleRequest(Intent intent) {
        final String userID = intent.getStringExtra(USER_ID);
        final String userName = intent.getStringExtra(USER_NAME);
        final String versionName = intent.getStringExtra(VERSION_NAME);
        return new RoleRequest(this, userID, userName, versionName).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleGetUserInfoVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN);
        return new GetUserInfoVKRequest(this, access_token).request();
    }

    private JSONObject handleIsMemberVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN);
        final String group_id = intent.getStringExtra(GROUP_ID);
        return new IsMemberVKRequest(this, access_token, group_id).request();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
