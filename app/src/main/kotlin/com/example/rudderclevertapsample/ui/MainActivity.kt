package com.example.rudderclevertapsample.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.clevertap.android.sdk.CTInboxListener
import com.example.rudderclevertapsample.R
import com.example.rudderclevertapsample.clevertap.CleverTapManager
import com.example.rudderclevertapsample.clevertap.SampleData
import com.example.rudderclevertapsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CTInboxListener {

    private lateinit var binding: ActivityMainBinding
    private var inboxReady = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d(TAG, "POST_NOTIFICATIONS granted = $granted")
            setStatus(if (granted) "Push permission granted" else "Push permission denied")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CleverTapManager.initializeInbox(this, this)

        binding.btnProfileSet.setOnClickListener {
            CleverTapManager.setProfile(this)
            setStatus("onUserLogin sent for Identity=${SampleData.sampleProfile()["Identity"]}")
        }

        binding.btnEvent.setOnClickListener {
            CleverTapManager.pushAddToSachinEvent(this)
            setStatus("Event \"${SampleData.EVENT_ADD_TO_SACHIN}\" sent")
        }

        binding.btnNativeDisplay.setOnClickListener {
            startActivity(Intent(this, NativeDisplayActivity::class.java))
        }

        binding.btnProdExperience.setOnClickListener {
            startActivity(Intent(this, ProdExperienceActivity::class.java))
        }

        binding.btnInbox.setOnClickListener { openInbox() }

        requestNotificationPermissionIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        refreshInboxBadge()
    }

    override fun inboxDidInitialize() {
        Log.d(TAG, "inboxDidInitialize")
        inboxReady = true
        refreshInboxBadge()
    }

    override fun inboxMessagesDidUpdate() {
        Log.d(TAG, "inboxMessagesDidUpdate")
        refreshInboxBadge()
    }

    private fun openInbox() {
        if (!inboxReady) {
            Toast.makeText(this, R.string.inbox_not_ready, Toast.LENGTH_SHORT).show()
            return
        }
        CleverTapManager.showAppInbox(this)
    }

    private fun refreshInboxBadge() {
        val unread = CleverTapManager.inboxUnreadCount(this)
        runOnUiThread {
            binding.txtInboxBadge.text = unread.toString()
            binding.txtInboxBadge.visibility = if (unread > 0) View.VISIBLE else View.GONE
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setStatus(message: String) {
        binding.txtStatus.text = getString(R.string.status_prefix, message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
