package arduino.semicolon.com.arduino.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DatabaseEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatabaseDao databaseDao();
}
