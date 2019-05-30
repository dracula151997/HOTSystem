package arduino.semicolon.com.arduino.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private Context context;
    private AppDatabase appDatabase;

    public DatabaseClient(Context context) {
        this.context = context;

        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "patient_database")
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null){
            instance = new DatabaseClient(context);
        }

        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
