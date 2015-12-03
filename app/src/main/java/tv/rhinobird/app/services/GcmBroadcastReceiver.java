package tv.rhinobird.app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import tv.rhinobird.app.MainActivity;
import tv.rhinobird.app.R;

/**
 * Created by emilio on 5/28/15.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    protected static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

    private Context mContext;
    private Bundle extras;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        extras = intent.getExtras();

        if (extras != null) {
            String notificationType = extras.getString("notificationType");
            if (notificationType != null && notificationType.equals("new_live_stream")) {
                showStreamNotification();
            }
        }

    }

    private void showStreamNotification() {
        String caption = extras.getString("caption");
        String url = extras.getString("url");
        String username = extras.getString("username");

        PendingIntent resultPendingIntent = getPendingIntent(url);
        // Vibration options
        long[] pattern = new long[]{500, 500};

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setContentIntent(resultPendingIntent)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(username + " is going live!")
                        .setVibrate(pattern)
                        .setColor(ContextCompat.getColor(mContext, R.color.red))
                        .setContentText(caption);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private int getNotificationIcon() {
        boolean whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        return whiteIcon ? R.drawable.ic_notif : R.mipmap.ic_launcher;
    }

    private PendingIntent getPendingIntent(String url) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.putExtra("url", url);

        return PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
