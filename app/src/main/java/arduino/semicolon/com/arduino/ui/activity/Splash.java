package arduino.semicolon.com.arduino.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sdsmdg.tastytoast.TastyToast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import arduino.semicolon.com.arduino.R;

import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_AGE;
import static arduino.semicolon.com.arduino.util.Constants.SharedPreferenceKeys.PATIENT_NAME;
import static arduino.semicolon.com.arduino.util.SharedPreferencesManger.SaveData;

public class Splash extends AppCompatActivity implements View.OnClickListener {
    Button newPatientBtn, showPatientsBtn;
    Intent in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        newPatientBtn = findViewById(R.id.new_patient);
        showPatientsBtn = findViewById(R.id.show_patients);

        newPatientBtn.setOnClickListener(this);
        showPatientsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.new_patient:
                //new patient fragment
                displayAlertDialog();

                break;
            case R.id.show_patients:
                //show patient fragment
                intent(PatientActivity.class, 2);
                break;
            default:
                break;
        }
    }

    private void displayAlertDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.input_dialog_layout, null);
        final EditText patientNameEt = view.findViewById(R.id.patient_name_et);
        final EditText patientAgeEt = view.findViewById(R.id.patient_age_et);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Fill in the patient information")
                .setTitle("New Patient")
                .setIcon(R.drawable.ic_person_add_black_24dp)
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String patientAge = patientAgeEt.getText().toString();
                        String patientName = patientNameEt.getText().toString();
                        if (TextUtils.isEmpty(patientName) || TextUtils.isEmpty(patientAge)) {
                            TastyToast.makeText(Splash.this, "Complete the information", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                            return;
                        }

                        intent(MainActivity.class, 0);
                        SaveData(Splash.this, PATIENT_NAME, patientName);
                        SaveData(Splash.this, PATIENT_AGE, patientAge);

                        TastyToast.makeText(Splash.this, "Success", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void intent(Class<?> cls, int id) {
        Intent in = new Intent(this, cls);
        in.putExtra("id", id);
        startActivity(in);
    }
}
