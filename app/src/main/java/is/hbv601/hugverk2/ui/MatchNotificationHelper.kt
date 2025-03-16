package `is`.hbv601.hugverk2.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import `is`.hbv601.hugverk2.R
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object MatchNotificationHelper {
    private const val CHANNEL_ID = "match_notifications"
    private const val CHANNEL_NAME = "Match Notifications"

    // Here we create the notification channel
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for when a donor matches a recipient."
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Here we displays a notification for a match.
     *
     * @param context build the notification.
     * @param matchTitle The title of the notification.
     * @param matchMessage The body text.
     * @param notificationId An ID to identify the notification.
     */
    fun showMatchNotification(
        context: Context,
        matchTitle: String,
        matchMessage: String,
        notificationId: Int = 1001
    ) {
        val intent = Intent(context, RecipientMatchesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification.
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_match)  // Replace with your drawable resource.
            .setContentTitle(matchTitle)
            .setContentText(matchMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_match, "Match!", contentPendingIntent
            )
            .addAction(
                R.drawable.ic_keep_match, "Keep Match", contentPendingIntent
            )
            .addAction(
                R.drawable.ic_dismiss, "Dismiss", getDismissPendingIntent(context, notificationId)
            )

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    // Here we dismiss the notification.
    private fun getDismissPendingIntent(context: Context, notificationId: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_DISMISS_MATCH"
            putExtra("notificationId", notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
