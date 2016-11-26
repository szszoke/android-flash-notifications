package szoke.flashnotifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "flashnotifications";

    private HashSet<String> notifications;

    private HashSet<String> excludedApps;
    private HashSet<String> excludedCategories;

    private boolean screenOn = true;

    private AlarmManager alarmManager;
    private BroadcastReceiver screenStateReceiver;
    private PendingIntent notifierIntent;

    int[] repeatIntervals;
    private int repeatIntervalIndex = 0;

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Listener connected");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notifications = new HashSet<>();

        Resources resources = getResources();

        // Getting the predefined list of excluded apps
        excludedApps = new HashSet<>(Arrays.asList(resources.getStringArray(R.array.excludedApps)));

        // Getting the predefined list of excluded categories
        excludedCategories = new HashSet<>(Arrays.asList(resources.getStringArray(R.array.excludedCategories)));

        // Getting the predefined repeat intervals
        repeatIntervals = resources.getIntArray(R.array.defaultRepeatIntervals);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        screenStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.i(TAG, "Screen on");

                    screenOn = true;

                    // No need for stopping the flashing if there isn't any
                    if (!notifications.isEmpty()) {
                        stopNotification();
                    }
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.i(TAG, "Screen off");

                    screenOn = false;

                    // There were some notifications in the set
                    // Which means it's time to blink the flash
                    if (!notifications.isEmpty()) {
                        scheduleNotification();
                    }
                }
            }
        };

        IntentFilter screenStateReceiverFilter = new IntentFilter("screenStateChanged");
        screenStateReceiverFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateReceiverFilter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenStateReceiver, screenStateReceiverFilter);

        BroadcastReceiver restartNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // The blinking cycle has ended.
                // Time to schedule a new one
                scheduleNotification();
            }
        };

        registerReceiver(restartNotificationReceiver, new IntentFilter("restartNotification"));

        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
        unregisterReceiver(screenStateReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Notification notification = sbn.getNotification();
        Log.i(TAG, "Notification posted: " + sbn.getKey() + " " + sbn.getNotification().priority + " " + sbn.isClearable() + " " + notification.ledARGB + " " + notification.category);

        if (notification.category != null &&
                !excludedCategories.contains(notification.category) &&
                !excludedApps.contains(sbn.getPackageName())) {
            if (notifications.add(sbn.getKey()) && !screenOn) {
                scheduleNotification();
            }

            repeatIntervalIndex = 0;

            Log.i(TAG, "Active notification count: " + String.valueOf(notifications.size()));
        } else {
            Log.i(TAG, "Excluded app/category: " + sbn.getPackageName() + ", " + notification.category);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Notification dismissed: " + sbn.getKey());

        if (notifications.contains(sbn.getKey())) {
            notifications.remove(sbn.getKey());

            Log.i(TAG, "Active notification count: " + String.valueOf(notifications.size()));

            // Every notification has been dismissed
            // The flashing could stop for now
            if (notifications.isEmpty()) {
                stopNotification();
            }
        }
    }

    private void scheduleNotification() {
        Log.i(TAG, "Scheduling flash notification");

        Intent flasherIntent = new Intent(getApplication(), FlashNotificationService.class);
        notifierIntent = PendingIntent.getService(getApplication(), 0, flasherIntent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatIntervals[repeatIntervalIndex], notifierIntent);

        Log.i(TAG, "Current interval: " + repeatIntervals[repeatIntervalIndex] + ", index: " + repeatIntervalIndex);

        if (repeatIntervalIndex < repeatIntervals.length)
            repeatIntervalIndex++;
    }

    private void stopNotification() {
        Log.i(TAG, "Stopping flash notification");
        alarmManager.cancel(notifierIntent);
        repeatIntervalIndex = 0;
    }
}
