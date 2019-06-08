package arduino.semicolon.com.arduino.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import arduino.semicolon.com.arduino.R;

public class Common {

    public static void intent(Context context, Class<?> cls) {
        Intent in = new Intent(context, cls);
        context.startActivity(in);
    }

    public static AlertDialog.Builder showCustomDialog(Context context, View customView) {

        return new AlertDialog.Builder(context)
                .setIcon(context.getResources().getDrawable(R.drawable.ic_person_add_black_24dp))
                .setMessage("Fill in the patient information")
                .setView(customView);
    }

    public static String getStringFromEditText(EditText editText) {
        return editText.getText().toString();
    }
}
