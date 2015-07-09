package com.mototime.motobat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

public class VKLoginActivity extends Activity {

    private static final String TAG = "Kate.LoginActivity";
    private final String appID = "4989462";

    private static String sTokenKey = "VK_ACCESS_TOKEN";
    private static String[] sMyScope = new String[]{VKScope.FRIENDS, VKScope.WALL, VKScope.PHOTOS, VKScope.NOHTTPS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vklogin);

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));

        VKSdk.authorize(sMyScope, true, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
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
            new AlertDialog.Builder(VKLoginActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(VKLoginActivity.this, sTokenKey);
            Intent i = new Intent(VKLoginActivity.this, MainActivity.class);
            startActivity(i);
        }

        // Вызывается после VKSdk.authorize, но до отображения окна VK.
        // Так что на этом этапе не понятно, авторизовался ли юзер успешно.
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Intent i = new Intent(VKLoginActivity.this, MainActivity.class);
            startActivity(i);
        }
    };
}
