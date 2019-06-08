package arduino.semicolon.com.arduino.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmedabdelmeged.bluetoothmc.BluetoothMC;
import com.ahmedabdelmeged.bluetoothmc.ui.BluetoothDevices;
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import arduino.semicolon.com.arduino.R;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static arduino.semicolon.com.arduino.util.LoggerUtil.verbose;

public class GraphViewActivity extends AppCompatActivity implements
        BluetoothMC.onDataReceivedListener,
        BluetoothMC.BluetoothErrorsListener,
        BluetoothMC.BluetoothConnectionListener {

    List<Integer> ecgData = new ArrayList<>();

    PublishSubject<Double> esgGraphSubject;

    GraphView heartBeatsGraph;
    Toolbar toolbar;

    private BluetoothMC bluetoothMC;
    private LineGraphSeries<DataPoint> esgGraphSeries;
    private double lastXPoint = 1d;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        ButterKnife.bind(this);

        initView();

        initBluetooth();
        setSupportActionBar(toolbar);

        bluetoothMC.send("HOt SYstem");

        ecgData.add(450);
        ecgData.add(460);
        ecgData.add(470);
        ecgData.add(480);
        ecgData.add(800);
        ecgData.add(430);
        ecgData.add(470);
        ecgData.add(620);
        ecgData.add(500);
        ecgData.add(550);
        ecgData.add(510);
        ecgData.add(630);
        ecgData.add(950);
        ecgData.add(520);
        ecgData.add(740);

        random = new Random();




        initGraph();
        setBluetoothListeners();

        esgGraphSubject = PublishSubject.create();

        esgGraphSubject.subscribe(new Observer<Double>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Double aDouble) {
                lastXPoint += 10d;
                esgGraphSeries.appendData(new DataPoint(lastXPoint, aDouble), false, 100 * 1000);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        heartBeatsGraph = findViewById(R.id._heart_beats_graphview);
    }

    private void setBluetoothListeners() {
        bluetoothMC.setOnBluetoothErrorsListener(this);
        bluetoothMC.setOnBluetoothConnectionListener(this);
        bluetoothMC.setOnDataReceivedListener(this);


    }

    private void initGraph() {
        heartBeatsGraph.setTitle("ECG Graph");
        heartBeatsGraph.getViewport().setYAxisBoundsManual(true);
        heartBeatsGraph.getViewport().setMinY(0);
        heartBeatsGraph.getViewport().setMaxY(1000);

        heartBeatsGraph.getViewport().setXAxisBoundsManual(true);
        heartBeatsGraph.getViewport().setMinX(0);
        heartBeatsGraph.getViewport().setMaxX(500);


        heartBeatsGraph.getViewport().setScrollable(true);
        heartBeatsGraph.getViewport().setScrollableY(true);


        esgGraphSeries = new LineGraphSeries<>();
        esgGraphSeries.appendData(new DataPoint(0, 0), false, 100 * 1000);
        heartBeatsGraph.addSeries(esgGraphSeries);
    }

    private void initBluetooth() {
        bluetoothMC = new BluetoothMC();

    }

    @Override
    public void onDeviceConnecting() {

    }

    @Override
    public void onDeviceConnected() {
        verbose("onDeviceConnected");

    }

    @Override
    public void onDeviceDisconnected() {

    }

    @Override
    public void onDeviceConnectionFailed() {
        verbose("onDeviceConnectionFailed");

    }

    @Override
    public void onSendingFailed() {
        verbose("onSendingFailed");

    }

    @Override
    public void onReceivingFailed() {
        verbose("onReceivingFailed");

    }

    @Override
    public void onDisconnectingFailed() {

    }

    @Override
    public void onCommunicationFailed() {

    }

    @Override
    public void onDataReceived(String data) {
        verbose("onDataReceived: " + data);

     /*   int i = random.nextInt(ecgData.size());
        int integer = ecgData.get(i);*/

     for (Integer value: ecgData){
         esgGraphSubject.onNext((double) value);

     }




       /* String[] esgPoints = data.trim().split(" ");

        for (String esgData : esgPoints) {
            //check received data is empty or not
            if (!esgData.isEmpty() && TextUtils.isDigitsOnly(esgData)) {
                //if not empty, parse ESG values to double...
                double esgPoint = Double.parseDouble(esgData);
                //if ESG value is greater than 300, then draw it at ESG Graph view.
                verbose("point:" + esgPoint);
                //pass ESG value to esg observable and draw it on Graph view...
                esgGraphSubject.onNext(esgPoint);


            }
        }*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_connect) {
            Intent in = new Intent(GraphViewActivity.this, BluetoothDevices.class);
            startActivityForResult(in, BluetoothStates.REQUEST_CONNECT_DEVICE);

        }
        return super.onOptionsItemSelected(item);
    }
}
