package com.example.rudderclevertapsample.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.variables.callbacks.VariablesChangedCallback
import com.example.rudderclevertapsample.R
import com.example.rudderclevertapsample.clevertap.CleverTapManager
import com.example.rudderclevertapsample.databinding.ActivityProdExperienceBinding

class ProdExperienceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdExperienceBinding
    private val variablesChangedCallback = object : VariablesChangedCallback() {
        override fun variablesChanged() {
            Log.d("ProdExperienceActivity", "CleverTap Remote Config Variables changed successfully.")
            updateUiWithVariables()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Pre-populate UI with current available variable values immediately
        updateUiWithVariables()

        // Register listener for live / fetched update notifications
        CleverTapManager.instance(this)?.addVariablesChangedCallback(variablesChangedCallback)

        // Request latest variable data states explicitly from the CleverTap dashboard
        CleverTapManager.instance(this)?.fetchVariables(null)
    }

    private fun updateUiWithVariables() {
        runOnUiThread {
            // Retrieve variable values with built-in fallbacks if wrappers are null or un-initialized
            val colorVal = CleverTapManager.varColor?.value() ?: "Grey"
            val productNameVal = CleverTapManager.varProductName?.value() ?: "clevertap"
            val appIconUrl = CleverTapManager.varAppIcon?.value().orEmpty()

            binding.txtColor.text = colorVal
            binding.txtProductName.text = productNameVal

            if (appIconUrl.isNotBlank()) {
                Glide.with(this@ProdExperienceActivity)
                    .load(appIconUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imgAppIcon)
            } else {
                // If AppIcon string config remains default/empty, use appicon launcher image directly
                binding.imgAppIcon.setImageResource(R.mipmap.ic_launcher)
            }
        }
    }

    override fun onDestroy() {
        CleverTapManager.instance(this)?.removeVariablesChangedCallback(variablesChangedCallback)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
