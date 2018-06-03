package com.example.voodoo.mikdemogeonoti;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceSerivce extends IntentService {


    public NotificationManagerCompat notificationManager;
    public NotificationCompat.Builder mBuilder;
    public static final String TAG = "geofenceSerivce";


    private NotificationCompat.Builder MakeNotiWithIntent(String Title, String Content){

        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return mBuilder = new NotificationCompat.Builder(this, "NotiID")
                .setSmallIcon(R.drawable.notiart)
                .setContentTitle(Title)
                .setContentText(Content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NotiID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public GeofenceSerivce() {
        super(TAG);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()){
            Log.d(TAG, "onHandleIntent: event error");
        }
        else {
            int transittion = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();

            if (transittion == geofence.GEOFENCE_TRANSITION_ENTER){
                Log.d(TAG, "onHandleIntent: enter geofence - " + requestId);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(123, MakeNotiWithIntent("Enter", "Du er gået ind").build());
            }else if(transittion == geofence.GEOFENCE_TRANSITION_EXIT){
                Log.d(TAG, "onHandleIntent: exit geofence - " + requestId);
                //notifikationer
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(123, MakeNotiWithIntent("Exit", "Du er gået ud").build());
            }
        }
    }
}
