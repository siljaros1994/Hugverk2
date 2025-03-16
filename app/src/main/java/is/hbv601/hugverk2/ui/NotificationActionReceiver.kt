package `is`.hbv601.hugverk2.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_DISMISS_MATCH") {
            val notificationId = intent.getIntExtra("notificationId", 0)
            with(NotificationManagerCompat.from(context)) {
                cancel(notificationId)
            }
        }
    }
}