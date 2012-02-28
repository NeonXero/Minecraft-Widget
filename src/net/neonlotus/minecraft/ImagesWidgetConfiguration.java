package net.neonlotus.minecraft;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ImagesWidgetConfiguration extends Activity {
    public static final String PREFS_NAME = "ImagesWidgetPrefs";
    public static final String PREFS_UPDATE_RATE_FIELD_PATTERN = "UpdateRate-%d";
    public static final String PREFS_CONTROLS_ACTIVE_FIELD_PATTERN = "ControlsActive-%d";
    public static final String PREFS_PAUSED_FIELD_PATTERN = "Paused-%d";

    RadioGroup time;
    RadioButton r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13;
    Button b1;

    int updateRateSeconds;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    String TAG = "/mc/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        time = (RadioGroup) findViewById(R.id.RadioGroup01);
        //r1 = (RadioButton) findViewById(R.id.RadioButton01);
        r2 = (RadioButton) findViewById(R.id.RadioButton02);
        r3 = (RadioButton) findViewById(R.id.RadioButton03);
        r4 = (RadioButton) findViewById(R.id.RadioButton04);
        r5 = (RadioButton) findViewById(R.id.RadioButton05);
        r6 = (RadioButton) findViewById(R.id.RadioButton06);
        r7 = (RadioButton) findViewById(R.id.RadioButton07);
        r8 = (RadioButton) findViewById(R.id.RadioButton08);
        r9 = (RadioButton) findViewById(R.id.RadioButton09);
        r10 = (RadioButton) findViewById(R.id.RadioButton10);
       // r11 = (RadioButton) findViewById(R.id.RadioButton11);
        //r12 = (RadioButton) findViewById(R.id.RadioButton12);
        r13 = (RadioButton) findViewById(R.id.RadioButtonnone);
        b1 = (Button) findViewById(R.id.Button01);

        readSelection();

        // get any data we were launched with
        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent cancelResultValue = new Intent();
            cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            setResult(RESULT_CANCELED, cancelResultValue);
        } else {
            // only launch if it's for configuration
            // Note: when you launch for debugging, this does prevent this
            // activity from running. We could also turn off the intent
            // filtering for main activity.
            // But, to debug this activity, we can also just comment the
            // following line out.
            finish();
        }

        final SharedPreferences config = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        updateRateSeconds = sharedPrefs.getInt(PREFS_NAME, updateRateSeconds);

        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (r1.isChecked()) {
                    updateRateSeconds = 900;
                    saveSelection();
                } else if (r2.isChecked()) {
                    updateRateSeconds = 1800;
                    saveSelection();
                } else if (r3.isChecked()) {
                    updateRateSeconds = 3600;
                    saveSelection();
                } else if (r4.isChecked()) {
                    updateRateSeconds = 5400;
                    saveSelection();
                } else if (r5.isChecked()) {
                    updateRateSeconds = 7200;
                    saveSelection();
                } else if (r6.isChecked()) {
                    updateRateSeconds = 9000;
                    saveSelection();
                } else if (r7.isChecked()) {
                    updateRateSeconds = 10800;
                    saveSelection();
                } else if (r8.isChecked()) {
                    updateRateSeconds = 21600;
                    saveSelection();
                } else if (r9.isChecked()) {
                    updateRateSeconds = 43200;
                    saveSelection();
                } else if (r10.isChecked()) {
                    updateRateSeconds = 86400;
                    saveSelection();
                } else if (r11.isChecked()) {
                    updateRateSeconds = 60;
                    saveSelection();
                } else if (r12.isChecked()) {
                    updateRateSeconds = 300;
                    saveSelection();
                } else if (r13.isChecked()) {
                    updateRateSeconds = 0;
                    saveSelection();
                } else {
                    updateRateSeconds = 86400;
                    saveSelection();
                }

                // store off the user setting for update timing
                SharedPreferences.Editor configEditor = config.edit();

                configEditor.putInt(String.format(
                        PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId),
                        updateRateSeconds);
                configEditor.commit();

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    // tell the app widget manager that we're now configured
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId);
                    setResult(RESULT_OK, resultValue);

                    Intent widgetUpdate = new Intent();
                    widgetUpdate
                            .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                            new int[]{appWidgetId});

                    // make this pending intent unique
                    widgetUpdate.setData(Uri.withAppendedPath(Uri
                            .parse(ImagesWidgetProvider.URI_SCHEME
                                    + "://widget/id/"), String
                            .valueOf(appWidgetId)));
                    PendingIntent newPending = PendingIntent.getBroadcast(
                            getApplicationContext(), 0, widgetUpdate,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // schedule the new widget for updating
                    AlarmManager alarms = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime(),
                            updateRateSeconds * 1000, newPending);
                }

                // activity is now done
                finish();
            }
        });
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            //change of background
        }
        else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            //change the background
        }
        else
        { //do nothing, this might apply for the keyboard }


            super.onConfigurationChanged(newConfig);





        }
    }*/

    public void saveSelection() {
        Log.d(TAG, "" + updateRateSeconds);
        //SharedPreferences sp = getPreferences(CONFIG.Preferences.SELECTED_TIME);
        //SharedPreferences.Editor editor = sp.edit();
        //editor.putInt("Selected_Time", updateRateSeconds);
        //editor.commit();
    }

    public void readSelection() {
        //SharedPreferences sp = getPreferences(CONFIG.Preferences.SELECTED_TIME);
        //updateRateSeconds=CONFIG.Preferences.SELECTED_TIME;
        //if (updateRateSeconds==60)
        //if (updateRateSeconds==60)
        //r11.setChecked(true);
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //int abc = sharedPrefs.getInt(PREFS_NAME, updateRateSeconds);

        //final SharedPreferences config = getSharedPreferences(PREFS_NAME, 0);
        //SharedPreferences sharedPrefs = PreferenceManager
        //		.getDefaultSharedPreferences(this);
        //updateRateSeconds = sharedPrefs.getInt(PREFS_NAME, updateRateSeconds);

        //String upsec = Integer.toString(updateRateSeconds);

        //Toast.makeText(this,upsec,Toast.LENGTH_SHORT).show();
    }
}