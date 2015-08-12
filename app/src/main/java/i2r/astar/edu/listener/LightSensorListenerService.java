package i2r.astar.edu.listener;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import i2r.astar.edu.global.Constant;
import i2r.astar.edu.global.GlobalState;
import i2r.astar.edu.util.Utils;

/**
 * Created by dashm on 10/6/15.
 */
public class LightSensorListenerService implements SensorEventListener {

    SQLiteDatabase objDB = null;
    SensorManager objSensorManager;
    Sensor objLightSensor;

    public LightSensorListenerService(SQLiteDatabase database, SensorManager objSensorManager){
        this.objDB = database;
        this.objSensorManager = objSensorManager;

        List<Sensor> lstSensorLightList =
                this.objSensorManager.getSensorList(Sensor.TYPE_LIGHT);
        if (lstSensorLightList != null && lstSensorLightList.size() > 0){
            objLightSensor = this.objSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }

    public void insertValues(double dblValue) {
        ContentValues values = new ContentValues();
        values.put(Constant.SQL_DATE, Utils.getCurrentTimeStamp());
        values.put(Constant.SQL_RECORDVALUE, dblValue);
        objDB.insertOrThrow(Constant.SQL_TABLE_LIGHT, null, values);
    }


    long lngLastRecord = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {

        try{
            if (event.sensor.getType() == Sensor.TYPE_LIGHT){
                if ((event.timestamp - lngLastRecord) / Constant.NANO_TO_SEC > GlobalState.UPDATE_DURATION){
                    insertValues(event.values[0]);
                }else{
                    objSensorManager.unregisterListener(this, objLightSensor);
                }
                lngLastRecord = event.timestamp;
            }//end light sensor check
        }catch (Exception e){
            Utils.insertErrorMessage("LIGHT_ERROR", e.getMessage());
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
