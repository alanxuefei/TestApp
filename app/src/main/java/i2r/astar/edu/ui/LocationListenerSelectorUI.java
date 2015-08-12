package i2r.astar.edu.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import i2r.astar.edu.global.GlobalState;
import i2r.astar.edu.database.R;
import i2r.astar.edu.util.Utils;

public class LocationListenerSelectorUI extends AppCompatActivity {


    private CheckBox objGPS;
    private CheckBox objNetwork;
    private Spinner objSpinner;
    private CheckBox objLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_listener_selector);

        objGPS =  (CheckBox) findViewById(R.id.chbGPS);
        objNetwork = (CheckBox) findViewById(R.id.chbNetwork);
        objLocation = (CheckBox) findViewById(R.id.chbLocation);
        if (GlobalState.ENABLE_GPS){
            objGPS.setChecked(true);
        }else{
            objGPS.setChecked(false);
        }
        if (GlobalState.ENABLE_NETWORK){
            objNetwork.setChecked(true);
        }else{
            objNetwork.setChecked(false);
        }

        if (GlobalState.blnCaptureLocation){
            objLocation.setChecked(true);
        } else {
            objLocation.setChecked(false);
        }

        String[] aryItems = new String[65 / 5];
        aryItems[0] = "1";
        for (int i = 5; i < 65; i+=5){
            aryItems[i / 5] = Integer.toString(i);
        }

        objSpinner = (Spinner) findViewById(R.id.spinUpdate);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, aryItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        objSpinner.setAdapter(spinnerArrayAdapter);
        objSpinner.setSelection(GlobalState.UPDATE_DURATION / 5);

    }

    public void updateOptions(View view){

        if (objGPS.isChecked()){
            GlobalState.ENABLE_GPS = true;
        }else{
            GlobalState.ENABLE_GPS = false;
        }
        if (objNetwork.isChecked()){
            GlobalState.ENABLE_NETWORK = true;
        }else{
            GlobalState.ENABLE_NETWORK = false;
        }

        if (objLocation.isChecked()){
            GlobalState.blnCaptureLocation = true;
        }else{
            GlobalState.blnCaptureLocation = false;
        }
        Utils.updateDuration(Integer.parseInt((String) objSpinner.getSelectedItem()));

        GlobalState.SETTINGS_CHANGE = true;

        super.onBackPressed();
    }
}
