package arduino.semicolon.com.arduino.database;

import java.io.Serializable;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class DatabaseEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "patient_name")
    private String name;

    @ColumnInfo(name = "patient_age")
    private String age;

    @TypeConverters(HotSystemTypeConverter.class)
    @ColumnInfo(name = "pulse_sense_list")
    private List<Double> pulseSenseList;

    @ColumnInfo(name = "pulse_average")
    private double pulseAverage;

    @ColumnInfo(name = "pulse_state")
    private String pulseState;

    @TypeConverters(HotSystemTypeConverter.class)
    @ColumnInfo(name = "co2_list")
    private List<Double> co2List;

    @ColumnInfo(name = "co2_average")
    private double average;

    @ColumnInfo(name = "co2_state")
    private String co2State;

    @TypeConverters(HotSystemTypeConverter.class)
    @ColumnInfo(name = "temp_list")
    private List<Double> tempList;

    @ColumnInfo(name = "temp_average")
    private double tempAverage;

    @ColumnInfo(name = "temp_state")
    private String tempState;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<Double> getPulseSenseList() {
        return pulseSenseList;
    }
    public void setPulseSenseList(List<Double> pulseSenseList) {
        this.pulseSenseList = pulseSenseList;
    }

    public double getPulseAverage() {
        return pulseAverage;
    }

    public void setPulseAverage(double pulseAverage) {
        this.pulseAverage = pulseAverage;
    }

    public String getPulseState() {
        return pulseState;
    }

    public void setPulseState(String pulseState) {
        this.pulseState = pulseState;
    }

    public List<Double> getCo2List() {
        return co2List;
    }

    public void setCo2List(List<Double> co2List) {
        this.co2List = co2List;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getCo2State() {
        return co2State;
    }

    public void setCo2State(String co2State) {
        this.co2State = co2State;
    }

    public List<Double> getTempList() {
        return tempList;
    }

    public void setTempList(List<Double> tempList) {
        this.tempList = tempList;
    }

    public double getTempAverage() {
        return tempAverage;
    }

    public void setTempAverage(double tempAverage) {
        this.tempAverage = tempAverage;
    }

    public String getTempState() {
        return tempState;
    }

    public void setTempState(String tempState) {
        this.tempState = tempState;
    }

    @Override
    public String toString() {
        return "DatabaseEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", pulseSenseList=" + pulseSenseList +
                ", pulseAverage=" + pulseAverage +
                ", pulseState='" + pulseState + '\'' +
                ", co2List=" + co2List +
                ", average=" + average +
                ", co2State='" + co2State + '\'' +
                ", tempList=" + tempList +
                ", tempAverage=" + tempAverage +
                ", tempState='" + tempState + '\'' +
                '}';
    }
}
