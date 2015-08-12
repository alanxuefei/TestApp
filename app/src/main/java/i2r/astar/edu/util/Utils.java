/**
 * 
 */
package i2r.astar.edu.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import i2r.astar.edu.global.Constant;
import i2r.astar.edu.global.GlobalState;
import i2r.astar.edu.to.ErrorTO;

import static i2r.astar.edu.global.Constant.SQL_TABLE_LOCATION;

/**
 * The Class Utils.
 *
 * @author wuwei
 */
public class Utils {

	/** The Constant CONSTANT. */
	public static final int CONSTANT = 6371;
	public static final double LATLONG_COMPUTE = 57.2957795130823;
	/** The r. */
	public static double R = 6371000; // meter
	public static SimpleDateFormat objDateFormat = new SimpleDateFormat(Constant.TIMESTAMP_FORMAT);

	/*
	 * Link for calculation: http://www.movable-type.co.uk/scripts/latlong.html
	 */
	/**
	 * Cal distance2 cells.
	 *
	 * @param lat1 the lat1
	 * @param long1 the long1
	 * @param lat2 the lat2
	 * @param long2 the long2
	 * @return the double
	 */
	public static double calDistance2Cells(double lat1, double long1,
			double lat2, double long2) {
		
		if (lat1 == lat2 && long1 == long2)
			return 0;

		double rLong1 = long1 / LATLONG_COMPUTE;
		double rLat1 = lat1 / LATLONG_COMPUTE;
		double rLong2 = long2 / LATLONG_COMPUTE;
		double rLat2 = lat2 / LATLONG_COMPUTE;
		double dLong = rLong2 - rLong1;
		double dLat = rLat2 - rLat1;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rLat1)
				* Math.cos(rLat2) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return c * R;
	}

	/**
	 * http://stackoverflow.com/questions/8077530/android-get-current-timestamp
	 *
	 * @return yyyy-MM-dd HH:mm:ss formate date as string
	 */
	public static String getCurrentTimeStamp() {
		try {
			return objDateFormat.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void updateDuration(int intMinutes){
		GlobalState.LOCATION_SPEED = intMinutes * Constant.SECONDS * 1000;
		GlobalState.UPDATE_DURATION = intMinutes;
	}

	public static void startDBTransaction(SQLiteDatabase objDB ){
        objDB.beginTransaction();
	}

    public static void endDBTransaction(SQLiteDatabase objDB ){
        objDB.setTransactionSuccessful();
        objDB.endTransaction();

    }

	public static void insertErrorMessage(String strSource, String
			strMessage){

		ErrorTO objError = new ErrorTO();
		objError.setStrDate(Utils.getCurrentTimeStamp());
		objError.setStrMessage(strMessage);
		objError.setStrSource(strSource);
	}

}
