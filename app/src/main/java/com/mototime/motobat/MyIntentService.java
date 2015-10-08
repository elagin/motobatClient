package com.mototime.motobat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.mototime.motobat.network.CreatePointRequestNew;
import com.mototime.motobat.network.GetPointListRequestNew;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {


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
        if (intent != null) {
            ResultReceiver rec = intent.getParcelableExtra("receiverTag");
            //String recName= intent.getStringExtra("nameTag");
            final String action = intent.getAction();

            Bundle bundle = new Bundle();
            bundle.putString("action", action);
            if (ACTION_CREATE_POINT.equals(action)) {
                final NewPointSerializable newPointSerializable = (NewPointSerializable) intent.getSerializableExtra("point");
                final String memberGroup = intent.getStringExtra("memberGroup");
                bundle.putString("result", handleActionCreatePoint(newPointSerializable, memberGroup));
                rec.send(0, bundle);
            } else if (ACTION_GET_POINT_LIST.equals(action)) {
                String points = handleActionGetPointList();
                bundle.putString("result",points);
                        //bundle.putString("result", handleActionGetPointList());
                rec.send(0, bundle);
            } else {
                rec.send(0, null);
            }
        }
    }

    private String handleActionCreatePoint(NewPointSerializable param1, String param2) {
        CreatePointRequestNew createPointRequest = new CreatePointRequestNew(this, param1, param2);
        return createPointRequest.request().toString();
    }

    private String handleActionGetPointList() {
        GetPointListRequestNew getPointListRequestNew = new GetPointListRequestNew(this);
        return getPointListRequestNew.request().toString();
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
