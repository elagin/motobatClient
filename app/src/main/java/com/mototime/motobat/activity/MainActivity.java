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

import com.mototime.motobat.MyApp;
import com.mototime.motobat.R;
import com.mototime.motobat.utils.AnimateViews;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public Context context;
    private MyApp myApp = null;
    private Button loginBtn;
    private Button addPointBtn;
    private Button cancelButton;

    private static String   sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static String[] sMyScope  = new String[]{VKScope.WALL};
    private final  String   appID     = "4989462";
    private boolean inCreate;

    View leftCreateWizard, rightCreateWizard, bottomCreate, leftMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inCreate = true;
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.root);

        leftCreateWizard = this.findViewById(R.id.create_left);
        rightCreateWizard = this.findViewById(R.id.create_right);
        bottomCreate = this.findViewById(R.id.create_bottom);
        leftMain = this.findViewById(R.id.main_left);
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
        myApp.createMap(this);

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        if (!VKSdk.wakeUpSession())
            VKSdk.authorize(sMyScope, true, true);
        else
            myApp.getSession().collectData();
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
        int            id = v.getId();
        switch (id) {
            case R.id.login_btn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.create_wizard:
                AnimateViews.show(leftCreateWizard, AnimateViews.LEFT);
                AnimateViews.show(rightCreateWizard, AnimateViews.RIGHT);
                AnimateViews.show(bottomCreate, AnimateViews.BOTTOM);
                AnimateViews.hide(leftMain, AnimateViews.LEFT);
                inCreate = true;
                break;
            case R.id.cancel_button:
                AnimateViews.hide(leftCreateWizard, AnimateViews.LEFT);
                AnimateViews.hide(rightCreateWizard, AnimateViews.RIGHT);
                AnimateViews.hide(bottomCreate, AnimateViews.BOTTOM);
                AnimateViews.show(leftMain, AnimateViews.LEFT);
                inCreate = false;
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            myApp.getPreferences().setUserID(token.userId);
            myApp.getPreferences().setVkToken(token.accessToken);
            myApp.getSession().collectData();
        }
    };
}
