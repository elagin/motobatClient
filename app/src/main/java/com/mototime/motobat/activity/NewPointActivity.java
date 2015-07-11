package com.mototime.motobat.activity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mototime.motobat.MyApp;
import com.mototime.motobat.MyLocationManager;
import com.mototime.motobat.Point;
import com.mototime.motobat.R;
import com.mototime.motobat.network.AsyncTaskCompleteListener;
import com.mototime.motobat.network.CreatePointRequest;
import com.mototime.motobat.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class NewPointActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner manAcvititySpinner;
    private Spinner vehicleTypeSpinner;
    private Button createBtn;
    private Button backButton;
    private Button addressConfirmBtn;
    private TextView createWhere;
    private TextView mapAddress;
    private TextView pointDescr;
    private View mapPage;
    private View detailsPage;
    private GoogleMap map;
    private MyApp myApp = null;
    private NewPoint newPoint;
    private final int RADIUS = 1000;
    private Context context;
    private Random rnd;

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
        context = this;

        myApp = (MyApp) getApplicationContext();
        rnd = new Random();

        mapPage = findViewById(R.id.map_page);
        detailsPage = findViewById(R.id.detail_page);

        createBtn = (Button) findViewById(R.id.point_create_create);
        createBtn.setOnClickListener(this);

        addressConfirmBtn = (Button) findViewById(R.id.address_confirm_btn);
        addressConfirmBtn.setOnClickListener(this);

        backButton = (Button) findViewById(R.id.point_create_back);
        backButton.setOnClickListener(this);

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

        createWhere = (TextView) findViewById(R.id.create_where);
        mapAddress = (TextView) findViewById(R.id.map_address);
        pointDescr = (TextView) findViewById(R.id.create_point_descr);

        newPoint = new NewPoint();
        map = makeMap();
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
            case R.id.point_create_create:
                CreatePointRequest request = new CreatePointRequest(new CreatePointCallback(), context, myApp.getPreferences().getUserID());
                request.setLocation(newPoint.location);
                request.setAddress(newPoint.address);
                request.execute();
                break;
            case R.id.address_confirm_btn:
                newPoint.updateLocation(MyUtils.LatLngToLocation(map.getCameraPosition().target));
                goToDetails();
                break;
            case R.id.point_create_back:
                if (mapPage.getVisibility() == View.GONE)
                    goToMap();
                break;
        }
    }

    private void goToDetails() {
        mapPage.setVisibility(View.GONE);
        detailsPage.setVisibility(View.VISIBLE);
        createWhere.setText(newPoint.address);
        backButton.setEnabled(true);
    }

    private void goToMap() {
        mapPage.setVisibility(View.VISIBLE);
        detailsPage.setVisibility(View.GONE);
        backButton.setEnabled(false);
    }

    private GoogleMap makeMap() {
        GoogleMap map;
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mc_create_map_container);
        map = mapFragment.getMap();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MyUtils.LocationToLatLng(newPoint.location), 16));
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!myApp.getSession().isModerator()) {
            CircleOptions circleOptions = new CircleOptions().center(MyUtils.LocationToLatLng(newPoint.initialLocation)).radius(RADIUS).fillColor(0x20FF0000);
            map.addCircle(circleOptions);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition camera) {
                    String address = myApp.getAddres(camera.target.latitude, camera.target.longitude);
                    mapAddress.setText(address);
                    newPoint.address = address;
//                    Button mcCreateFineAddressConfirm = (Button) ((Activity) context).findViewById(R.id.mc_create_fine_address_confirm);
//                    if (newPoint.initialLocation != null) {
//                        double distance = MyUtils.LatLngToLocation(camera.target).distanceTo(newPoint.initialLocation);
//                        if (distance > RADIUS) {
//                            mcCreateFineAddressConfirm.setEnabled(false);
//                        } else {
//                            mcCreateFineAddressConfirm.setEnabled(true);
//                        }
//                    } else {
//                        mcCreateFineAddressConfirm.setEnabled(false);
//                    }
                }
            });
        }
        map.clear();
        for (int id : myApp.getPoints().getMap().keySet()) {
            final Point point = myApp.getPoints().getPoint(id);
            if (point.isInvisible()) continue;
            String title = point.getAddress();
            title += ", " + MyUtils.getIntervalFromNowInText(point.getCreated()) + " назад";

            float alpha;
            int age = (int) (((new Date()).getTime() - point.getCreated().getTime()) / 3600000);
            if (age < 2) {
                alpha = 1.0f;
            } else if (age < 6) {
                alpha = 0.5f;
            } else {
                alpha = 0.2f;
            }
            map.addMarker(new MarkerOptions().position(MyUtils.LocationToLatLng(point.getLocation())).title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.point)).alpha(alpha));
        }
        return map;
    }

    private class NewPoint {
        final Location initialLocation;
        final Date created;
        Location location;
        String address;
        String description;

        public NewPoint() {
            initialLocation = location = MyLocationManager.getLocation(context);
            created = new Date();
        }

        public void updateLocation(Location location) {
            this.location = location;
        }
    }

    private class CreatePointCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            try {
                //{"isError":"Ошибка соединения {\"RESULT\":{\"response\":\"ok\"}}"}
                //{"isError":"Ошибка соединения {\"ERROR\":{\"text\":\"PREREQUISITES\",\"object\":\"userid\"}}"}
                //{"RESULT":{"role":"standart"}}
                //{"ERROR":{"text":"NO USER","object":"rjhd"}}
                result = (JSONObject) result.get("RESULT");
            } catch (JSONException e) {
            }
        }
    }
}
