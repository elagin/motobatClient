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
import android.widget.Button;
import android.widget.TextView;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.R;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.IsMemberVKRequest;
import com.mototime.motobat.utils.AnimateViews;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public Context context;
    private MyApp myApp = null;
    private Button loginBtn;
    private Button addPointBtn;
    private Button cancelButton;
    private TextView textNotify;

    private static String sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static String[] sMyScope = new String[]{VKScope.WALL};
    private final String appID = "4989462";
    private boolean inCreate;

    View leftCreateWizard, rightCreateWizard, bottomCreate, leftMain, notifyTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inCreate = true;
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();
        context = getApplicationContext();

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.root);

        leftCreateWizard = this.findViewById(R.id.create_left);
        rightCreateWizard = this.findViewById(R.id.create_right);
        bottomCreate = this.findViewById(R.id.create_bottom);
        leftMain = this.findViewById(R.id.main_left);
        notifyTop = findViewById((R.id.notify_top));
/*
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        addPointBtn = (Button) findViewById(R.id.add_point_btn);
        addPointBtn.setOnClickListener(this);
*/

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        addPointBtn = (Button) findViewById(R.id.create_wizard);
        addPointBtn.setOnClickListener(this);

        textNotify = (TextView) findViewById(R.id.text_notify);

        myApp.createMap(this);

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
//        if (!VKSdk.wakeUpSession())
//            VKSdk.authorize(sMyScope, true, true);
//        else
//            myApp.getSession().collectData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                if(myApp.getSession().isStandart()) {
                    AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                    AnimateViews.show(rightCreateWizard, AnimateViews.RIGHT);
                    AnimateViews.show(bottomCreate, AnimateViews.BOTTOM);
                    AnimateViews.hide(leftMain, AnimateViews.LEFT);
                    inCreate = true;
                } else {
                    showNotify("У Вас нет прав на создание точек.");
                }
                break;
            case R.id.cancel_button:
                AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                AnimateViews.hide(rightCreateWizard, AnimateViews.RIGHT);
                AnimateViews.hide(bottomCreate, AnimateViews.BOTTOM);
                AnimateViews.show(leftMain, AnimateViews.LEFT);
                inCreate = false;
                break;
//            case R.id.notify_button:
//                ObjectAnimator animateTop = ObjectAnimator.ofFloat(notifyTop, View.TRANSLATION_Y, 0, -notifyTop.getWidth());
//                animateTop.setDuration(200).start();
//                notifyButton.setText("");
//                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);

        if (!VKSdk.wakeUpSession())
            VKSdk.authorize(sMyScope, true, true);
        else {
            //myApp.getSession().collectData();
            new IsMemberVKRequest(new IsMemberVKCallback(), context, myApp.getPreferences().getVkToken());
        }
        myApp.getPoints().requestPoints(myApp);
        if (inCreate) {
            ViewTreeObserver vto = leftCreateWizard.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    leftCreateWizard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    leftCreateWizard.setTranslationX(-leftCreateWizard.getWidth());
                    rightCreateWizard.setTranslationX(rightCreateWizard.getWidth());
                    bottomCreate.setTranslationY(bottomCreate.getHeight());
                    inCreate = false;
                }
            });
        }
    }

    private class IsMemberVKCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                Boolean isMember = (result.getInt("response") != 0);
                myApp.getSession().setIsMember(isMember);
                if (isMember)
                    myApp.getPoints().requestPoints(myApp);
                else
                    showNotify("Вы не состоите в группе 'Moto Times'.\nЗагрузка точек не возможна.");
            } catch (JSONException e) {
                int a = 10;
            }
        }
    }

    private void showNotify(String text) {
        textNotify.setText(text);
        ObjectAnimator animateTop = ObjectAnimator.ofFloat(notifyTop, View.TRANSLATION_Y, -notifyTop.getWidth(), 0);
        animateTop.setDuration(500).start();
    }

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
            myApp.getSession().collectData();
        }

        // Вызывается после VKSdk.authorize, но до отображения окна VK.
        // Так что на этом этапе не понятно, авторизовался ли юзер успешно.
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //TODO верятно сохранять по новой не нужно, токен-то старый
            myApp.getPreferences().setUserID(token.userId);
            myApp.getPreferences().setVkToken(token.accessToken);
            myApp.getSession().collectData();
        }
    };
}
