package com.example.rudderclevertapsample.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.rudderclevertapsample.R
import com.example.rudderclevertapsample.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val timeoutRunnable = Runnable { startMainActivity() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoView: VideoView = binding.videoView
        val videoUri = ("android.resource://" + packageName + "/" + R.raw.splash_video).toUri()
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        binding.root.postDelayed(timeoutRunnable, 2000)

        videoView.setOnErrorListener { _, _, _ ->
            binding.root.removeCallbacks(timeoutRunnable)
            startMainActivity()
            true
        }

        videoView.start()
    }

    override fun onDestroy() {
        binding.root.removeCallbacks(timeoutRunnable)
        super.onDestroy()
    }

    private fun startMainActivity() {
        if (!isFinishing) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
