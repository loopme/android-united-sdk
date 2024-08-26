package com.loopme.sdk_sample.app

import android.content.Intent
import android.os.Bundle import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopme.LoopMeSdk
import com.loopme.LoopMeSdk.LoopMeSdkListener
import com.loopme.sdk_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initFromAnotherThread()
//        tryInitLoopMeSdk()
        setUpButtons()
    }

    private fun initFromAnotherThread() {
        Thread {
            println("@@@wjw operating on thread: ${Thread.currentThread().name}")
            tryInitLoopMeSdk()
        }.start()
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
        binding.initBtn.setOnClickListener {
            tryInitLoopMeSdk()
            tryInitLoopMeSdk()
        }

        binding.initThredBtn.setOnClickListener {
            initFromAnotherThread()
            initFromAnotherThread()
            tryInitLoopMeSdk()
        }
    }

    private fun tryInitLoopMeSdk() {
//        if (LoopMeSdk.isInitialized()) return
//        alert("LoopMe SDK: initialization…")
        LoopMeSdk.initialize(this, LoopMeSdk.Configuration(), object : LoopMeSdkListener {
            override fun onSdkInitializationSuccess() {
                println("@@@wjw LoopMe SDK: onSdkInitializationSuccess()")
//                alert("LoopMe SDK: initialized")
            }

            override fun onSdkInitializationFail(errorCode: Int, message: String) {
//                alert("LoopMe SDK: failed to initialize. Trying again…")
                println("@@@wjw LoopMe SDK: failed to initialize. Trying again…")
                tryInitLoopMeSdk()
            }
        })
    }

    private fun alert(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}