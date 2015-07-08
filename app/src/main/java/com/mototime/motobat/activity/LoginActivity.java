package com.mototime.motobat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wallet.LineItem;
import com.mototime.motobat.MyApp;
import com.mototime.motobat.MyPreferences;
import com.mototime.motobat.R;
import com.mototime.motobat.utils.MyUtils;

import org.w3c.dom.Text;

public class LoginActivity extends ActionBarActivity {

    private Button logoutBtn;
    private Button loginBtn;
    private Button cancelBtn;

    private EditText login;
    private EditText password;
    private MyApp myApp = null;
    private MyPreferences prefs;

    private static Context context;

    private void enableLoginBtn() {
        loginBtn.setEnabled(login.getText().toString().length() > 0 && password.getText().toString().length() > 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyApp myApp = (MyApp) getApplicationContext();

        context = this;
        prefs = myApp.getPreferences();
        login = (EditText) findViewById(R.id.auth_login);
        login.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });

        password = (EditText) findViewById(R.id.auth_password);
        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableLoginBtn();
            }
        });

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
                prefs.resetAuth();
//                AccidentsGeneral.auth.logoff();
                fillCtrls();
            }
        });

        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isOnline(context)) {
                    MyApp myApp1 = (MyApp) getApplicationContext();
                    if (myApp1.getSession().auth(context, login.getText().toString(), password.getText().toString())) {
                        finish();
                    } else {
                        TextView authErrorHelper = (TextView) findViewById(R.id.auth_error_helper);
                        authErrorHelper.setText(R.string.auth_password_error);
                    }
                } else {
                    //TODO Перенести в ресурсы
                    Toast.makeText(context, R.string.auth_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView accListYesterdayLine = (TextView) findViewById(R.id.accListYesterdayLine);
        accListYesterdayLine.setMovementMethod(LinkMovementMethod.getInstance());

        fillCtrls();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void fillCtrls() {

        login.setText(prefs.getLogin());
        password.setText(prefs.getPassword());
        View accListYesterdayLine = findViewById(R.id.accListYesterdayLine);
        TextView roleView = (TextView)findViewById(R.id.role);

        //Авторизованы?
        if (myApp.getSession().isAuthorized()) {
            loginBtn.setEnabled(false);
            logoutBtn.setEnabled(true);
            accListYesterdayLine.setVisibility(View.GONE);
            String format = getString(R.string.auth_role);
            roleView.setText(String.format(format, myApp.getSession().getName(this)));
            roleView.setVisibility(View.VISIBLE);
            login.setEnabled(false);
            password.setEnabled(false);
        } else {
//        if (prefs.isAnonim()) {
            loginBtn.setEnabled(true);
            logoutBtn.setEnabled(false);
//            login.setEnabled(!anonim.isChecked());
//            password.setEnabled(!anonim.isChecked());
            accListYesterdayLine.setVisibility(View.VISIBLE);
            roleView.setVisibility(View.GONE);
            enableLoginBtn();
        }
    }
}
