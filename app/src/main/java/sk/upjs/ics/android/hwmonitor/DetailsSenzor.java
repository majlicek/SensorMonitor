package sk.upjs.ics.android.hwmonitor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.List;

/************
*Kniznica na grafy http://www.android-graphview.org/
*/

public class DetailsSenzor extends AppCompatActivity implements SensorEventListener {
    private Sensor senzor;
    private ArrayList<DoubleListItem> naZobrazenie;
    private ListView zoznam;
    private SensorManager mSensorManager;
    private DetailAdapter adapter;
    private boolean nastaveneHodnoty = false;
    private LineGraphSeries<DataPoint> series;
    private Runnable mTimer;
    private int pocitadlo = 0;
    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_senzor);
        String senzorNazov;
        if (savedInstanceState != null) {
            System.out.println("Mam ulozeny stav");
            senzorNazov = (String) savedInstanceState.get("ulozenyStavDetail");
            System.out.println(senzorNazov + " detail");
        } else {
            System.out.println("nemam nic ulozene");
            senzorNazov = getIntent().getStringExtra("VYBRANY_SENZOR");
            if (senzorNazov != null) {
                System.out.println(senzorNazov);
            } else {
                System.out.println("Ziadny senzor som nedostal");
            }
        }
        zoznam = (ListView) findViewById(R.id.listViewDetail);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i=0; i< deviceSensors.size(); i++){
            if (deviceSensors.get(i).getName().equals(senzorNazov)){
                this.senzor = deviceSensors.get(i);
                break;
            }
        }
        naZobrazenie = new ArrayList<>();
        naZobrazenie.add(new DoubleListItem("Názov:", senzor.getName()));
        naZobrazenie.add(new DoubleListItem("Výrobca:", senzor.getVendor()));
        naZobrazenie.add(new DoubleListItem("Verzia:", senzor.getVersion()));
        naZobrazenie.add(new DoubleListItem("Maximálny rozsah:", senzor.getMaximumRange()));
        naZobrazenie.add(new DoubleListItem("Spotreba batérie:", senzor.getPower() + " mA"));
//        naZobrazenie.add(new DoubleListItem("Počet parametrov:", senzor.getPower()));


        adapter = new DetailAdapter(this, naZobrazenie);
        zoznam.setAdapter(adapter);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        series1 = new LineGraphSeries<>();
        series2 = new LineGraphSeries<>();
        series1.setColor(Color.RED);
        series2.setColor(Color.GREEN);
        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(0);
//        graph.getViewport().setMaxX(1000);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.addSeries(series);
        graph.addSeries(series1);
        graph.addSeries(series2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_senzor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("ulozenyStavDetail", senzor.getName());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!nastaveneHodnoty){
            for (int i = 0; i < sensorEvent.values.length; i++){
//                if (sensorEvent.values[i]==0) continue;
                naZobrazenie.add(new DoubleListItem("Hodnota " + i+1 + ":", sensorEvent.values[i]));
            }
            nastaveneHodnoty = true;
        }
        for (int i = 0; i < sensorEvent.values.length; i++){
//            if (sensorEvent.values[i]==0) continue;
            adapter.getItem(i+5).setHodnota(sensorEvent.values[i]);
        }
        if (pocitadlo%10 == 0){
            for (int i = 0; i < sensorEvent.values.length; i++) {
                DataPoint point = new DataPoint(pocitadlo / 10, sensorEvent.values[i]);
                switch(i){
                    case 0:  series.appendData(point, true, 1000);
                        series.appendData(point, true, 1000);
                        break;
                    case 1:  series1.appendData(point, true, 1000);
                        series1.appendData(point, true, 1000);
                        break;
                    case 2:  series2.appendData(point, true, 1000);series2.appendData(point, true, 1000);

                        break;
                    default:
                        break;
                }
            }
        }

        pocitadlo++;


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, senzor, SensorManager.SENSOR_DELAY_NORMAL);
//        mTimer = new Runnable() {
//            @Override
//            public void run() {
//                graph2LastXValue += 1d;
//                DataPoint point = new DataPoint(pocitadlo, x);
//                series.appendData(point, true, 1000);
//            }
//        };

    }



    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
