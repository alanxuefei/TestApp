package i2r.astar.edu.listener;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import i2r.astar.edu.global.Constant;
import i2r.astar.edu.util.Utils;

/**
 * Created by dashm on 15/6/15.
 */
public class StepSensorListener implements SensorEventListener {


    SensorManager objDeviceSensorManager;
    SQLiteDatabase objDatabase;
    boolean blnProcessedFirst = false;
    public StepSensorListener(SQLiteDatabase database, SensorManager objSensorManager){
        this.objDeviceSensorManager = objSensorManager;
        this.objDatabase = database;
    }


    double dblCount = 0;
    double dblLastInsertedCount = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        dblCount += event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void consolidate(){
        insertRecord(dblCount);
        dblCount = 0;
    }


    //There is potential for dirty read, a decision is made to ignore because the values will be inmaterial
    //Creating syncrhonization will be expensive with minimal benefits
    public void insertRecord(double dblCount){

        try{
            if (blnProcessedFirst){
                ContentValues values = new ContentValues();
                values.put(Constant.SQL_DATE, Utils.getCurrentTimeStamp());
                values.put(Constant.SQL_RECORDVALUE, dblCount);
                objDatabase.insertOrThrow(Constant.SQL_TABLE_STEP, null, values);
            }else{
                //Ignore First Record
                blnProcessedFirst = true;
            }
        }catch (Exception e){
            Utils.insertErrorMessage("SENSOR_ERROR", e.getMessage());
        }


    }
}
