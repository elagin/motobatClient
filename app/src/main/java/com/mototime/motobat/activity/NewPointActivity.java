package com.mototime.motobat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.Point;
import com.mototime.motobat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class NewPointActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner manAcvititySpinner;
    Spinner vehicleTypeSpinner;
    Button createBtn;

    private enum ManAcvitityStatus {
        ACTIVE, NOT_ACTIVE, UNKNOWN,
    }

    private enum VehicleType {
        RT, GS, OTHER,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_point);

        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(this);

        manAcvititySpinner = (Spinner) findViewById(R.id.manAcvititySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.man_acvitity_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manAcvititySpinner.setAdapter(adapter);
        manAcvititySpinner.setOnItemSelectedListener(this);

        vehicleTypeSpinner = (Spinner) findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<CharSequence> vehicleTypeAdapter = ArrayAdapter.createFromResource(this, R.array.man_vehicle_spinner_array, android.R.layout.simple_spinner_item);
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
        vehicleTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_point, menu);
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        String aaa = parent.getSelectedItem().toString();
        createBtn.setEnabled(manAcvititySpinner.getSelectedItemPosition() != 0 && vehicleTypeSpinner.getSelectedItemPosition() != 0);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.createBtn:
                JSONObject result = createNew();
                try {
                    Point point = new Point(this, result);
                    ((MyApp) getApplicationContext()).points.addPoint(point);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("CreateNewPoint", true);
                    startActivity(intent);
                } catch (Point.PointException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    private JSONObject createNew() {
        JSONObject json = new JSONObject();
        try {
            json.put("lat", 55);
            json.put("lon", 37);
            json.put("id", 10);
            json.put("address", "ул. Ленина");
            json.put("created_date", new Date().getTime());
            json.put("owner", "UserName");
            json.put("owner_id", 22);
            json.put("descr", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
