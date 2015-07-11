package com.mototime.motobat.activity;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private MyApp myApp = null;
    private Button loginBtn;
    private Button addPointBtn;
    private View pointList;
    private View mapContainer;
    public Context context;
    private Random rnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();

        setContentView(R.layout.activity_main);

        context = this;

        rnd = new Random();

        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        addPointBtn = (Button) findViewById(R.id.add_point_btn);
        addPointBtn.setOnClickListener(this);

        mapContainer = findViewById(R.id.map_container);

        myApp.createMap(this);
        //pointList = findViewById(R.id.point_list);
        testCreatePoints();
    }

    private void testCreatePoints() {
        double lat = 55.7522964477;
        double lon = 37.6227340698;

        JSONArray json = new JSONArray();

        for (int i = 0; i < 6; i++) {
            json.put(createNew(i, lat, lon));
            lat += 0.008;
            lon += (i * 0.002);
        }
        try {
            myApp.getPoints().parseJSON(json);
            myApp.updateMap(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject createNew(int id, double lat, double lon) {
        JSONObject json = new JSONObject();
        try {
            json.put("lat", lat);
            json.put("lon", lon);
            json.put("id", rnd.nextInt(100));
            json.put("address", myApp.getAddres(lat, lon));
            json.put("created_date", new Date().getTime());
            json.put("owner", "UserName");
            json.put("owner_id", 22);
            json.put("descr", "text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
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
        //drawList(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean("CreateNewPoint", false)) {
                myApp.updateMap(context);
            }
        }
    }

    private void drawList(Context context) {
        ViewGroup view = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);
        if (view.getChildCount() > 0)
            view.removeAllViews();
        boolean noYesterday = true;

        Integer[] visible = myApp.points.sort(myApp.points.getVisibleAccidents(), Points.Sort.BACKWARD);

//        if (points.error.equals("ok") || points.error.equals("no_new")) {
        for (int i : visible) {
            Point point = myApp.points.getPoint(i);
//                if (!acc.isToday() && noYesterday) {
//                    //inflateYesterdayRow(context, view);
//                    noYesterday = false;
//                }
            point.inflateRow(context, view);
        }
//            if (visible.length == 0) {
//                view.addView(noAccidentsNotification(context));
//            }
//        } else {
//            // TODO Сюда вкрячить сообщение об ошибке
//        }
    }
}
