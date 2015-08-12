package i2r.astar.edu.listener;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import i2r.astar.edu.global.Constant;
import i2r.astar.edu.global.GlobalState;
import i2r.astar.edu.to.AccelerometerTO;
import i2r.astar.edu.to.ErrorTO;
import i2r.astar.edu.to.ModeChangeTO;
import i2r.astar.edu.util.Utils;

import static i2r.astar.edu.global.Constant.*;

/**
 * Created by dashm on 8/6/15.
 */
public class LocationListenerService implements LocationListener {

    // For Stay Region Extraction
    private double dblStayLat = 0;
    private double dblStayLon = 0;
    private DateTime objStayStart;
    private DateTime objLastRecord;
    private double dblStayAccuracy = 0;
    private double dblStayCount = 1;

    private SQLiteDatabase objDB = null;

    private SensorManager objDeviceSensorMgr;
    private LightSensorListenerService objLightSensor;
    private MediaRecorder objMediaRecorder;
    private StepSensorListener objStepSensor;
    private LocalBroadcastManager objBroadcast;
    private Intent intent;
    private LocationManager objLocationMgr;
    private double dblPreviousLat = 0;
    private double dblPreviousLon = 0;
    private DateTime objPreviousDate = null;

    public LocationListenerService(SQLiteDatabase database, SensorManager objSensorMgr,
                                   LightSensorListenerService objLightSensor, StepSensorListener
                                           objStepSensor, LocalBroadcastManager objBroadcast,
                                   LocationManager objLocationMgr) {
        this.objDB = database;
        this.objDeviceSensorMgr = objSensorMgr;
        this.objLightSensor = objLightSensor;
        this.objMediaRecorder = new MediaRecorder();

        this.objStepSensor = objStepSensor;
        this.objBroadcast = objBroadcast;
        this.intent = new Intent(Constant.TRIGGERED);

        this.objLocationMgr = objLocationMgr;
    }

    private boolean setupMediaRecorder() {

        try {
            objMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            objMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            objMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            objMediaRecorder.setOutputFile("/dev/null");
            objMediaRecorder.prepare();
            objMediaRecorder.start();
            objMediaRecorder.getMaxAmplitude();
        } catch (Exception e) {
            objMediaRecorder.reset();
            return false;
        }
        return true;
    }

    private void closeMediaRecorder() {
        objMediaRecorder.stop();
        objMediaRecorder.reset();
    }

    private void resetStayInfo(double dblEventLat, double dblEventLon, double dblEventAccuracy,
                               DateTime objNow) {
        dblStayLat = dblEventLat;
        dblStayLon = dblEventLon;
        dblStayAccuracy = dblEventAccuracy;
        objStayStart = objNow;
        objLastRecord = objNow;
        dblStayCount = 2;
    }

    private void insertAccValues(String strDate, double dblX, double dblY, double dblZ, double
            dblMag) {
        ContentValues values = new ContentValues();
        values.put(SQL_DATE, strDate);
        values.put(SQL_SENSOR_X, dblX);
        values.put(SQL_SENSOR_Y, dblY);
        values.put(SQL_SENSOR_Z, dblZ);
        values.put(SQL_SENSOR_MAG, dblMag);

        objDB.insertOrThrow(SQL_TABLE_SENSOR, null, values);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (!GlobalState.blnLocationServiceRunning){
            objLocationMgr.removeUpdates(this);
            return;
        }

        Utils.startDBTransaction(objDB);
        //System.out.println("===============================================");
        //        System.out.println(GlobalState.lstRecords.size());
        //        System.out.println("===============================================");
            if (GlobalState.lstRecords.size() > 0){
                synchronized (GlobalState.objAccelermoeter) {


                    for (AccelerometerTO objData : GlobalState.lstRecords) {
                        insertAccValues(objData.getStrDate(), objData.getDblX(), objData.getDblY(),
                                objData.getDblZ(), objData.getDblMagnitude());
                    }
                    GlobalState.lstRecords.clear();
                }
            }//end if

        // ===============================================================
        // Batch Insert Data if Any
        // ===============================================================
        if (GlobalState.lstModeChange.size() > 0){
            for (ModeChangeTO objChange : GlobalState.lstModeChange){
                ContentValues values = new ContentValues();
                values.put(Constant.SQL_DATE, objChange.getStrDate());
                values.put(Constant.SQL_TRANSPORT_MODE, objChange.getIntMode());
                objDB.insertOrThrow(Constant.SQL_TABLE_MODE, null, values);
            }
            GlobalState.lstModeChange.clear();
        }

        if (GlobalState.lstError.size() > 0){
            for(ErrorTO objError: GlobalState.lstError){
                ContentValues values = new ContentValues();
                values.put(Constant.SQL_DATE, objError.getStrDate());
                values.put(Constant.SQL_ERROR_MESSAGE, objError.getStrMessage());
                values.put(Constant.SQL_ERROR_SOURCE, objError.getStrSource());

                objDB.insertOrThrow(Constant.SQL_TABLE_ERROR, null, values);
            }
            GlobalState.lstError.clear();
        }

        double dblEventLat = location.getLatitude();
        double dblEventLon = location.getLongitude();
        double dblEventAccuracy = location.getAccuracy();
        DateTime objNow = new DateTime();

        try {
            // ===============================================================
            // Location Data
            // ===============================================================

           // System.out.println("**************************************");
//System.out.println(GlobalState.blnCaptureLocation);
            if (!GlobalState.blnCaptureLocation){


                if (objPreviousDate != null){
                    double dblDistance = Utils.calDistance2Cells(dblEventLat, dblEventLon, dblPreviousLat,
                            dblPreviousLon);

                    int intSeconds = Seconds.secondsBetween(objNow, objPreviousDate).getSeconds();
                    double dblSpeed = dblDistance / (double) intSeconds;
                    //System.out.println("=================================================");
                    //System.out.println(dblSpeed);

                    insertRawRecords(0, 0, 0,
                            location.getProvider(), location.getAltitude(), dblSpeed, location
                                    .getBearing());
                }
                //Compute Speed First
                //Distance / Time


                dblPreviousLat = dblEventLat;
                dblPreviousLon = dblEventLon;
                objPreviousDate = objNow;

            }else{
                insertRawRecords(dblEventLat, dblEventLon, dblEventAccuracy,
                        location.getProvider(), location.getAltitude(), location.getSpeed(), location
                                .getBearing());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utils.insertErrorMessage("LOCATION_ERROR", e.getMessage());
        }

        try {
            // ===============================================================
            // Sample Light
            // ===============================================================
            if (GlobalState.blnLightSensorServiceRunning) {
                objDeviceSensorMgr.registerListener(objLightSensor, objDeviceSensorMgr
                                .getDefaultSensor(Sensor.TYPE_LIGHT),
                        SensorManager.SENSOR_DELAY_FASTEST);
            }
        } catch (Exception e) {
            Utils.insertErrorMessage("LOCATION_LIGHT_ERROR", e.getMessage());
        }


        try {
            // ===============================================================
            // Determine Stay
            // ===============================================================
            if (GlobalState.blnCaptureLocation){
                if (dblStayLat == 0) {
                    resetStayInfo(dblEventLat, dblEventLon, dblEventAccuracy, objNow);
                } else {
                    // Check Stay
                    double dblDistance = Utils.calDistance2Cells(dblStayLat,
                            dblStayLon, dblEventLat, dblStayLon);

                    if (dblDistance < dblStayAccuracy + dblEventAccuracy) {

                        // Update Accuaracy
                        dblStayAccuracy += (dblEventAccuracy - dblStayAccuracy)
                                / dblStayCount;
                        // Update Centroid
                        dblStayLat += (dblEventLat - dblStayLat)
                                / dblStayCount;
                        dblStayLon += (dblEventLon - dblStayLon)
                                / dblStayCount;

                        ++dblStayCount;
                        objLastRecord = objNow;
                    } else {
                        if (Minutes.minutesBetween(objStayStart, objLastRecord).getMinutes() > 60) {
                            insertStayRecords(dblStayLat, dblStayLon, objStayStart, objLastRecord,
                                    dblStayAccuracy);
                        }
                        resetStayInfo(dblEventLat, dblEventLon, dblEventAccuracy, objNow);
                    }
                }// end check stay
            }

        } catch (Exception e) {
            Utils.insertErrorMessage( "LOCATION_STAY_ERROR", e.getMessage());
        }


        // ===============================================================
        // Sound Recorder
        // ===============================================================
        try {
            boolean blnSetupResult = setupMediaRecorder();
            if (blnSetupResult) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try{
                            //Insert into database
                            insertSoundLevel(objMediaRecorder.getMaxAmplitude());
                            closeMediaRecorder();
                            updateScreen();
                        }catch (Exception e){
                        }
                    }
                }, 5000);
            }
        } catch (Exception e) {
            objMediaRecorder = new MediaRecorder();
            Utils.insertErrorMessage("LOCATION_SOUND_ERROR", e.getMessage());
        }


        try {
            // ===============================================================
            // Step Sensor
            // ===============================================================
            if (GlobalState.blnStepSensorServiceRunning) {
                objStepSensor.consolidate();
            }
        } catch (Exception e) {
            Utils.insertErrorMessage("LOCATION_SENSOR_ERROR", e.getMessage());
        }


        try {
            // ===============================================================
            // Settings Changes
            // ===============================================================
            if (GlobalState.SETTINGS_CHANGE || GlobalState.RE_TRIGGER) {
                GlobalState.SETTINGS_CHANGE = false;
                objLocationMgr.removeUpdates(this);

                List<String> providers = objLocationMgr.getProviders(true);
                boolean blnProcess = true;
                if (providers == null || providers.size() == 0) {
                    blnProcess = false;
                }

                if (blnProcess) {
                    if (providers.contains(LocationManager.NETWORK_PROVIDER) && GlobalState
                            .ENABLE_NETWORK) {
                        objLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                GlobalState
                                        .LOCATION_SPEED,
                                0, this);
                    }
                    if (providers.contains(LocationManager.GPS_PROVIDER) && GlobalState
                            .ENABLE_GPS) {
                        objLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                GlobalState
                                        .LOCATION_SPEED,
                                0, this);
                    }
                }//end if
            }//end settings change
        } catch (Exception e) {
            Utils.insertErrorMessage("LOCATION_SETTINGS_ERROR", e.getMessage());
        }


        Utils.endDBTransaction(objDB);

    }

    private void insertSoundLevel(double dblSoundLevel) {
        ContentValues values = new ContentValues();
        values.put(SQL_RECORDVALUE, dblSoundLevel);
        values.put(SQL_DATE, Utils.getCurrentTimeStamp());
        objDB.insertOrThrow(SQL_TABLE_SOUND, null, values);
    }


    private void insertStayRecords(double dblLat, double dblLon, DateTime objStart, DateTime
            objEnd, double dblAccuracy) {

        ContentValues values = new ContentValues();
        values.put(SQL_LAT, dblLat);
        values.put(SQL_LON, dblLon);
        values.put(SQL_STAY_START, objStart.toString(Constant.TIMESTAMP_FORMAT));
        values.put(SQL_STAY_END, objEnd.toString(Constant.TIMESTAMP_FORMAT));
        values.put(SQL_LOCATION_ACCURACY, dblAccuracy);
        objDB.insertOrThrow(SQL_TABLE_STAY, null, values);

    }


    private void insertRawRecords(double dblLat, double dblLon, double dblAccuracy, String
            strProvider, double dblAltitude, double dblSpeed, double dblBearing) {
        ContentValues values = new ContentValues();
        values.put(SQL_LAT, dblLat);
        values.put(SQL_LON, dblLon);
        values.put(SQL_DATE, Utils.getCurrentTimeStamp());
        values.put(SQL_LOCATION_PROVIDER, strProvider);
        values.put(SQL_LOCATION_ACCURACY, dblAccuracy);
        values.put(SQL_LOCATION_ALTITUDE, dblAltitude);
        values.put(SQL_LOCATION_SPEED, dblSpeed);
        values.put(SQL_LOCATION_BEARING, dblBearing);

        objDB.insertOrThrow(SQL_TABLE_LOCATION, null, values);
    }


//    //Not used at the moment
//    public void retrieveAllRecords() {
//        Cursor curData = objDB.query("tblLocation", new String[]{"dblLat", "dblLon",
//                "txtDate"}, null, null, null, null, null);
//        curData.moveToFirst();
//        int intCount = 0;
//
//        if (!curData.isAfterLast()) {
//            do {
//                intCount++;
//            } while (curData.moveToNext());
//        }
//        curData.close();
//    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void updateScreen() {
        objBroadcast.sendBroadcast(intent);
        GlobalState.strLastTriggered = Utils.getCurrentTimeStamp();
    }

}
