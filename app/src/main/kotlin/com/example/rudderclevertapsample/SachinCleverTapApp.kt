package com.example.rudderclevertapsample

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.example.rudderclevertapsample.clevertap.CleverTapManager

/**
 * Application class – the single place where the CleverTap SDK is bootstrapped.
 */
class SachinCleverTapApp : Application(), CTPushNotificationListener {

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)


        CleverTapAPI.createNotificationChannel(getApplicationContext(),"1","ShubPN","Your Channel Description",NotificationManager.IMPORTANCE_MAX,true)

        // Initialize Product Config Variables capability
        CleverTapManager.initializeVariables(this)

        val cleverTap = CleverTapManager.instance(this)
        cleverTap?.enableDeviceNetworkInfoReporting(true)
        if (cleverTap == null) {
            Log.e(TAG, "CleverTap default instance is null. Fill CLEVERTAP_ACCOUNT_ID / CLEVERTAP_TOKEN ")
            return
        }

        cleverTap.setCTPushNotificationListener(this)
        cleverTap.syncVariables() // Sync variables layout map definition with dashboard panel
    }

    override fun onNotificationClickedPayloadReceived(payload: HashMap<String, Any>?) {
        Log.d(TAG, "Push notification clicked. Payload: $payload")
    }

    companion object {
        private const val TAG = "SachinCleverTapApp"
    }
}
