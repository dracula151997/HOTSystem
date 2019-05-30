package arduino.semicolon.com.arduino.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

public class HotSystemTypeConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Double> stringToDoubleList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Double>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String doublesToString(List<Double> values) {
        return gson.toJson(values);
    }
}
