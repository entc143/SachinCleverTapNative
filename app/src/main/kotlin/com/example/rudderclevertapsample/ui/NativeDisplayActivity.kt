package com.example.rudderclevertapsample.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.InAppNotificationListener
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit
import com.clevertap.android.sdk.inapp.CTInAppNotification
import com.example.rudderclevertapsample.R
import com.example.rudderclevertapsample.clevertap.CleverTapManager
import com.example.rudderclevertapsample.clevertap.SampleData
import com.example.rudderclevertapsample.databinding.ActivityNativeDisplayBinding
import com.example.rudderclevertapsample.databinding.ItemDisplayUnitBinding

class NativeDisplayActivity : AppCompatActivity(), DisplayUnitListener, InAppNotificationListener {

    private lateinit var binding: ActivityNativeDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Glide.with(this)
            .load(SampleData.DEFAULT_NATIVE_DISPLAY_IMAGE_URL)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .into(binding.imgDefault)

        CleverTapManager.setDisplayUnitListener(this, this)
        CleverTapManager.instance(this)?.setInAppNotificationListener(this)

        renderDisplayUnits(CleverTapManager.cachedDisplayUnits(this))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDisplayUnitsLoaded(units: ArrayList<CleverTapDisplayUnit>?) {
        Log.d(TAG, "onDisplayUnitsLoaded: ${units?.size ?: 0} unit(s)")
        runOnUiThread { renderDisplayUnits(units.orEmpty()) }
    }

    private fun renderDisplayUnits(units: List<CleverTapDisplayUnit>) {
        val container = binding.containerDisplayUnits
        container.removeAllViews()

        val hasUnits = units.isNotEmpty()
        binding.txtNoUnits.visibility = if (hasUnits) View.GONE else View.VISIBLE
        binding.imgDefault.visibility = if (hasUnits) View.GONE else View.VISIBLE

        val inflater = LayoutInflater.from(this)
        units.forEach { unit ->
            unit.contents.orEmpty().forEach { content ->
                val item = ItemDisplayUnitBinding.inflate(inflater, container, false)

                item.txtTitle.text = content.title.orEmpty()
                item.txtMessage.text = content.message.orEmpty()
                parseColor(content.titleColor)?.let { item.txtTitle.setTextColor(it) }
                parseColor(content.messageColor)?.let { item.txtMessage.setTextColor(it) }
                parseColor(unit.bgColor)?.let { item.card.setCardBackgroundColor(it) }

                if (content.mediaIsImage() || content.mediaIsGIF()) {
                    item.imgMedia.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(content.media)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(item.imgMedia)
                } else {
                    item.imgMedia.visibility = View.GONE
                }

                item.root.setOnClickListener {
                    CleverTapManager.trackDisplayUnitClicked(this, unit.unitID)
                    content.actionUrl?.takeIf { it.isNotBlank() }?.let { url ->
                        runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                            .onFailure { Log.w(TAG, "Cannot open action url $url", it) }
                    }
                }
                container.addView(item.root)
            }

            CleverTapManager.trackDisplayUnitViewed(this, unit.unitID)
            Log.d(TAG, "Rendered unit ${unit.unitID}, custom KV: ${unit.customExtras}")
        }
    }

    private fun parseColor(hex: String?): Int? =
        hex?.takeIf { it.isNotBlank() }?.let { runCatching { Color.parseColor(it) }.getOrNull() }

    override fun beforeShow(extras: Map<String, Any>?): Boolean {
        Log.d(TAG, "In-App beforeShow, extras=$extras")
        return true
    }

    override fun onShow(ctInAppNotification: CTInAppNotification?) {
        Log.d(TAG, "In-App shown: ${ctInAppNotification?.campaignId}")
    }

    override fun onDismissed(extras: Map<String, Any>?, actionExtras: Map<String, Any>?) {
        Log.d(TAG, "In-App dismissed, extras=$extras actionExtras=$actionExtras")
    }

    companion object {
        private const val TAG = "NativeDisplayActivity"
    }
}
