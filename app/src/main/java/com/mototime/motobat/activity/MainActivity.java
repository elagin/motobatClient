package com.mototime.motobat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.R;
import com.mototime.motobat.activity.NewPointActivity;


public class MainActivity extends Activity implements View.OnClickListener  {

    private MyApp myApp = null;
    private Button newPointBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplicationContext();

        setContentView(R.layout.activity_main);

        newPointBtn = (Button)findViewById(R.id.new_point_btn);
        newPointBtn.setOnClickListener(this);
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
            case R.id.new_point_btn:
                startActivity(new Intent(this, NewPointActivity.class));
                break;
        }
    }
}
