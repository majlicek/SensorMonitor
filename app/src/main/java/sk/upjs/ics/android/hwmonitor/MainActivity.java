package sk.upjs.ics.android.hwmonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private ListView zoznam;
    private ArrayList<DoubleListItem> naZobrazenie;
    private List senzor;
    private List<Sensor> deviceSensors;
    private SenzorAdapter adapter;
    private int poloha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            System.out.println("Mam ulozeny stav");
            senzor = (List) savedInstanceState.get("ulozenyStav");
            System.out.println(senzor + " blabla");
        } else {
            System.out.println("nemam nic ulozene");
            senzor = (List) getIntent().getSerializableExtra("SENSOR");
            if (senzor != null) {
                System.out.println(senzor.toString());
            } else {
                senzor = new ArrayList<>();
            }
        }
        zoznam = (ListView) findViewById(R.id.listView2);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        naZobrazenie = new ArrayList<>();
        for (int i = 0; i < deviceSensors.size(); i++) {
            for (int j = 0; j < senzor.size(); j++) {
                if (deviceSensors.get(i).getName().equals(senzor.get(j)))
                    naZobrazenie.add(new DoubleListItem(deviceSensors.get(i).getName(), deviceSensors.get(i).getMaximumRange()));
            }
        }
        adapter = new SenzorAdapter(this, naZobrazenie);

        zoznam.setAdapter(adapter);


        zoznam.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String meno = adapter.getItem(i).getMeno();
                poloha = i;
                new AlertDialog.Builder(MainActivity.this).setTitle("Odstrániť")
                        .setMessage("Prajete si odobrat " + meno.replaceAll("PSH ", "") + " zo sledovaných?")
                        .setNegativeButton("Zrušiť", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.remove(adapter.getItem(poloha));
                                adapter.notifyDataSetChanged();
                                poloha = -1;
                            }
                        })
                        .show();
                return true;
            }
        });
        zoznam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailsSenzor.class);
                String meno = adapter.getItem(i).getMeno();;

                intent.putExtra("VYBRANY_SENZOR", meno);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HWMonitor.class);
                intent.putExtra("uzVybrane", (Serializable) senzor);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("ulozenyStav", new ArrayList(senzor));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String senzor = sensorEvent.sensor.getName();
        String vysledok = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getMeno().equals(senzor)) {
//                if (sensorEvent.values.length==1) {
                    if(sensorEvent.values[0] == (long) sensorEvent.values[0])
                        vysledok = vysledok + " " + String.format("%d",(long)sensorEvent.values[0]);
                    else
                        vysledok =  vysledok + " " + String.format("%.2f",sensorEvent.values[0]);
//                } else vysledok = "N/A";


                adapter.getItem(i).setHodnota(vysledok);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < deviceSensors.size(); i++) {
            for (int j = 0; j < senzor.size(); j++) {
                if (deviceSensors.get(i).getName().equals(senzor.get(j)))
//                    naZobrazenie.add(new DoubleListItem(deviceSensors.get(i).getName(), deviceSensors.get(i).getMaximumRange()));
                    mSensorManager.registerListener(this, deviceSensors.get(i), SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}