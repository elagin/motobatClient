package com.mototime.motobat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.MyPreferences;
import com.mototime.motobat.R;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.RequestErrors;
import com.mototime.motobat.network.RoleRequest;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private static Context context;
    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;
    private MyApp myApp = null;
    private MyPreferences prefs;
    private static String[] sMyScope = new String[]{VKScope.WALL};

    private void enableLoginBtn() {
        //loginBtn.setEnabled(login.getText().toString().length() > 0 && password.getText().toString().length() > 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myApp = (MyApp) getApplicationContext();

        context = this;
        prefs = myApp.getPreferences();

        cancelBtn = (Button) findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutBtn = (Button) findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                //TODO Добавить запрос подтверждения на выход.
                VKSdk.logout();
                prefs.resetAuth();
//                AccidentsGeneral.auth.logoff();
                fillCtrls();
            }
        });

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(this);
//            @Override
//            public void onClick(View v) {
//                if (MyUtils.isOnline(context)) {
//                    MyApp myApp1 = (MyApp) getApplicationContext();
//                    if (myApp1.getSession().auth(context, login.getText().toString(), password.getText().toString())) {
//                        finish();
//                    } else {
//                        TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
//                        authErrorHelper.setText(R.string.auth_password_error);
//                    }
//                } else {
//                    //TODO Перенести в ресурсы
//                    Toast.makeText(context, R.string.auth_not_available, Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        TextView accListYesterdayLine = (TextView) findViewById(R.id.accListYesterdayLine);
        accListYesterdayLine.setMovementMethod(LinkMovementMethod.getInstance());

        //checkBundle();

        fillCtrls();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_button:
                //startActivity(new Intent(this, VKLoginActivity.class));
                VKSdk.authorize(sMyScope, true, true);
                break;
        }
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

    private void fillCtrls() {
        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView = (TextView) findViewById(R.id.role);
        TextView loggedStatus = (TextView) findViewById(R.id.auth_logged_status);
        TextView userNameView = (TextView) findViewById(R.id.user_name);

        String formatRole = getString(R.string.auth_role);
        String formatUserName = getString(R.string.auth_user_name);

//        String formatStatus = getString(R.string.auth_logged_status);

        if (VKSdk.instance() != null) {
            if (VKSdk.isLoggedIn()) {
                loginBtn.setEnabled(false);
                logoutBtn.setEnabled(true);

                //roleView.setText(String.format(formatRole, myApp.getSession().getRole()));
                String role = myApp.getSession().getRole();
                roleView.setText(String.format(formatRole, role));

                loggedStatus.setText(R.string.is_authorized);

                userNameView.setText(String.format(formatUserName, myApp.getSession().getName()));
                //finish();

            } else {
                loginBtn.setEnabled(true);
                logoutBtn.setEnabled(false);
                roleView.setText(String.format(formatRole, getString(R.string.auth_role_un_set)));
                loggedStatus.setText(R.string.is_not_authorized);
                //authorized
            }
        } else {
            loginBtn.setEnabled(true);
            roleView.setText(String.format(formatRole, getString(R.string.auth_role_un_set)));
            loggedStatus.setText(R.string.is_not_authorized);
        }

        //Авторизованы?
//        if (myApp.getSession().isAuthorized()) {
//            loginBtn.setEnabled(false);
//            logoutBtn.setEnabled(true);
//            accListYesterdayLine.setVisibility(View.GONE);
//            String format = getString(R.string.auth_role);
//            roleView.setText(String.format(format, myApp.getSession().getName(this)));
//            roleView.setVisibility(View.VISIBLE);
//            login.setEnabled(false);
//            password.setEnabled(false);
//        } else {
////        if (prefs.isAnonim()) {
//            loginBtn.setEnabled(true);
//            logoutBtn.setEnabled(false);
////            login.setEnabled(!anonim.isChecked());
////            password.setEnabled(!anonim.isChecked());
//            accListYesterdayLine.setVisibility(View.VISIBLE);
//            roleView.setVisibility(View.GONE);
//            enableLoginBtn();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Boolean isReceiveNewToken = bundle.getBoolean("ReceiveNewToken", false);
            Boolean isAcceptUserToken = bundle.getBoolean("AcceptUserToken", false);
            if (isReceiveNewToken || isAcceptUserToken) {
                String accessToken = bundle.getString("accessToken");
                String userId = bundle.getString("userId");
                myApp.getPreferences().setUserID(userId);
                myApp.getPreferences().setVkToken(accessToken);
                //new IsMemberVKRequest(new IsMemberVKCallback(), context, myApp.getPreferences().getVkToken());
                new RoleRequest(new RoleCallback(), context, userId);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            myApp.getSession().setRole(role);
            fillCtrls();
        }
    }

    private class IsMemberVKCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                //{"response":0}
                result = (JSONObject) result.get("response");
            } catch (JSONException e) {
            }
        }
    }
}
