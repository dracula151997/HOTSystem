package arduino.semicolon.com.arduino.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedabdelmeged.bluetoothmc.BluetoothMC;
import com.ahmedabdelmeged.bluetoothmc.ui.BluetoothDevices;
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates;
import com.ahmedabdelmeged.bluetoothmc.util.InputDataHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import arduino.semicolon.com.arduino.R;
import arduino.semicolon.com.arduino.database.DatabaseClient;
import arduino.semicolon.com.arduino.database.DatabaseEntity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_AGE;
import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_NAME;
import static arduino.semicolon.com.arduino.util.LoggerUtil.error;
import static arduino.semicolon.com.arduino.util.LoggerUtil.verbose;
import static arduino.semicolon.com.arduino.util.SharedPreferencesManger.LoadStringData;

public class MainActivity extends AppCompatActivity implements
        BluetoothMC.BluetoothConnectionListener,
        BluetoothMC.BluetoothErrorsListener,
        BluetoothMC.onDataReceivedListener {

    private static final String TAG = MainActivity.class.getName();
    private BluetoothMC bluetoothMC;
    private LineGraphSeries<DataPoint> esgGraphSeries;
    private GraphView heartBeatsGraph;
    private Toolbar toolbar;
    private TextView tempListTextView,
            tempAverageTextView, tempStateTextView,
            co2ListTextView, co2AverageTextView, co2StateTextView,
            pulseSenseTextView, pulseSenseAverage, pulseSenseState;
    private int id;
    private InputDataHelper dataHelper;

    PublishSubject<Double> esgGraphSubject;
    PublishSubject<Integer> tempSubject;
    PublishSubject<Double> co2Subject;
    PublishSubject<Double> pulseSenseSubject;

    String patientName, patientAge;

    private double lastXPoint = 0d;

    DatabaseEntity databaseEntity;

    private List<Double> tempList = new ArrayList<>();
    private List<Double> co2List = new ArrayList<>();
    private List<Double> pulseSenseList = new ArrayList<Double>();

    private double pulseAverge, co2Average, tempAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
        initView();

        initGraph();


        getDataFromSharedPreference();

        setBluetoothListeners();
        dataHelper = new InputDataHelper();

        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            if (getIntent().hasExtra("id")) {
                id = getIntent().getIntExtra("id", 0);
            }

            if (id > 0) {
                getPatientInfoById();
            }

        }

        databaseEntity = new DatabaseEntity();

        esgGraphSubject = PublishSubject.create();

        esgGraphSubject.subscribe(new Observer<Double>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Double integer) {
                Log.v(TAG, "ecg graph point: : " + integer);
                lastXPoint += 10d;
                esgGraphSeries.appendData(new DataPoint(lastXPoint, integer), false, 100 * 1000);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        tempSubject = PublishSubject.create();
        tempSubject.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                verbose("temp value: " + value);

                if (tempListTextView.getText() != null && tempList.size() <= 10) {
                    tempListTextView.setText("");
                    tempListTextView.setText(value + "");
                } else {
                    return;
                }

                String state = getState(value, "temp");
                tempStateTextView.setText(state);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        co2Subject = PublishSubject.create();
        co2Subject.subscribe(new Observer<Double>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Double co2Value) {
                verbose("co2 value: " + co2Value);

                if (co2ListTextView.getText() != null) {
                    co2ListTextView.setText(" ");
                    co2ListTextView.setText(co2Value + "");
                }

                co2Average = getCO2Average(co2List);

                co2StateTextView.setText(getState(co2Value, "co2"));


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        pulseSenseSubject = PublishSubject.create();
        pulseSenseSubject.subscribe(new Observer<Double>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Double values) {
                verbose("Heart rate: " + values);
                if (pulseSenseTextView.getText() != null) {
                    pulseSenseTextView.setText("");
                    pulseSenseTextView.setText(values + "");
                }

                pulseAverge = getCO2Average(pulseSenseList);

                pulseSenseState.setText(getState(pulseAverge, "heart rate"));


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        //check if the device have a bluetooth
        if (!bluetoothMC.isBluetoothAvailable())

        {
            showToast("The bluetooth device doesn't support by this device.");
        } else if (!bluetoothMC.isBluetoothEnabled())

        {
            bluetoothMC.enableBluetooth();
        }


    }

    private void getPatientInfoById() {
        new GetPatientInfoById().execute();

    }

    private void getDataFromSharedPreference() {
        patientName = LoadStringData(this, PATIENT_NAME);
        patientAge = LoadStringData(this, PATIENT_AGE);
    }

    private double getCO2Average(List<Double> list) {
        double avg = 0;
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
            avg = total / list.size();


        }

        return avg;

    }

    private String getState(double average, String type) {
        String state = "";
        switch (type) {
            case "co2":
                if (average >= 92 && average <= 95) {
                    state = "Safe";
                }
                break;
            case "temp":
                if (average >= 36.5 && average <= 37.5) {
                    state = "Normal";
                } else if (average == 37.5 || average == 38.3) {
                    state = "Fever";
                } else if (average < 35.0) {
                    state = "Hypothermia";

                }
                break;
            case "heart rate":
                if (average >= 60 && average <= 100) {
                    state = "Safe";

                }
                break;
        }

        return state;

    }

    private double getAverage(List<Double> list) {
        double avg = 0;
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
            avg = total / tempList.size();


        }

        return avg;
    }

    private void setBluetoothListeners() {
        bluetoothMC.setOnBluetoothErrorsListener(this);
        bluetoothMC.setOnBluetoothConnectionListener(this);
        bluetoothMC.setOnDataReceivedListener(this);
    }

    private void initBluetooth() {
        bluetoothMC = new BluetoothMC();
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

    private void initView() {
        heartBeatsGraph = findViewById(R.id._heart_beats_graphview);
        toolbar = findViewById(R.id.toolbar);

        tempListTextView = findViewById(R.id._temperture_text_view);
        tempStateTextView = findViewById(R.id._temperture_state_text_view);

        co2ListTextView = findViewById(R.id._co2_list_text);
        co2StateTextView = findViewById(R.id._co2_state_text);

        pulseSenseTextView = findViewById(R.id._pulse_sense_text_view);
        pulseSenseState = findViewById(R.id._pulse_sense_state_text_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BluetoothStates.REQUEST_CONNECT_DEVICE) {
            if (resultCode == RESULT_OK) {
                bluetoothMC.connect(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        if (id != 0) {
            menu.findItem(R.id.action_patients).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_connect:
                Intent in = new Intent(MainActivity.this, BluetoothDevices.class);
                startActivityForResult(in, BluetoothStates.REQUEST_CONNECT_DEVICE);
                break;

            case R.id.action_save_data:
                new SaveAsyncTask().execute();
                break;
            case R.id.action_reset:
                Intent intent2 = new Intent(this, GraphViewActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_patients:
                Intent intent = new Intent(this, PatientActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseEntity.setAge(patientAge);
            databaseEntity.setTempList(tempList);
            databaseEntity.setTempAverage(tempAverage);
            databaseEntity.setTempState(tempStateTextView.getText().toString());
            databaseEntity.setName(patientName);
            databaseEntity.setCo2List(co2List);
            databaseEntity.setAverage(co2Average);
            databaseEntity.setCo2State(co2StateTextView.getText().toString());
            databaseEntity.setPulseSenseList(pulseSenseList);
            databaseEntity.setPulseAverage(pulseAverge);
            databaseEntity.setPulseState(pulseSenseState.getText().toString());

            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase().databaseDao().insert(databaseEntity);

            verbose("Success");

            return null;
        }
    }


    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.input_dialog, null);
        builder.setView(view);

        final EditText editText = view.findViewById(R.id.add_value_edit);
        builder.setPositiveButton("Add Value", (dialog, which) -> {
            int temp = Integer.parseInt(editText.getText().toString());
            tempSubject.onNext(temp);
            dialog.dismiss();
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.show();


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceConnecting() {
        verbose("onDeviceConnection: connecting");

    }

    @Override
    public void onDeviceConnected() {
        verbose("onDeviceConnected: success");

    }

    @Override
    public void onDeviceDisconnected() {
        error("onDeviceDisconnected: disconnect");

    }

    @Override
    public void onDeviceConnectionFailed() {
        error("onDeviceConnectionFailed");

    }

    @Override
    public void onSendingFailed() {
        error("onSendingFailed");

    }

    @Override
    public void onReceivingFailed() {
        error("onReceivingFailed");

    }

    @Override
    public void onDisconnectingFailed() {
        error("onDisconnectingFailed");

    }

    @Override
    public void onCommunicationFailed() {
        error("onCommunicationFailed");

    }

    @Override
    public void onDataReceived(String data) {
        verbose("onDataReceived: " + data);

    /*    String[] datas = data.split("-");
        if (datas.length >= 3) {
            String temp = datas[0];
            String pulse = datas[1];
            String o2 = datas[3];


            if (!temp.equals("")) {
                tempSubject.onNext(Integer.valueOf(temp));
            }

            if (!pulse.equals("")) {
                pulseSenseSubject.onNext(Double.valueOf(pulse));
            }


            if (!o2.equals("")) {
                co2Subject.onNext(Double.valueOf(o2));
            }




        }*/

   /*     if (sensors.size() >= 3) {
            String tempSensor = sensors.get(0).trim();
            tempSubject.onNext(Integer.valueOf(tempSensor));
            verbose("Temp: " + tempSensor);

       *//*     String ecgSensor = sensors.get(0).trim();
            esgGraphSubject.onNext(Double.valueOf(ecgSensor));*//*

            String pulseSenseSensor = sensors.get(1).trim();
            pulseSenseSubject.onNext(Double.valueOf(pulseSenseSensor));
            verbose("pulse: " + pulseSenseSensor);


            String o2Sensor = sensors.get(2).trim();
            co2Subject.onNext(Double.valueOf(o2Sensor));
            verbose("o2: " + o2Sensor);


        }*/


        String[] esgPoints = data.trim().split(" ");

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
        }
    }

    public class GetPatientInfoById extends AsyncTask<Void, Void, List<DatabaseEntity>> {

        @Override
        protected List<DatabaseEntity> doInBackground(Void... voids) {
            List<DatabaseEntity> data = DatabaseClient.getInstance(MainActivity.this)
                    .getAppDatabase().databaseDao().getPatientById(id);
            return data;
        }

        @Override
        protected void onPostExecute(List<DatabaseEntity> entities) {
            super.onPostExecute(entities);
            for (DatabaseEntity entity : entities) {
                String age = entity.getAge();
                List<Double> co2List = entity.getCo2List();
                String co2State = entity.getCo2State();
                String name = entity.getName();
                List<Double> tempList = entity.getTempList();
                String tempState = entity.getTempState();
                tempListTextView.setText(tempList.toString());
                tempStateTextView.setText(tempState);
                co2StateTextView.setText(co2State);
                co2ListTextView.setText(co2List.toString());
                getSupportActionBar().setTitle(name + "info");
                getSupportActionBar().setSubtitle(age + "years old");

            }
        }
    }
}
