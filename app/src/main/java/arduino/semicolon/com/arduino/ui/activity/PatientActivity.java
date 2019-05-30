package arduino.semicolon.com.arduino.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import arduino.semicolon.com.arduino.R;
import arduino.semicolon.com.arduino.adapter.PatientAdapter;
import arduino.semicolon.com.arduino.database.DatabaseClient;
import arduino.semicolon.com.arduino.database.DatabaseEntity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static arduino.semicolon.com.arduino.util.Common.getStringFromEditText;
import static arduino.semicolon.com.arduino.util.Common.intent;
import static arduino.semicolon.com.arduino.util.Common.showCustomDialog;
import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_AGE;
import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_NAME;
import static arduino.semicolon.com.arduino.util.SharedPreferencesManger.SaveData;

public class PatientActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView patientsRecycler;
    private Toolbar toolbar;
    private FloatingActionButton addPatientFB;
    private PatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        initViews();
        setSupportActionBar(toolbar);

        setupRecyclerView();



        new GetPatients().execute();
        
    }

    private void setupRecyclerView() {
        patientsRecycler.setHasFixedSize(true);
        patientsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initViews() {
        patientsRecycler = findViewById(R.id.patient_rv);
        addPatientFB = findViewById(R.id.activity_patient_fb_addPatient);
        toolbar = findViewById(R.id.toolbar);

        addPatientFB.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.activity_patient_fb_addPatient:
                final View view = LayoutInflater.from(this)
                        .inflate(R.layout.input_dialog_layout, null);
                showCustomDialog(this, view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText patientName = view.findViewById(R.id.patient_name_et);
                        EditText patientAge = view.findViewById(R.id.patient_age_et);

                        String name = getStringFromEditText(patientName);
                        String age = getStringFromEditText(patientAge);

                        SaveData(PatientActivity.this, PATIENT_NAME, name);
                        SaveData(PatientActivity.this, PATIENT_AGE, age);

                        intent(PatientActivity.this, MainActivity.class);
                    }
                }).show();
                break;

        }

    }

    private class GetPatients extends AsyncTask<Void, Void, List<DatabaseEntity>>{

        @Override
        protected List<DatabaseEntity> doInBackground(Void... voids) {
            List<DatabaseEntity> patientsList = DatabaseClient.getInstance(PatientActivity.this)
                    .getAppDatabase().databaseDao().getAllPatients();
            return patientsList;
        }

        @Override
        protected void onPostExecute(List<DatabaseEntity> databaseEntities) {
            super.onPostExecute(databaseEntities);
            adapter = new PatientAdapter(getApplicationContext(), PatientActivity.this, databaseEntities);
            patientsRecycler.setAdapter(adapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_clear:
                adapter.clear();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
