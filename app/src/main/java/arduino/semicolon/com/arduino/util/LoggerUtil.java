package arduino.semicolon.com.arduino.util;

import android.util.Log;

public class LoggerUtil {

    private static final String TAG = LoggerUtil.class.getName();

    public static void verbose(String message) {
        Log.v(TAG, message);
    }

    public static void error(String errorMessage){
        Log.e(TAG, errorMessage);
    }
}
