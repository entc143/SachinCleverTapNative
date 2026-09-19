package com.example.rudderclevertapsample.notifications

import android.os.Bundle
import android.util.Log
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CleverTapFcmService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            if (message.data.isNotEmpty()) {
                val extras = Bundle()
                for ((key, value) in message.data) {
                    extras.putString(key, value)
                }

                val info = CleverTapAPI.getNotificationInfo(extras)

                if (info.fromCleverTap) {
                    CleverTapAPI.createNotification(applicationContext, extras)
                    CleverTapAPI.getDefaultInstance(applicationContext)
                        ?.pushNotificationViewedEvent(extras)

                    Log.d(TAG, "CleverTap custom notification rendered and viewed tracked.")
                } else {
                    Log.d(TAG, "Non-CleverTap payload received.")
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Error handling custom push notification payload", t)
        }
    }

    override fun onNewToken(token: String) {
        CTFcmMessageHandler().onNewToken(applicationContext, token)
        Log.d(TAG, "FCM token refreshed and forwarded to CleverTap")
    }

    companion object {
        private const val TAG = "CleverTapFcmService"
    }
}
