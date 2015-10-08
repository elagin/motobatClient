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
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_GET_POINT_LIST = "com.mototime.motobat.action.GetPointList";
    public static final String ACTION_CREATE_POINT = "com.mototime.motobat.action.CreatePoint";

    private static final String ACTION_BAZ = "com.mototime.motobat.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.mototime.motobat.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.mototime.motobat.extra.PARAM2";

    public static void startActionCreatePoint(Context context, NewPointSerializable point, String memberGroup) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_CREATE_POINT);
        intent.putExtra("point", point);
        intent.putExtra("memberGroup", memberGroup);
        context.startService(intent);
    }

    public static void startActionGetPointList(Context context, NewPointSerializable point, String memberGroup) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_GET_POINT_LIST);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
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
                bundle.putString("result", handleActionGetPointList());
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
