package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.mototime.motobat.network.CreatePointRequestNew;
import com.mototime.motobat.network.GetPointListRequestNew;
import com.mototime.motobat.network.RequestErrors;

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


    //private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_GET_POINT_LIST = "com.mototime.motobat.action.GetPointList";
    public static final String ACTION_CREATE_POINT = "com.mototime.motobat.action.CreatePoint";

    public static void startActionCreatePoint(Context context, NewPointSerializable point, String memberGroup) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_CREATE_POINT);
        intent.putExtra("point", point);
        intent.putExtra("memberGroup", memberGroup);
        context.startService(intent);
    }

    public static void startActionGetPointList(Context context, MyResultReceiver receiver) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_GET_POINT_LIST);
        intent.putExtra("receiverTag", receiver);
        context.startService(intent);
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver rec = intent.getParcelableExtra("receiverTag");
        //String recName= intent.getStringExtra("nameTag");
        final String action = intent.getAction();


        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        if (ACTION_CREATE_POINT.equals(action)) {
            final NewPointSerializable newPointSerializable = (NewPointSerializable) intent.getSerializableExtra("point");
            final String memberGroup = intent.getStringExtra("memberGroup");
            JSONObject response = handleActionCreatePoint(newPointSerializable, memberGroup);
            rec.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
        } else if (ACTION_GET_POINT_LIST.equals(action)) {
            JSONObject response = handleActionGetPointList();
            if (RequestErrors.isError(response)) {
                bundle.putString(ERROR, RequestErrors.getError(response));
                rec.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
            }
            else {
                try {
                    MyApp myApp = (MyApp) getApplicationContext();
                    myApp.getPoints().updatePointsList(response.getJSONArray("RESULT"));
                    myApp.getMap().placePoints(myApp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //bundle.putString(RESPONSE_HAVE_ERROR, points.toString());
            }
            rec.send(MyResultReceiver.SUCCSESS_RESULT, bundle);
        } else {
            rec.send(MyResultReceiver.SUCCSESS_RESULT, null);
        }
    }

    private JSONObject handleActionCreatePoint(NewPointSerializable point, String group) {
        JSONObject result = new CreatePointRequestNew(this, point, group).request(myApp.getPreferences().getServerURI());
        return result;
    }

    private JSONObject handleActionGetPointList() {
        JSONObject result = new GetPointListRequestNew(this).request(myApp.getPreferences().getServerURI());
        return result;
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
