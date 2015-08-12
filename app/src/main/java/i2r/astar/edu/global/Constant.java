package i2r.astar.edu.global;

/**
 * Created by dashm on 10/6/15.
 */
public class Constant {


    public static final float SHAKE_THRESHOLD = 1.5f;
    public static final long NANO_TO_SEC = 1000000000;
    public static final int SECONDS = 60;
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static final String TRIGGERED = "triggered";

    public static final String SQL_PK_ID = "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
    public static final String  SQL_CREATE_TABLE = "CREATE TABLE ";
    public static final String SQL_TABLE_LOCATION = "tblLocation";


    public static final String SQL_SENSOR_X = "dblX";
    public static final String SQL_SENSOR_Y = "dblY";
    public static final String SQL_SENSOR_Z = "dblZ";
    public static final String SQL_SENSOR_MAG = "dblMag";
    public static final String SQL_DATE = "txtDate";

    public static final String SQL_TABLE_SENSOR = "tblSensor";
    public static final String SQL_REAL = " REAL ";
    public static final String SQL_TIMESTAMP = " TIMESTAMP ";
    public static final String SQL_TEXT = " TEXT ";


    public static final String SQL_TABLE_ERROR = "tblError";
    public static final String SQL_ERROR_SOURCE = "tblSource";
    public static final String SQL_ERROR_MESSAGE = "tblMessage";
    public static final String SQL_TABLE_MODE = "tblMode";

    public static final String SQL_TRANSPORT_MODE = "intMode";

    public static final String SQL_LAT = "dblLat";
    public static final String SQL_LON = "dblLon";
    public static final String SQL_LOCATION_PROVIDER = "txtProvider";
    public static final String SQL_LOCATION_SPEED = "dblSpeed";
    public static final String SQL_LOCATION_BEARING = "dblBearing";
    public static final String SQL_LOCATION_ALTITUDE = "dblAltitude";
    public static final String SQL_LOCATION_ACCURACY = "dblAccuracy";

    public static final String SQL_COMMA = " , ";
    public static final String SQL_OPEN_BRACKET = " ( ";
    public static final String SQL_CLOSE_BRACKET = " ) ";
    public static final String SQL_TABLE_STAY = "tblStay";
    public static final String SQL_STAY_START = "txtStartDate";
    public static final String SQL_STAY_END = "txtEndDate";
    public static final String SQL_TABLE_LIGHT = "tblLight";
    public static final String SQL_TABLE_SOUND = "tblSound";
    public static final String SQL_TABLE_AZIMUTH = "tblAzimuth";

    public static final String SQL_RECORDVALUE = "dblValue";

    public static final String SQL_TABLE_STEP = "tblStep";

    public static final int MODE_WALKING = 0;
    public static final int MODE_MRT = 1;
    public static final int MODE_TAXI = 2;
    public static final int MODE_CYCLING = 3;
    public static final int MODE_BUS = 4;
    public static final int MODE_NA = 5;

    public static final String MODE[] = {"WALKING", "MRT", "TAXI", "CYCLING", "BUS", "NA"};

    public static final String ERROR_MAIN = "MAIN_MENU_ERROR";


}
