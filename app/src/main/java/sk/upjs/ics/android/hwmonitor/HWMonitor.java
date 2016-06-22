package sk.upjs.ics.android.hwmonitor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HWMonitor extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mLight;
    private ListView zoznam;
    private ArrayAdapter<Sensor> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hwmonitor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        zoznam = (ListView) findViewById(R.id.listView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

//        String[] from = { mSensorManager.getSensorList(Sensor.TYPE_ALL) };
//        int[] to = { R.id.grid_item_text };
//        adapter = new SimpleCursorAdapter(this, R.layout.grid_item, NO_CURSOR, from, to, NO_FLAGS);

        listAdapter = new ArrayAdapter<Sensor>(this, android.R.layout.simple_list_item_checked, deviceSensors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView listItemView = (TextView) super.getView(position, convertView, parent);
                Sensor senzor = getItem(position);
//                if(task.isDone()) {
//                    listItemView.setPaintFlags(listItemView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                }
                listItemView.setText(senzor.getName().replaceAll("PSH ", ""));
                return listItemView;
            }

        };

        zoznam.setAdapter(listAdapter);


        List senzor = (List) getIntent().getSerializableExtra("uzVybrane");
        if (senzor != null) {
            for (int i = 0; i < senzor.size(); i++) {
                for (int j = 0; j < listAdapter.getCount(); j++) {
                    if (listAdapter.getItem(j).getName().equals(senzor.get(i)))
                        zoznam.setItemChecked(j, true);
                }

            }
        }

        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hwmonitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_all) {
            for (int j = 0; j < listAdapter.getCount(); j++) {
                zoznam.setItemChecked(j, true);
            }
        }
        if (id == R.id.action_nothing) {
            for (int j = 0; j < listAdapter.getCount(); j++) {
                zoznam.setItemChecked(j, false);
            }
        }
        if (id == R.id.addNewSensorMenu) {

            Intent intent = new Intent(this, MainActivity.class);
            List<String> checkedSensors = new ArrayList<>();
            SparseBooleanArray checkedItemPositions = zoznam.getCheckedItemPositions();
            for (int i = 0; i < checkedItemPositions.size(); i++) {
                if (checkedItemPositions.valueAt(i)) {
                    checkedSensors.add(listAdapter.getItem(checkedItemPositions.keyAt(i)).getName());
                }
            }
            intent.putExtra("SENSOR", (Serializable) checkedSensors);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
