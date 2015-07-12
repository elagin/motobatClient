package com.mototime.motobat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.Point;
import com.mototime.motobat.Points;
import com.mototime.motobat.R;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.GetPointListRequest;
import com.mototime.motobat.network.RequestErrors;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public Context context;
    private MyApp myApp = null;
    private Button loginBtn;
    private Button addPointBtn;

    private static String sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static String[] sMyScope = new String[]{VKScope.WALL};
    private final String appID = "4989462";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();

        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        addPointBtn = (Button) findViewById(R.id.add_point_btn);
        addPointBtn.setOnClickListener(this);

        myApp.createMap(this);

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        if(!VKSdk.wakeUpSession())
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
        int id = v.getId();
        switch (id) {
            case R.id.login_btn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.add_point_btn:
                startActivity(new Intent(this, NewPointActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApp.getPoints().requestPoints(myApp);
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
