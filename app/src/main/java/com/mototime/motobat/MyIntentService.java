package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.mototime.motobat.network.CreatePointRequestNew;
import com.mototime.motobat.network.GetPointListRequestNew;
import com.mototime.motobat.network.RequestErrors;
import com.mototime.motobat.network.RoleRequestNew;
import com.mototime.motobat.utils.Const;

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


    public static final int ACTION_GET_POINT_LIST_COMPLETE = 10;


    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String VERSION_NAME = "versionName";

    public static final String POINT = "point";
    public static final String MEMBER_GROUP = "memberGroup";

    public static final String RESULT = "RESULT";

    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    private static Intent newIntent(Context context, String action, MyResultReceiver receiver) {
        Intent res = new Intent(context, MyIntentService.class);
        res.setAction(action);
        res.putExtra(RECEIVER_TAG, receiver);
        return res;
    }

    public static void startActionCreatePoint(Context context, MyResultReceiver receiver, NewPoint point, String memberGroup) {
        Intent intent = newIntent(context, ACTION_CREATE_POINT, receiver);
        intent.putExtra(POINT, point);
        intent.putExtra(MEMBER_GROUP, memberGroup);
        context.startService(intent);
    }

    public static void startActionGetPointList(Context context, MyResultReceiver receiver) {
        Intent intent = newIntent(context, ACTION_GET_POINT_LIST, receiver);
        context.startService(intent);
    }

    public static void startActionGetRole(Context context, MyResultReceiver receiver, String userID, String userName, String versionName) {
        Intent intent = newIntent(context, ACTION_GET_ROLE, receiver);
        intent.putExtra(USER_ID, userID);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(VERSION_NAME, versionName);
        context.startService(intent);
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //mBroadcaster.broadcastIntentWithState(Const.STATE_ACTION_STARTED);

        ResultReceiver reciver = intent.getParcelableExtra(RECEIVER_TAG);
        //String recName= intent.getStringExtra("nameTag");
        final String action = intent.getAction();
        Bundle bundle = new Bundle();
        bundle.putString(ACTION, action);

        switch (action) {
            case ACTION_CREATE_POINT:
                JSONObject response = handleActionCreatePoint(intent);
                if (RequestErrors.isError(response)) {
                    returnError(response, bundle, reciver);
                } else {
                    reciver.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
                }
                break;
            case ACTION_GET_POINT_LIST:
                JSONObject pointList = handleActionGetPointList();
                if (RequestErrors.isError(pointList)) {
                    returnError(pointList, bundle, reciver);
                } else {
                    try {
                        myApp.getPoints().updatePointsList(pointList.getJSONArray(RESULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    reciver.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
                    mBroadcaster.broadcastIntentWithState(action, ACTION_GET_POINT_LIST_COMPLETE);
                }
                break;
            case ACTION_GET_ROLE:
                JSONObject roleResponse = handleRoleRequest(intent);
                if (RequestErrors.isError(roleResponse)) {
                    returnError(roleResponse, bundle, reciver);
                } else {
                    String role = "readonly";
                    JSONObject result = null;
                    try {
                        result = roleResponse.getJSONObject(RESULT);
                        role = result.getString("role");
                        myApp.getSession().setRole(role);
                        if (role != "readonly") {
                            //TODO Отобразить кнопку
                            //addPointBtn
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    reciver.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
                }
                break;
            default:
                reciver.send(MyResultReceiver.SUCCSESS_RESULT, null);
                break;
        }
        //mBroadcaster.broadcastIntentWithState(Const.STATE_ACTION_COMPLETE);
    }

    private void returnError(JSONObject response, Bundle bundle, ResultReceiver reciver) {
        bundle.putString(ERROR, RequestErrors.getError(response));
        reciver.send(MyResultReceiver.ERROR_RESULT, bundle);
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

    private JSONObject handleRoleRequest(Intent intent/*String userID, String userName, String versionName*/) {
        final String userID = intent.getStringExtra(USER_ID);
        final String userName = intent.getStringExtra(USER_NAME);
        final String versionName = intent.getStringExtra(VERSION_NAME);
        return new RoleRequestNew(this, userID, userName, versionName).request(myApp.getPreferences().getServerURI());
    }


    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
