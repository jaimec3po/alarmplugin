package co.gov.rtvc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import co.gov.senalradionica.HelloWorld2;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmPlugin extends CordovaPlugin {
	
	private static final String TAG = AlarmPlugin.class.getSimpleName();
	
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());


	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		
		Log.d(TAG, "Ha llegado el mensaje de invocacion, action " + action + " args "+ args.toString());

		if("setup".equalsIgnoreCase(action)){
			
			int hour = args.getInt(0);
			int min = args.getInt(1);
			boolean[] bDays = {true, true, true, true, true, true, true};
			JSONArray days = args.getJSONArray(2);
			
			if(days != null){
				for(int i = 0; i < days.length(); i++){
					bDays[i] = days.getBoolean(i);
				}
			}
			
			setAlarm(hour, min, bDays);
			
			callbackContext.success("true");
			return true;
			
		} else if ("cancel".equalsIgnoreCase(action)){
			callbackContext.success("true");
			return true;
		}		
		return false;
	}
	
	
	private void setAlarm(int hour, int min, boolean[] days){
		
		Calendar day = Calendar.getInstance();
		day.set(Calendar.HOUR_OF_DAY, hour);
		day.set(Calendar.MINUTE, min);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);		
		
		// Get a reference to the Alarm Manager 
		AlarmManager alarmManager = (AlarmManager)this.cordova.getActivity().getSystemService(Context.ALARM_SERVICE);
				
		// Create a Pending Intent that will broadcast and action String ALARM_ACTION
		Intent intentToFire = new Intent(this.cordova.getActivity(), HelloWorld2.class);
		intentToFire.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		for(int i = 1; i < 8; i++){
			Calendar c = day;
			c.set(Calendar.DAY_OF_WEEK, i);
			cancelAlarm(alarmManager, intentToFire, c);
		}
		
		for(int i = 0; i < days.length; i++){
			if(days[i]){
				Calendar c = day;
				c.set(Calendar.DAY_OF_WEEK, i + 1);
				setAlarmForDay(alarmManager, intentToFire, c);
			}
		}
	}
	
	private void cancelAlarm(AlarmManager alarmManager, Intent intentToFire, Calendar day){
		PendingIntent alarmIntent = PendingIntent.getActivity(this.cordova.getActivity(), day.get(Calendar.DAY_OF_WEEK), intentToFire, 0);
		alarmManager.cancel(alarmIntent);
	}
	
	private void setAlarmForDay(AlarmManager alarmManager, Intent intentToFire, Calendar dayInput){
		// Set the alarm to wake the device if sleeping.
		int alarmType = AlarmManager.RTC_WAKEUP;
						
		// Schedule the alarm to repeat every week day.
		long timeOfWait = AlarmManager.INTERVAL_DAY * 7;
		long firstTimeLaunch = 0;
				
		Calendar now = Calendar.getInstance();
		Calendar day = Calendar.getInstance();
		day.setTimeInMillis(dayInput.getTimeInMillis());
		
		PendingIntent alarmIntent = PendingIntent.getActivity(this.cordova.getActivity(), day.get(Calendar.DAY_OF_WEEK), intentToFire, 0);

		
		//Log.d(TAG, "now " + now.toString());
		//Log.d(TAG, "day " + day.toString());
		
		if(now.after(day)){
			Log.d(TAG, "El ahora es posterior a la fecha de la alarma para el dia " + day.get(Calendar.DAY_OF_WEEK));
			day.set(Calendar.WEEK_OF_YEAR, day.get(Calendar.WEEK_OF_YEAR) + 1);
			firstTimeLaunch = day.getTimeInMillis();
		} else {
			Log.d(TAG, "El ahora es anterior a la fecha de la alarma para el dia " + day.get(Calendar.DAY_OF_WEEK));
			firstTimeLaunch = day.getTimeInMillis();
		}
		
		Calendar f = Calendar.getInstance();
		f.setTimeInMillis(firstTimeLaunch);

		
		Log.d(TAG, "El tiempo de lanzamiento esta en " + df.format(f.getTime()));
		
		// Wake up the device to fire an alarm in the calculated day and hour, 
		// and every week after that.
				
		Log.d(TAG, "setup Alarm for day " + day.get(Calendar.DAY_OF_WEEK));
				
		alarmManager.setRepeating(alarmType, firstTimeLaunch, timeOfWait, alarmIntent);

	}

}
