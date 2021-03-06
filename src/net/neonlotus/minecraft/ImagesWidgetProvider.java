package net.neonlotus.minecraft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ImagesWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_WIDGET_CONTROL = "com.mamlambo.ImagesWidget.WIDGET_CONTROL";
    private static final String LOG_TAG = "ImagesWidgetProvider";
    private static final String TAG = "Mine/ImagesWidgetProvider";
    public static final String URI_SCHEME = "images_widget";

   boolean doMakeActive = false;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        int[] images = getSelectedImages(context);

        for (int appWidgetId : appWidgetIds) {
            State state = getState(context, appWidgetId);

            int imageNum = (new java.util.Random().nextInt(images.length));

            RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.widget);
            remoteView.setImageViewResource(R.id.image, images[imageNum]);

            // modify remoteView based on current state
            updateDisplayState(context, remoteView, state, appWidgetId);
        }
    }

    public int[] getSelectedImages(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(CONFIG.Preferences.MOBPREFS, Context.MODE_PRIVATE);
        String ids = sp.getString(CONFIG.Preferences.SELECTED_MOBS, null);
        int[] toRet;
        if (ids != null) {
            Log.d(TAG, "load ids: " + ids);

            String[] splitIds = ids.split("_");
            toRet = new int[splitIds.length];

            if (splitIds.length > 0) {

                for (int i = 0, len = splitIds.length; i < len; i++) {
                    Log.d(TAG, "found id: " + splitIds[i]);

                    toRet[i] = CONFIG.IMAGES[Integer.parseInt(splitIds[i])];
                }

                return toRet;

            }
        }
        return CONFIG.IMAGES;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // stop alarm
            setAlarm(context, appWidgetId, -1);
            // remove preference
            // remove our stored state
            deleteStateForId(context, appWidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

    public void onHandleAction(Context context, int appWidgetId, Uri data) {
        State state = getState(context, appWidgetId);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        String controlType = data.getFragment();
        // actions
        if (controlType.equalsIgnoreCase("active")) {
            state.controlsActive = (state.controlsActive ? false : true);
        } else if (controlType.equalsIgnoreCase("playpause")) {
            state.paused = (state.paused ? false : true);
            if (state.paused) {
                setAlarm(context, appWidgetId, -1);
            } else {
                setAlarm(context, appWidgetId, state.updateRateSeconds);
            }
        } else if (controlType.equalsIgnoreCase("next")) {

            int images[] = getSelectedImages(context);

            int imageNum = (new java.util.Random().nextInt(images.length));
            remoteView.setImageViewResource(R.id.image, images[imageNum]);
        }
        // save the new state of things
        storeState(context, appWidgetId, state);
        // display
        updateDisplayState(context, remoteView, state, appWidgetId);
    }

    private class State {
        boolean controlsActive;
        boolean paused;
        int updateRateSeconds; // readonly in here
    }

    public void updateDisplayState(Context context, RemoteViews remoteView,
                                   State state, int appWidgetId) {


        Intent configIntent = null;
        if (state.controlsActive) {
            //testingngngnginging
            /*remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.widget);*/

            remoteView.setViewVisibility(R.id.controls_frame, View.VISIBLE);
            remoteView
                    .setOnClickPendingIntent(R.id.play_pause,
                            makeControlPendingIntent(context, "playpause",
                                    appWidgetId));
            remoteView.setOnClickPendingIntent(R.id.next,
                    makeControlPendingIntent(context, "next", appWidgetId));

            configIntent = new Intent(context, ImagesWidgetConfiguration.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            // gotta make this unique for this appwidgetid
            configIntent.setData(Uri.withAppendedPath(Uri
                    .parse(ImagesWidgetProvider.URI_SCHEME + "://widget/id/"),
                    String.valueOf(appWidgetId)));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.config, pendingIntent);
            if (state.paused) {
                remoteView.setImageViewResource(R.id.play_pause,
                        R.drawable.ic_menu_play_clip);
            } else {
                remoteView.setImageViewResource(R.id.play_pause,
                        R.drawable.ic_menu_stop);
            }
        } else {
            remoteView.setViewVisibility(R.id.controls_frame, View.GONE);
            //testingngngnginging
            /*remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.widget);*/
        }

        remoteView.setOnClickPendingIntent(R.id.widget_frame,
                makeControlPendingIntent(context, "active", appWidgetId));
        //assign pending intents in remote views?

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId,
                remoteView);
    }

    private State getState(Context context, int appWidgetId) {
        State state = new State();
        SharedPreferences config = context.getSharedPreferences(
                ImagesWidgetConfiguration.PREFS_NAME, 0);

        state.updateRateSeconds = config.getInt(String.format(
                ImagesWidgetConfiguration.PREFS_UPDATE_RATE_FIELD_PATTERN,
                appWidgetId), -1);
        state.paused = config.getBoolean(String.format(
                ImagesWidgetConfiguration.PREFS_PAUSED_FIELD_PATTERN,
                appWidgetId), false);
        state.controlsActive = config.getBoolean(String.format(
                ImagesWidgetConfiguration.PREFS_CONTROLS_ACTIVE_FIELD_PATTERN,
                appWidgetId), false);

        return state;
    }

    // store the updated state, except updateRateSeconds; that's only updated by
    // the configuration activity
    private void storeState(Context context, int appWidgetId, State state) {
        SharedPreferences config = context.getSharedPreferences(
                ImagesWidgetConfiguration.PREFS_NAME, 0);
        SharedPreferences.Editor edit = config.edit();
        edit.putBoolean(String.format(
                ImagesWidgetConfiguration.PREFS_PAUSED_FIELD_PATTERN,
                appWidgetId), state.paused);
        edit.putBoolean(String.format(
                ImagesWidgetConfiguration.PREFS_CONTROLS_ACTIVE_FIELD_PATTERN,
                appWidgetId), state.controlsActive);
        edit.commit();
    }

    private void deleteStateForId(Context context, int appWidgetId) {
        SharedPreferences config = context.getSharedPreferences(
                ImagesWidgetConfiguration.PREFS_NAME, 0);
        SharedPreferences.Editor edit = config.edit();
        edit.remove(String.format(
                ImagesWidgetConfiguration.PREFS_PAUSED_FIELD_PATTERN,
                appWidgetId));
        edit.remove(String.format(
                ImagesWidgetConfiguration.PREFS_CONTROLS_ACTIVE_FIELD_PATTERN,
                appWidgetId));
        edit.remove(String.format(
                ImagesWidgetConfiguration.PREFS_UPDATE_RATE_FIELD_PATTERN,
                appWidgetId));
        edit.commit();
    }

    private PendingIntent makeControlPendingIntent(Context context,
                                                   String command, int appWidgetId) {
        Intent active = new Intent();
        active.setAction(ACTION_WIDGET_CONTROL);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/#"
                + command), String.valueOf(appWidgetId));
        active.setData(data);
        return (PendingIntent.getBroadcast(context, 0, active,
                PendingIntent.FLAG_ONE_SHOT));
    }

    @Override
    public void onReceive(Context context, Intent intent) {



        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[]{appWidgetId});
            }
        } else if (ACTION_WIDGET_CONTROL.equals(action)) {
            // pass this on to the action handler where we'll figure out what to
            // do and update the widget
            final int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

                this.onHandleAction(context, appWidgetId, intent.getData());
            }

        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            // if the scheme doesn't match, that means it wasn't from the alarm
            // either it's the first time in (even before the configuration
            // is done) or after a reboot or update
            if (!URI_SCHEME.equals(intent.getScheme())) {
                final int[] appWidgetIds = intent.getExtras().getIntArray(
                        AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null) {
                    for (int appWidgetId : appWidgetIds) {
                        // get the user settings for how long to schedule the update
                        // time for
                        SharedPreferences config = context.getSharedPreferences(
                                ImagesWidgetConfiguration.PREFS_NAME, 0);
                        int updateRateSeconds = config
                                .getInt(
                                        String
                                                .format(
                                                        ImagesWidgetConfiguration.PREFS_UPDATE_RATE_FIELD_PATTERN,
                                                        appWidgetId), -1);
                        if (updateRateSeconds != -1) {
                            Log.i(LOG_TAG, "Starting recurring alarm for id "
                                    + appWidgetId);

                            setAlarm(context, appWidgetId, updateRateSeconds);
                        }
                    } /*this guy*/
                } else {
                    Toast.makeText(context, "All", Toast.LENGTH_SHORT).show();
                }
            }
            super.onReceive(context, intent);
        } else {
            super.onReceive(context, intent);
        }
    }

    private void setAlarm(Context context, int appWidgetId,
                          int updateRateSeconds) {
        Intent widgetUpdate = new Intent();
        widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[]{appWidgetId});

        // make this pending intent unique by adding a scheme to it
        widgetUpdate.setData(Uri.withAppendedPath(Uri
                .parse(ImagesWidgetProvider.URI_SCHEME + "://widget/id/"),
                String.valueOf(appWidgetId)));
        PendingIntent newPending = PendingIntent.getBroadcast(context, 0,
                widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // schedule the updating
        AlarmManager alarms = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        if (updateRateSeconds >= 0) {
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock
                    .elapsedRealtime(), updateRateSeconds * 1000, newPending);
        } else {
            alarms.cancel(newPending);
        }
    }



}