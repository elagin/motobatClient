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
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.GetPointListRequest;
import com.mototime.motobat.network.RequestErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public Context context;
    private MyApp myApp = null;
    private Button loginBtn;
    private Button addPointBtn;
    private View pointList;
    private View mapContainer;
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
        new GetPointListRequest(new GetPointListCallback(), context);
    }

    private void drawList(Context context) {
        ViewGroup view = (ViewGroup) ((Activity) context).findViewById(R.id.accListContent);
        if (view.getChildCount() > 0)
            view.removeAllViews();
        boolean noYesterday = true;

        Integer[] visible = myApp.points.sort(myApp.points.getVisibleAccidents(), Points.Sort.BACKWARD);

//        if (points.isError.equals("ok") || points.isError.equals("no_new")) {
        for (int i : visible) {
            final Point point = myApp.points.getPoint(i);
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

    private class GetPointListCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject response) {
            if (!RequestErrors.isError(response)) {
                try {
                    JSONArray result = response.getJSONArray("RESULT");
                    myApp.getPoints().parseJSON(result);
                    myApp.updateMap(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
