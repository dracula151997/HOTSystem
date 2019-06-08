package arduino.semicolon.com.arduino.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DatabaseDao {

    @Insert
    void insert(DatabaseEntity newPatient);

    @Delete
    void delete(DatabaseEntity patient);

    @Update
    void update(DatabaseEntity currentPatient);

    @Query("SELECT * FROM DatabaseEntity")
    List<DatabaseEntity> getAllPatients();

    @Query("SELECT * FROM DatabaseEntity WHERE id LIKE :id")
    List<DatabaseEntity> getPatientById(int id);


}
