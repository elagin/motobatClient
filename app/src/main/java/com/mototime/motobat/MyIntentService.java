package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mototime.motobat.content.police.NewPoint;
import com.mototime.motobat.network.CreatePointRequest;
import com.mototime.motobat.network.GetObjectsListRequest;
import com.mototime.motobat.network.GetPoliceListRequest;
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

    public static final  String ACTION_GET_POINT_LIST     = "com.mototime.motobat.action.GetPointList";
    public static final  String ACTION_GET_OBJECTS_LIST   = "com.mototime.motobat.action.GetObjectsList";
    public static final  String ACTION_CREATE_POINT       = "com.mototime.motobat.action.CreatePoint";
    public static final  String ACTION_GET_ROLE           = "com.mototime.motobat.action.GetRole";
    public static final  String ACTION_GET_USER_INFO_VK   = "com.mototime.motobat.action.GetUserInfoVK";
    public static final  String ACTION_IS_OPEN_MEMBER_VK  = "com.mototime.motobat.action.IsOpenMemberVK";
    public static final  String ACTION_IS_CLOSE_MEMBER_VK = "com.mototime.motobat.action.IsCloseMemberVK";
    public static final  String RESULT_CODE               = "result_code";
    public static final  int    RESULT_SUCCESS            = 0;
    public static final  int    RESULT_ERROR              = 1;
    public static final  String RESULT                    = "RESULT";
    private static final String USER_ID                   = "userID";
    private static final String USER_NAME                 = "userName";
    private static final String VERSION_NAME              = "versionName";
    private static final String ACCESS_TOKEN              = "access_token";
    private static final String GROUP_ID                  = "group_id";

    private static final String            POINT        = "point";
    private static final String            MEMBER_GROUP = "memberGroup";
    private final        BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);
    private              MyApp             myApp        = null;

    public MyIntentService() {
        super("MyIntentService");
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

    public static void startActionGetObjectsList(Context context) {
        Intent intent = newIntent(context, ACTION_GET_OBJECTS_LIST);
        context.startService(intent);
    }

    public static void startActionGetRole(Context context,
                                          String userID,
                                          String userName,
                                          String versionName) {
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

    public static void startActionIsOpenMemberVKRequest(Context context, String token) {
        Intent intent = newIntent(context, ACTION_IS_OPEN_MEMBER_VK);
        intent.putExtra(ACCESS_TOKEN, token);
        intent.putExtra(GROUP_ID, MyApp.OPEN_GROUP_ID);
        context.startService(intent);
    }

    private static void startActionIsCloseMemberVKRequest(Context context, String token) {
        Intent intent = newIntent(context, ACTION_IS_CLOSE_MEMBER_VK);
        intent.putExtra(ACCESS_TOKEN, token);
        intent.putExtra(GROUP_ID, MyApp.CLOSE_GROUP_ID);
        context.startService(intent);
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        JSONObject   response;
        switch (action) {
            case ACTION_CREATE_POINT:
                response = handleActionCreatePoint(intent);
                break;
            case ACTION_GET_POINT_LIST:
                response = handleActionGetPointList();
                break;
            case ACTION_GET_OBJECTS_LIST:
                response = handleActionGetObjectsList();
                break;
            case ACTION_GET_ROLE:
                response = handleRoleRequest(intent);
                break;
            case ACTION_GET_USER_INFO_VK:
                response = handleGetUserInfoVKRequest(intent);
                break;
            case ACTION_IS_OPEN_MEMBER_VK:
                response = handleIsMemberVKRequest(intent);
                break;
            case ACTION_IS_CLOSE_MEMBER_VK:
                response = handleIsMemberVKRequest(intent);
                break;
            default:
                response = new JSONObject();
        }
//        if (RequestStatus.parse(response) != RequestStatus.OK) returnError(response, action);
        if (RequestErrors.isError(response) && RequestErrors.isVkError(response)) returnError(response, action);
//        if (RequestErrors.isVkError(response)) returnError(response, action);
        String additional = "";
        try {
            switch (action) {
                case ACTION_CREATE_POINT:
                    break;
                case ACTION_GET_POINT_LIST:
                    myApp.getPolicePoints().updatePointsList(response.getJSONArray(RESULT));
                    break;
                case ACTION_GET_OBJECTS_LIST:
                    myApp.getObjectsPoints().updatePointsList(response.getJSONArray(RESULT));
                    break;
                case ACTION_GET_ROLE:
                    if (response.getJSONObject(RESULT).has("role")) {
                        myApp.getSession().setRole(response.getJSONObject(RESULT).getString("role"));
                    } else {
                        myApp.getSession().setRole("readonly");
                    }
                    break;
                case ACTION_GET_USER_INFO_VK:
                    JSONArray resArr = (JSONArray) response.get("response");
                    JSONObject resp = (JSONObject) resArr.get(0);
                    final String userName = resp.getString("first_name") + " " + resp.getString("last_name");
                    myApp.getSession().setUserName(userName);
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    additional = new JSONObject().put("userName", userName).put("versionName", pInfo.versionName).toString();
                    break;
                case ACTION_IS_OPEN_MEMBER_VK:
                    boolean isMember = (response.getInt("response") != 0);
                    myApp.getSession().setIsOpenMember(isMember);
                    if (isMember) startActionIsCloseMemberVKRequest(this, myApp.getPreferences().getVkToken());
                    break;
                case ACTION_IS_CLOSE_MEMBER_VK:
                    myApp.getSession().setIsCloseMember(response.getInt("response") != 0);
                    break;
            }
            mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCESS, additional);
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, e.getLocalizedMessage());
        }
    }

    private void returnError(JSONObject response, String action) {
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }

    private JSONObject handleActionCreatePoint(Intent intent) {
        final NewPoint point = (NewPoint) intent.getSerializableExtra(POINT);
        final String   group = intent.getStringExtra(MEMBER_GROUP);
        return new CreatePointRequest(this, point, group).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleActionGetPointList() {
        return new GetPoliceListRequest(this).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleActionGetObjectsList() {
        return new GetObjectsListRequest(this).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleRoleRequest(Intent intent) {
        final String userID      = intent.getStringExtra(USER_ID);
        final String userName    = intent.getStringExtra(USER_NAME);
        final String versionName = intent.getStringExtra(VERSION_NAME);
        return new RoleRequest(this, userID, userName, versionName).request(myApp.getPreferences().getServerURI());
    }

    private JSONObject handleGetUserInfoVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN);
        return new GetUserInfoVKRequest(this, access_token).request();
    }

    private JSONObject handleIsMemberVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN);
        final String group_id     = intent.getStringExtra(GROUP_ID);
        return new IsMemberVKRequest(this, access_token, group_id).request();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
