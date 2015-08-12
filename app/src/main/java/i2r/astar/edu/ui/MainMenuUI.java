package i2r.astar.edu.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import i2r.astar.edu.global.Constant;
import i2r.astar.edu.global.GlobalState;
import i2r.astar.edu.listener.LightSensorListenerService;
import i2r.astar.edu.listener.LocationListenerService;
import i2r.astar.edu.listener.AccelerometerEventListenerService;
import i2r.astar.edu.listener.StepSensorListener;
import i2r.astar.edu.service.DatabaseService;
import i2r.astar.edu.service.ForegroundService;
import i2r.astar.edu.database.DatabaseContext;
import i2r.astar.edu.database.*;
import i2r.astar.edu.to.ModeChangeTO;
import i2r.astar.edu.util.Utils;


public class MainMenuUI extends AppCompatActivity {

    LocationManager objLocationMgr;
    SensorManager objDeviceSensorMgr;
    LocationListenerService objLocationService;
    AccelerometerEventListenerService objAccSensorListener;
    LightSensorListenerService objLightSensor;
    StepSensorListener objStepSensor;

    DatabaseService objDatabaseService;
    SQLiteDatabase objDatabase;

    Resources objRes;

    TextView lblLastTriggered;
    TextView lblErrorRecords;
    TextView lblCurrentMode;
    TextView txtSensorStatus;

    LocalBroadcastManager objBroadcastMgr;
    BroadcastReceiver objReceiver;


    //Buttons
    Button btnMovementMonitoring;
    Button btnMRT;
    Button btnWalking;
    Button btnTaxi;
    Button btnBus;
    Button btnNA;
    Button btnCycle;
    Button btnLocationMonitoring;

    //Colors
    Drawable objDefault;
    int intBlueColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        objLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        objDeviceSensorMgr =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        objDatabaseService = new DatabaseService(new DatabaseContext(this));
        objDatabase = objDatabaseService.getWritableDatabase();

        objBroadcastMgr = LocalBroadcastManager.getInstance(this);
        objReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                triggered();
            }
        };

        this.objLightSensor = new LightSensorListenerService(objDatabase, objDeviceSensorMgr);
        objStepSensor = new StepSensorListener(objDatabase, objDeviceSensorMgr);
        objLocationService = new LocationListenerService(objDatabase, objDeviceSensorMgr,
                objLightSensor, objStepSensor, objBroadcastMgr, objLocationMgr);
        objAccSensorListener = new AccelerometerEventListenerService(objDatabase);

        objRes = this.getResources();
        lblLastTriggered = (TextView) findViewById(R.id.lblLastTrigger);
        lblErrorRecords = (TextView) findViewById(R.id.lblErrorRecords);
        lblCurrentMode = (TextView) findViewById(R.id.lblCurrentMode);

        String PhoneModel = android.os.Build.MODEL;
        if (PhoneModel.equalsIgnoreCase("A0001")) {
            GlobalState.RE_TRIGGER = true;
        }

        lblCurrentMode.setText(Constant.MODE[GlobalState.intCurrentMode]);


        //Buttons
        btnMRT = (Button) findViewById(R.id.btnMRT);
        btnNA = (Button) findViewById(R.id.btnNA);
        btnWalking = (Button) findViewById(R.id.btnWalking);
        btnBus = (Button) findViewById(R.id.btnBUS);
        btnCycle = (Button) findViewById(R.id.btnCycle);
        btnTaxi = (Button) findViewById(R.id.btnTaxi);

        btnLocationMonitoring = (Button) findViewById(R.id.btnLocationMonitoring);
        objDefault = btnLocationMonitoring.getBackground();
        intBlueColor = getResources().getColor(android.R.color.holo_blue_dark);

        txtSensorStatus = (TextView) findViewById(R.id.lblSensorStatus);
        btnMovementMonitoring = (Button) findViewById(R.id.btnMovementMonitoring);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(objReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((objReceiver),
                new IntentFilter(Constant.TRIGGERED));
    }

    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(objReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LocationListenerSelectorUI.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void controlStepSensor() {
        List<Sensor> lstStepSensor = objDeviceSensorMgr.getSensorList(Sensor.TYPE_STEP_DETECTOR);
        TextView objStepSensorLabel = (TextView) findViewById(R.id.lblStepSensorStatus);


        if (lstStepSensor.size() == 0) {
            objStepSensorLabel.setText("No Step Senor on Phone");
            GlobalState.blnStepSensorServiceRunning = false;
        } else {
            objStepSensorLabel.setText("Started");
            GlobalState.blnStepSensorServiceRunning = true;
            objDeviceSensorMgr.registerListener(objStepSensor,
                    objDeviceSensorMgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager
                            .SENSOR_DELAY_NORMAL);
        }
    }


    public void controlMotionSensor(View view) {

        try{
            // ===========================================================
            // Sensor Data
            // ===========================================================
            List<Sensor> lstAccelerometerSensor =
                    objDeviceSensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            List<Sensor> lstMagneticSensor =
                    objDeviceSensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

            // Start Sensor
            if (!GlobalState.blnSensorServiceRunnning) {
                if (lstAccelerometerSensor.size() > 0 && lstMagneticSensor.size() > 0) {
                    objDeviceSensorMgr.registerListener(objAccSensorListener, objDeviceSensorMgr
                                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            SensorManager.SENSOR_DELAY_NORMAL);
//                    objDeviceSensorMgr.registerListener(objAccSensorListener, objDeviceSensorMgr
//                                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                            SensorManager.SENSOR_DELAY_NORMAL);

                    txtSensorStatus.setText(objRes.getText(R.string.started));
                    GlobalState.blnSensorServiceRunnning = true;
                    btnMovementMonitoring.setText(objRes.getText(R.string.stopMonitoringMovement));
                    startForegrdService();
                } else {
                    txtSensorStatus.setText(objRes.getText(R.string.sensorDisabled));
                    GlobalState.blnSensorServiceRunnning = false;
                }
            } else {
                objDeviceSensorMgr.unregisterListener(objAccSensorListener, objDeviceSensorMgr
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//                objDeviceSensorMgr.unregisterListener(objAccSensorListener, objDeviceSensorMgr
//                        .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
                txtSensorStatus.setText(objRes.getText(R.string.stopped));
                btnMovementMonitoring.setText(objRes.getText(R.string.startMonitoringMovement));
                GlobalState.blnSensorServiceRunnning = false;
                stopForegrdService();
            }
        }catch (Exception e){
            Utils.insertErrorMessage(Constant.ERROR_MAIN + " Sensor Control", e
                    .getMessage());
        }


    }


    public void controlLocationLightSensor(View view) {
        try{
            // ===========================================================
            // Light Sensor
            // ===========================================================
            List<Sensor> lstSensorLightList =
                    objDeviceSensorMgr.getSensorList(Sensor.TYPE_LIGHT);

            // ===========================================================
            // Location Data
            // ===========================================================
            List<String> providers = objLocationMgr.getProviders(true);
            TextView lblLocationStatus = (TextView) findViewById(R.id.lblLocationStatus);
            TextView lblStepStatus = (TextView) findViewById(R.id.lblStepSensorStatus);

            if (!GlobalState.blnLocationServiceRunning) {
                int intStartCount = 0;
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
                                0, objLocationService);
                        intStartCount++;
                    }
                    if (providers.contains(LocationManager.GPS_PROVIDER) && GlobalState.ENABLE_GPS) {
                        objLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, GlobalState
                                        .LOCATION_SPEED,
                                0, objLocationService);
                        intStartCount++;
                    }
                }//end if
                if (intStartCount > 0) {

                    // Light Sensor
                    if (lstSensorLightList.size() > 0 && !GlobalState.blnLightSensorServiceRunning) {
                        GlobalState.blnLightSensorServiceRunning = true;
                    } else {
                        GlobalState.blnLightSensorServiceRunning = false;
                    }

                    // Step Sensor
                    controlStepSensor();

                    lblLocationStatus.setText(objRes.getText(R.string.started));
                    btnLocationMonitoring.setText(objRes.getText(R.string.stopMonitoringLocation));
                    GlobalState.blnLocationServiceRunning = true;
                    startForegrdService();
                } else {
                    objLocationMgr.removeUpdates(objLocationService);
                    lblLocationStatus.setText(objRes.getText(R.string.locationDisabled));
                }


            }//end if
            else {
                objLocationMgr.removeUpdates(objLocationService);
                objDeviceSensorMgr.unregisterListener(objLightSensor, objDeviceSensorMgr
                        .getDefaultSensor(Sensor.TYPE_LIGHT));
                objDeviceSensorMgr.unregisterListener(objStepSensor,
                        objDeviceSensorMgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));

                GlobalState.blnLocationServiceRunning = false;
                GlobalState.blnLightSensorServiceRunning = false;
                GlobalState.blnStepSensorServiceRunning = false;
                lblLocationStatus.setText(objRes.getText(R.string.stopped));
                lblStepStatus.setText(objRes.getText(R.string.stopped));
                btnLocationMonitoring.setText(objRes.getText(R.string.startMonitoringLocation));

                stopForegrdService();
            }//end else
        }catch (Exception e){
            Utils.insertErrorMessage(Constant.ERROR_MAIN + " Location Monitoring", e
                    .getMessage());
        }


    }

    public void updateScreen() {
        TextView objLocationStatus = (TextView) findViewById(R.id.lblLocationStatus);
        TextView objSensorStatus = (TextView) findViewById(R.id.lblSensorStatus);
        TextView lblStepStatus = (TextView) findViewById(R.id.lblStepSensorStatus);
        Button objLocationButton = (Button) findViewById(R.id.btnLocationMonitoring);
        Button objMovementButton = (Button) findViewById(R.id.btnMovementMonitoring);

        //Location + Light Sensor
        if (GlobalState.blnLocationServiceRunning) {
            objLocationStatus.setText(objRes.getText(R.string.started));
            objLocationButton.setText(objRes.getText(R.string.stopMonitoringLocation));
        } else {
            objLocationStatus.setText(objRes.getText(R.string.stopped));
            objLocationButton.setText(objRes.getText(R.string.startMonitoringLocation));
        }

        //Movement Sensor
        if (GlobalState.blnSensorServiceRunnning) {
            objSensorStatus.setText(objRes.getText(R.string.started));
            objMovementButton.setText(objRes.getText(R.string.stopMonitoringMovement));
        } else {
            objSensorStatus.setText(objRes.getText(R.string.stopped));
            objMovementButton.setText(objRes.getText(R.string.startMonitoringMovement));
        }

        if (GlobalState.blnStepSensorServiceRunning) {
            lblStepStatus.setText(objRes.getText(R.string.started));
        } else {
            lblStepStatus.setText(objRes.getText(R.string.stopped));
        }
    }

    public void startForegrdService() {
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    public void stopForegrdService() {
        if (!GlobalState.blnSensorServiceRunnning &&
                !GlobalState.blnLightSensorServiceRunning &&
                !GlobalState.blnLocationServiceRunning) {
            Intent intent = new Intent(this, ForegroundService.class);
            stopService(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScreen();
        updateColors();
        LocalBroadcastManager.getInstance(this).registerReceiver((objReceiver),
                new IntentFilter(Constant.TRIGGERED));
        triggered();
    }

    public void triggered() {
        lblLastTriggered.setText(GlobalState.strLastTriggered);
        lblErrorRecords.setText(GlobalState.intTotalErrorAccelerator + "");
    }

    public void updateTransportMode(View v) {

        GlobalState.intCurrentMode = Integer.parseInt((String) v.getTag());
        lblCurrentMode.setText(Constant.MODE[GlobalState.intCurrentMode]);

        ModeChangeTO objChange = new ModeChangeTO();
        objChange.setStrDate(Utils.getCurrentTimeStamp());
        objChange.setIntMode(GlobalState.intCurrentMode);
        GlobalState.lstModeChange.add(objChange);

        updateColors();
    }

    private void updateColors(){
        clearColors();
        switch (GlobalState.intCurrentMode){
            case Constant.MODE_BUS:
                btnBus.setBackgroundColor(intBlueColor);
                break;
            case Constant.MODE_CYCLING:
                btnCycle.setBackgroundColor(intBlueColor);
                break;
            case Constant.MODE_MRT:
                btnMRT.setBackgroundColor(intBlueColor);
                break;
            case Constant.MODE_WALKING:
                btnWalking.setBackgroundColor(intBlueColor);
                break;
            case Constant.MODE_NA:
                btnNA.setBackgroundColor(intBlueColor);
                break;
            default:
                btnTaxi.setBackgroundColor(intBlueColor);

        }
    }

    public void clearColors() {

        btnMRT.setBackground(objDefault);
        btnNA.setBackground(objDefault);
        btnWalking.setBackground(objDefault);
        btnBus.setBackground(objDefault);
        btnCycle.setBackground(objDefault);
        btnTaxi.setBackground(objDefault);
    }
}
