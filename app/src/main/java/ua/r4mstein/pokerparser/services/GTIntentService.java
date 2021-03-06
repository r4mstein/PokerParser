package ua.r4mstein.pokerparser.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import ua.r4mstein.pokerparser.GetData;
import ua.r4mstein.pokerparser.MainActivity;
import ua.r4mstein.pokerparser.MyModel;
import ua.r4mstein.pokerparser.R;

public class GTIntentService extends IntentService {
    public static final String TAG = "GTIntentService";
    public static final int GT_INTERVAL = 1000 * 60;

    public static final String MODELS_FROM_GT_LINK1 = "models_from_gt_link1";
    public static final String KEY_GT_LINK = "key_gt_link";
    private static final String IS_FIRST_GT = "is_first_gt";

    private SharedPreferences mPreferences;
    private boolean mIsFirstMonitoring = true;

    private ArrayList<MyModel> myModels = new ArrayList<>();
    private ArrayList<MyModel> myModelsFromShared = new ArrayList<>();

    public static Intent newIntent(Context context) {
        return new Intent(context, GTIntentService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = GTIntentService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), GT_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public GTIntentService() {
        super("GTIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleGT_Intent: " + intent);

        if (MainActivity.isConnected(getApplicationContext())) {
            if (intent != null) {

                myModels.clear();
                GetData.getDataFromGTLinks(MainActivity.GT_LINK_1, myModels);

                mPreferences = getSharedPreferences(MODELS_FROM_GT_LINK1,
                        getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();

                if (didFirstMonitoring()) {
                    addModelToSharedPreferences(editor, myModels);
                    markNotFirstMonitoring();
                } else {
                    myModelsFromShared.clear();
                    for (int i = 0; i < 5; i++) {
                        String modelFromPref = mPreferences.getString(KEY_GT_LINK + i, null);
                        MyModel myModel = MyModel.createMyModel(modelFromPref);

                        myModelsFromShared.add(myModel);
                        Log.i(TAG, "GT_MyModelFromShared -- " + myModel.getLinkTitle() +
                                " : " + myModel.getUser());
                    }

                    for (int j = 0; j < 5; j++) {
                        if (!(myModelsFromShared.contains(myModels.get(j)))) {

                            Resources resources = getResources();
                            Intent notificationIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(myModels.get(j).getLink()));
                            PendingIntent pi = PendingIntent.getActivity(this, 0,
                                    notificationIntent, 0);

                            Notification notification = new NotificationCompat.Builder(this)
                                    .setTicker(resources.getString(R.string.new_topic_gt))
                                    .setSmallIcon(R.mipmap.ic_launcher_gt)
                                    .setContentTitle(resources.getString(R.string.new_topic_gt))
                                    .setContentText(myModels.get(j).getLinkTitle() + "\n User: "
                                            + myModels.get(j).getUser())
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setContentIntent(pi)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                            myModels.get(j).getLinkTitle() + "\n User: "
                                                    + myModels.get(j).getUser()))
                                    .build();

                            long i = new Date().getTime();
                            NotificationManagerCompat managerCompat =
                                    NotificationManagerCompat.from(this);
                            managerCompat.notify((int) i, notification);

                            Log.i(TAG, "GT_myModels -- " + myModels.get(j).getLinkTitle() +
                                    " : " + myModels.get(j).getUser());
                        }
                    }
                    addModelToSharedPreferences(editor, myModels);
                }
            }
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Not connected to internet")
                    .setSmallIcon(R.mipmap.ic_launcher_gt)
                    .setContentTitle("Not connected to internet")
                    .setContentText("Please connect to internet")
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat managerCompat =
                    NotificationManagerCompat.from(this);
            managerCompat.notify(2, notification);
        }
    }

    private void addModelToSharedPreferences(SharedPreferences.Editor editor, ArrayList<MyModel> myModels) {
        for (int i = 0; i < 5; i++) {
            editor.putString(KEY_GT_LINK + i, myModels.get(i).serializeMyModel());
            editor.apply();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = GTIntentService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    private boolean didFirstMonitoring() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsFirstMonitoring = sharedPreferences.getBoolean(IS_FIRST_GT, true);
        return mIsFirstMonitoring;
    }

    private void markNotFirstMonitoring() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsFirstMonitoring = false;
        sharedPreferences.edit().putBoolean(IS_FIRST_GT, mIsFirstMonitoring).apply();
    }
}
