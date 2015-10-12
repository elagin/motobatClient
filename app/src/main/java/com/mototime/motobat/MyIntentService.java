package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.mototime.motobat.network.CreatePointRequestNew;
import com.mototime.motobat.network.GetPointListRequestNew;
import com.mototime.motobat.network.GetUserInfoVKRequestNew;
import com.mototime.motobat.network.RequestErrors;
import com.mototime.motobat.network.RoleRequestNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    MyApp myApp = null;

    private final static String CHARSET = "UTF-8";
    private final static String USERAGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    public static final String ERROR = "error";

    public static final String RECEIVER_TAG = "receiverTag";
    public static final String ACTION = "action";
    public static final String ACTION_GET_POINT_LIST = "com.mototime.motobat.action.GetPointList";
    public static final String ACTION_CREATE_POINT = "com.mototime.motobat.action.CreatePoint";
    public static final String ACTION_GET_ROLE = "com.mototime.motobat.action.GetRole";
    public static final String ACTION_GET_USER_INFO_VK = "com.mototime.motobat.action.GetUserInfoVK";

    public static final String RESUIL_CODE = "result_code";

    public final static int RESULT_SUCCSESS = 0;
    public final static int RESULT_ERROR = 1;

    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String VERSION_NAME = "versionName";
    public static final String ACCESS_TOKEN = "access_token";


    public static final String POINT = "point";
    public static final String MEMBER_GROUP = "memberGroup";

    public static final String RESULT = "RESULT";

    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

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

    public MyIntentService() {
        super("MyIntentService");
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
                        //new RoleRequest(new RoleCallback(), context, myApp.getPreferences().getUserID(), userName, versionName);
                        startActionGetRole(this, myApp.getPreferences().getUserID(), userName, versionName);
                        //mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
        JSONObject result = new CreatePointRequestNew(this, point, group).request(myApp.getPreferences().getServerURI());
        return result;
    }

    private JSONObject handleActionGetPointList() {
        JSONObject result = new GetPointListRequestNew(this).request(myApp.getPreferences().getServerURI());
        return result;
    }

    private JSONObject handleRoleRequest(Intent intent) {
        final String userID = intent.getStringExtra(USER_ID);
        final String userName = intent.getStringExtra(USER_NAME);
        final String versionName = intent.getStringExtra(VERSION_NAME);
        return new RoleRequestNew(this, userID, userName, versionName).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleGetUserInfoVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN);
        return new GetUserInfoVKRequestNew(this, access_token).request();
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
