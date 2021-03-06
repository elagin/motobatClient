package com.mototime.motobat.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.MyIntentService;
import com.mototime.motobat.NewPoint;
import com.mototime.motobat.R;
import com.mototime.motobat.utils.AnimateViews;
import com.mototime.motobat.utils.Const;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static final String[] sMyScope = new String[]{VKScope.WALL};
    private final String appID = "4989462";

    private static final String CLASS_TAG = "MainActivity";

    private Context context;

    private View leftCreateWizard;
    private View rightCreateWizard;
    private View bottomCreate;
    private View leftMain;
    private View notifyTop;
    private View targetView;
    private ImageButton rt;
    private ImageButton gs;
    private ImageButton car;
    private ImageButton good;
    private ImageButton normal;
    private ImageButton evil;
    private MyApp myApp = null;
    private TextView textNotify;
    private boolean inCreate;
    private NewPoint newPoint;
    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(context, sTokenKey);
            myApp.getPreferences().setUserID(newToken.userId);
            myApp.getPreferences().setVkToken(newToken.accessToken);
            //myApp.getSession().collectData();
        }

        // Вызывается после VKSdk.authorize, но до отображения окна VK.
        // Так что на этом этапе не понятно, авторизовался ли юзер успешно.
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //TODO верятно сохранять по новой не нужно, токен-то старый
            myApp.getPreferences().setUserID(token.userId);
            myApp.getPreferences().setVkToken(token.accessToken);
            //myApp.getSession().collectData();
        }

        public void onRenewAccessToken(VKAccessToken token) {
            onReceiveNewToken(token);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.processActivityResult(VKSdk.VK_SDK_REQUEST_CODE, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        inCreate = true;
        myApp = (MyApp) getApplicationContext();
        context = getApplicationContext();

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(Const.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new ResponseStateReceiver
        ResponseStateReceiver mDownloadStateReceiver = new ResponseStateReceiver();

        // Registers the ResponseStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadStateReceiver, statusIntentFilter);
        setContentView(R.layout.main_screen_fragment);

        assignViews();
        assignButtons();
        newPoint = new NewPoint();
        myApp.createMap(this);

        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
//        if (!VKSdk.wakeUpSession())
//            VKSdk.authorize(sMyScope, true, true);
//        else
//            myApp.getSession().collectData();
    }

    private void assignButtons() {
        ImageButton addPointBtn = (ImageButton) findViewById(R.id.create_wizard);
        ImageButton okButton = (ImageButton) findViewById(R.id.ok_button);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button);

        good = (ImageButton) findViewById(R.id.good_police);
        evil = (ImageButton) findViewById(R.id.evil_police);
        normal = (ImageButton) findViewById(R.id.normal_police);

        gs = (ImageButton) findViewById(R.id.gs);
        rt = (ImageButton) findViewById(R.id.rt);
        car = (ImageButton) findViewById(R.id.car);

        addPointBtn.setOnClickListener(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        good.setOnClickListener(this);
        evil.setOnClickListener(this);
        normal.setOnClickListener(this);
        gs.setOnClickListener(this);
        rt.setOnClickListener(this);
        car.setOnClickListener(this);
    }

    private void assignViews() {
        leftCreateWizard = this.findViewById(R.id.alignment_type);
        rightCreateWizard = this.findViewById(R.id.create_vehicle_types);
        bottomCreate = this.findViewById(R.id.create_ok_cancel_edit);
        leftMain = this.findViewById(R.id.create_add);
        notifyTop = this.findViewById((R.id.notify_top));
        textNotify = (TextView) findViewById(R.id.text_notify);
        targetView = this.findViewById((R.id.target_view));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(context, AboutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        InputMethodManager imm;
        switch (id) {
            case R.id.create_wizard:
                if (myApp.getSession().isRO()) {
                    Toast.makeText(context, "Вам запрещено создавать точки", Toast.LENGTH_LONG).show();
                } else if (!myApp.getSession().isOpenMember()) {
                    Toast.makeText(context, "Вы не состоите в группе имеющей право создавать точки", Toast.LENGTH_LONG).show();
                } else {
                    AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                    AnimateViews.show(rightCreateWizard, AnimateViews.RIGHT);
                    AnimateViews.show(bottomCreate, AnimateViews.BOTTOM);
                    AnimateViews.hide(leftMain);
                    AnimateViews.show(targetView);
                    inCreate = true;
                    newPoint = new NewPoint();
                    evil.setAlpha(0.4f);
                    normal.setAlpha(1f);
                    good.setAlpha(0.4f);
                    gs.setAlpha(1f);
                    rt.setAlpha(0.4f);
                    car.setAlpha(0.4f);
                    newPoint.setNormal();
                    newPoint.setGS();
                }
                break;
            case R.id.ok_button:
                newPoint.setLatLng(myApp.getMap().getCenter());
                newPoint.setText(((TextView) findViewById(R.id.inputDescription)).getText().toString());

                if (myApp.getSession().isCloseMember()) {
                    MyIntentService.startActionCreatePoint(this, newPoint, MyApp.CLOSE_GROUP_ID);
                } else if (myApp.getSession().isOpenMember()) {
                    MyIntentService.startActionCreatePoint(this, newPoint, MyApp.OPEN_GROUP_ID);
                } else {
                    Toast.makeText(context, "Вы не состоите в группе имеющей право создавать точки", Toast.LENGTH_LONG).show();
                }

                ((TextView) findViewById(R.id.inputDescription)).setText("");
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.inputDescription).getWindowToken(), 0);
            case R.id.cancel_button:
                AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                AnimateViews.hide(rightCreateWizard, AnimateViews.RIGHT);
                AnimateViews.hide(bottomCreate, AnimateViews.BOTTOM);
                AnimateViews.show(leftMain);
                AnimateViews.hide(targetView);
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.inputDescription).getWindowToken(), 0);
                ((TextView) findViewById(R.id.inputDescription)).setText("");
                newPoint = null;
                inCreate = false;
                break;
            case R.id.good_police:
                evil.setAlpha(0.4f);
                normal.setAlpha(0.4f);
                good.setAlpha(1f);
                newPoint.setGood();
                break;
            case R.id.evil_police:
                evil.setAlpha(1f);
                normal.setAlpha(0.4f);
                good.setAlpha(0.4f);
                newPoint.setEvil();
                break;
            case R.id.normal_police:
                evil.setAlpha(0.4f);
                normal.setAlpha(1f);
                good.setAlpha(0.4f);
                newPoint.setNormal();
                break;
            case R.id.car:
                rt.setAlpha(0.4f);
                gs.setAlpha(0.4f);
                car.setAlpha(1f);
                newPoint.setCar();
                break;
            case R.id.gs:
                rt.setAlpha(0.4f);
                gs.setAlpha(1f);
                car.setAlpha(0.4f);
                newPoint.setGS();
                break;
            case R.id.rt:
                rt.setAlpha(1f);
                gs.setAlpha(0.4f);
                car.setAlpha(0.4f);
                newPoint.setRT();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        if (VKSdk.wakeUpSession()) {
            //myApp.getSession().collectData();
            MyIntentService.startActionIsOpenMemberVKRequest(this, myApp.getPreferences().getVkToken());
        } else {
            VKSdk.authorize(sMyScope, true, true);
        }

        if (inCreate) {
            ViewTreeObserver vto = leftCreateWizard.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    leftCreateWizard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    leftCreateWizard.setTranslationX(-leftCreateWizard.getWidth());
                    rightCreateWizard.setTranslationX(rightCreateWizard.getWidth());
                    bottomCreate.setTranslationY(bottomCreate.getHeight());
                    textNotify.setTranslationY(-textNotify.getHeight());
                    AnimateViews.hide(targetView);
                    AnimateViews.show(leftMain);
                    myApp.getMap().goToUser();
                    inCreate = false;
                }
            });
        }
    }

    private void showNotify(String text) {
        textNotify.setText(text);
        ObjectAnimator animateTop = ObjectAnimator.ofFloat(notifyTop, View.TRANSLATION_Y, -notifyTop.getWidth(), 0);
        animateTop.setDuration(500).start();
    }

    private void getVKUserInfo() {
        MyIntentService.startActionGetUserInfoVKRequest(this, myApp.getPreferences().getVkToken());
        MyIntentService.startActionGetPointList(this);
    }

    private class ResponseStateReceiver extends BroadcastReceiver {
        private ResponseStateReceiver() {
            // prevents instantiation by other packages.
        }

        /**
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent  The incoming broadcast Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(CLASS_TAG, "onReceive");
            int resultCode = intent.getIntExtra(MyIntentService.RESULT_CODE, 0);
            if (resultCode == MyIntentService.RESULT_SUCCSESS) {
                switch (intent.getStringExtra(Const.EXTENDED_OPERATION_TYPE)) {
                    case MyIntentService.ACTION_GET_POINT_LIST:
                        myApp.getMap().placePoints(myApp);
                        Toast.makeText(context, String.format("Загружено %d точек.", myApp.getPoints().getSize()), Toast.LENGTH_LONG).show();
                        break;
                    case MyIntentService.ACTION_CREATE_POINT:
                        MyIntentService.startActionGetPointList(context);
                        break;
                    case MyIntentService.ACTION_GET_ROLE:
                        break;
                    case MyIntentService.ACTION_IS_OPEN_MEMBER_VK:
                        if (!myApp.getSession().isOpenMember())
                            getVKUserInfo();
                        break;
                    case MyIntentService.ACTION_IS_CLOSE_MEMBER_VK:
                        getVKUserInfo();
//                            Toast.makeText(context, "Ошибка при проверке членства в группе: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        break;
                    case MyIntentService.ACTION_GET_USER_INFO_VK:
                        String userInfo = intent.getStringExtra(MyIntentService.RESULT);
                        if (userInfo != null) {
                            try {
                                JSONObject res = new JSONObject(userInfo);
                                String userName = res.getString("userName");
                                String versionName = res.getString("versionName");
                                MyIntentService.startActionGetRole(context, myApp.getPreferences().getUserID(), userName, versionName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else if (resultCode == MyIntentService.RESULT_ERROR) {
                String error = intent.getStringExtra(MyIntentService.RESULT);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "В onReceiveResult пришло не понятно что.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
