package com.mototime.motobat.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.NewPoint;
import com.mototime.motobat.R;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.GetUserInfoVKRequest;
import com.mototime.motobat.network.IsMemberVKRequest;
import com.mototime.motobat.network.RequestErrors;
import com.mototime.motobat.network.RoleRequest;
import com.mototime.motobat.utils.AnimateViews;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static String   sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static String[] sMyScope  = new String[]{VKScope.WALL};
    private final  String   appID     = "4989462";
    public Context context;
    View leftCreateWizard, rightCreateWizard, bottomCreate, leftMain, notifyTop, targetView;
    ImageButton rt, gs, car, good, normal, evil, addPointBtn, cancelButton, okButton;
    private MyApp myApp = null;
    private TextView textNotify;
    private boolean  inCreate;
    private NewPoint newPoint;
    private VKSdkListener sdkListener = new VKSdkListener() {
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

        setContentView(R.layout.root);

        assignViews();
        assignButtons();
        newPoint = new NewPoint(myApp);
        myApp.createMap(this);

        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
//        if (!VKSdk.wakeUpSession())
//            VKSdk.authorize(sMyScope, true, true);
//        else
//            myApp.getSession().collectData();
    }

    private void assignButtons() {
        addPointBtn = (ImageButton) findViewById(R.id.create_wizard);
        okButton = (ImageButton) findViewById(R.id.ok_button);
        cancelButton = (ImageButton) findViewById(R.id.cancel_button);

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
        leftCreateWizard = this.findViewById(R.id.create_left);
        rightCreateWizard = this.findViewById(R.id.create_right);
        bottomCreate = this.findViewById(R.id.create_bottom);
        leftMain = this.findViewById(R.id.main_left);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_btn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.create_wizard:
                if (myApp.getSession().isStandart() && myApp.getSession().isMember()) {
                    AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                    AnimateViews.show(rightCreateWizard, AnimateViews.RIGHT);
                    AnimateViews.show(bottomCreate, AnimateViews.BOTTOM);
                    AnimateViews.hide(leftMain);
                    AnimateViews.show(targetView);
                    inCreate = true;
                    newPoint = new NewPoint(myApp);
                    evil.setAlpha(0.4f);
                    normal.setAlpha(1f);
                    good.setAlpha(0.4f);
                    gs.setAlpha(1f);
                    rt.setAlpha(0.4f);
                    car.setAlpha(0.4f);
                    newPoint.setNormal();
                    newPoint.setGS();
                } else {
                    //showNotify("У Вас нет прав на создание точек.");
                    //TODO по-хорошему надо выводить разные сообщения для прав и членства в группе.
                    Toast.makeText(context, "У Вас нет прав на создание точек.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ok_button:
                newPoint.setLatLng(myApp.getMap().getCenter());
                newPoint.sendRequest();
            case R.id.cancel_button:
                AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                AnimateViews.hide(rightCreateWizard, AnimateViews.RIGHT);
                AnimateViews.hide(bottomCreate, AnimateViews.BOTTOM);
                AnimateViews.show(leftMain);
                AnimateViews.hide(targetView);
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
            new IsMemberVKRequest(new IsMemberVKCallback(), this, myApp.getPreferences().getVkToken());
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

    private class IsMemberVKCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                Boolean isMember = (result.getInt("response") != 0);
                myApp.getSession().setIsMember(isMember);
                //               if (isMember) {
                new GetUserInfoVKRequest( new GetUserInfoCallback(), context, myApp.getPreferences().getVkToken());
                //new RoleRequest(new RoleCallback(), context, myApp.getPreferences().getUserID());
                myApp.getPoints().requestPoints(myApp);
//                } else
//                    showNotify("Вы не состоите в группе 'Moto Times'.\nЗагрузка точек не возможна.");
            } catch (JSONException e) {
                int a = 10;
            }
        }
    }

    private class RoleCallback implements AsyncTaskCompleteListener {

        @Override
        public void onTaskComplete(JSONObject response) {
            String role = "readonly";
            if (!RequestErrors.isError(response)) {
                try {
                    JSONObject result = response.getJSONObject("RESULT");
                    role = result.getString("role");
                    myApp.getSession().setRole(role);
                    if (role != "readonly") {
                        //TODO Отобразить кнопку
                        //addPointBtn
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetUserInfoCallback implements AsyncTaskCompleteListener {

        @Override
        public void onTaskComplete(JSONObject response) throws JSONException {
            JSONArray resArr = (JSONArray)response.get("response");
            if(resArr != null) {
                JSONObject resp = (JSONObject) resArr.get(0);
                String userName = resp.getString("first_name") + " " + resp.getString("last_name");
                myApp.getSession().setUserName(userName);
                new RoleRequest(new RoleCallback(), context, myApp.getPreferences().getUserID(), userName);
            }
        }
    }
}
