package com.example.rudderclevertapsample.clevertap

import android.content.Context
import android.util.Log
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CTInboxStyleConfig
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.variables.Var
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit

object CleverTapManager {

    private const val TAG = "CleverTapManager"

    fun instance(context: Context): CleverTapAPI? =
        CleverTapAPI.getDefaultInstance(context.applicationContext)

    fun setProfile(context: Context, profile: Map<String, Any> = SampleData.sampleProfile()) {
        val ct = instance(context) ?: return logMissing("onUserLogin")
        ct.onUserLogin(profile)
        Log.d(TAG, "onUserLogin -> $profile")
    }

    fun pushEvent( context: Context, eventName: String, properties: Map<String, Any>? = null) {
        val ct = instance(context) ?: return logMissing("pushEvent($eventName)")
        if (properties.isNullOrEmpty()) ct.pushEvent(eventName) else ct.pushEvent(eventName, properties)
        Log.d(TAG, "pushEvent -> $eventName $properties")
    }

    fun pushAddToSachinEvent(context: Context) =
        pushEvent(context, SampleData.EVENT_ADD_TO_SACHIN, SampleData.sampleEventProperties())

    fun initializeInbox(context: Context, listener: CTInboxListener) {
        val ct = instance(context) ?: return logMissing("initializeInbox")
        ct.setCTNotificationInboxListener(listener)
        ct.initializeInbox()
    }

    fun inboxUnreadCount(context: Context): Int = instance(context)?.inboxMessageUnreadCount ?: 0

    fun showAppInbox(context: Context) {
        val ct = instance(context) ?: return logMissing("showAppInbox")
        val style = CTInboxStyleConfig().apply {
            navBarTitle = "Sachin's Inbox"
            navBarTitleColor = "#FFFFFF"
            navBarColor = "#1E88E5"
            backButtonColor = "#FFFFFF"
            inboxBackgroundColor = "#F5F7FA"
            noMessageViewText = "No messages yet"
            noMessageViewTextColor = "#616161"
            firstTabTitle = "All"
            tabs = arrayListOf("Offers", "Updates")
            tabBackgroundColor = "#1E88E5"
            selectedTabColor = "#FFFFFF"
            unselectedTabColor = "#BBDEFB"
            selectedTabIndicatorColor = "#FFFFFF"
        }
        ct.showAppInbox(style)
    }

    fun setDisplayUnitListener(context: Context, listener: DisplayUnitListener) {
        val ct = instance(context) ?: return logMissing("setDisplayUnitListener")
        ct.setDisplayUnitListener(listener)
    }

    fun cachedDisplayUnits(context: Context): List<CleverTapDisplayUnit> =
        instance(context)?.allDisplayUnits ?: emptyList()

    fun trackDisplayUnitViewed(context: Context, unitId: String) {
        instance(context)?.pushDisplayUnitViewedEventForID(unitId)
    }

    fun trackDisplayUnitClicked(context: Context, unitId: String) {
        instance(context)?.pushDisplayUnitClickedEventForID(unitId)
    }

    fun isPushPermissionGranted(context: Context): Boolean =
        instance(context)?.isPushPermissionGranted ?: false

    // ------------------------------------------------------------------
    // Remote Config / Variables
    // ------------------------------------------------------------------
    var varColor: Var<String>? = null
    var varProductName: Var<String>? = null
    var varAppIcon: Var<String>? = null

    fun initializeVariables(context: Context) {
        val ct = instance(context) ?: return
        varColor = ct.defineVariable("Color", "Grey")
        varProductName = ct.defineVariable("ProductName", "clevertap")
        varAppIcon = ct.defineFileVariable("AppIcon")
    }

    private fun logMissing(what: String) {
        Log.w(TAG, "Skipping $what – CleverTap instance is null")
    }
}
