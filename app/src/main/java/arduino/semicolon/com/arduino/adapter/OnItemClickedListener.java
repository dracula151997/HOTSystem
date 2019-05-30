package arduino.semicolon.com.arduino.adapter;

import java.util.List;

import arduino.semicolon.com.arduino.database.DatabaseEntity;

public interface OnItemClickedListener {
    void onItemClicked(DatabaseEntity entity);
}
