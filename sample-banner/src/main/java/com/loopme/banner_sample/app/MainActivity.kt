package com.loopme.banner_sample.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopme.LoopMeSdk
import com.loopme.LoopMeSdk.LoopMeSdkListener
import com.loopme.banner_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tryInitLoopMeSdk()
        setUpButtons()
    }

    private fun setUpButtons() {
        binding.banner.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    BannerActivity::class.java
                )
            )
        }
        binding.interstitial.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    InterstitialActivity::class.java
                )
            )
        }
    }

    private fun tryInitLoopMeSdk() {
        if (LoopMeSdk.isInitialized()) return
        alert("LoopMe SDK: initialization…")
        LoopMeSdk.initialize(this, LoopMeSdk.Configuration(), object : LoopMeSdkListener {
            override fun onSdkInitializationSuccess() {
                alert("LoopMe SDK: initialized")
            }

            override fun onSdkInitializationFail(errorCode: Int, message: String) {
                alert("LoopMe SDK: failed to initialize. Trying again…")
                tryInitLoopMeSdk()
            }
        })
    }

    private fun alert(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}