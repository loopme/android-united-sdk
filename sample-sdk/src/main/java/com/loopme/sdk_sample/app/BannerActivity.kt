package com.loopme.sdk_sample.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopme.LoopMeBanner
import com.loopme.common.LoopMeError
import com.loopme.sdk_sample.R
import com.loopme.sdk_sample.databinding.ActivityBannerNewBinding

class BannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerNewBinding
    private var mBanner: LoopMeBanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    public override fun onResume() {
        super.onResume()
        mBanner?.resume()
    }

    public override fun onPause() {
        super.onPause()
        mBanner?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBanner?.dismiss()
        mBanner?.destroy()
    }

    private fun setupViews() {
        binding.loadMpuBtn.setOnClickListener {
            loadBanner()
        }

        binding.loadLongitudinalBtn.setOnClickListener {
            loadBanner()
        }

        binding.destroyAdBtn.setOnClickListener {
            mBanner?.destroy()
        }
    }

    private fun loadBanner() {
        if (mBanner != null) {
            mBanner?.destroy()
            mBanner = null
        }

        var appKey = binding.appKeyEt.text.toString()
        if (appKey.isBlank()) {
            appKey = getString(R.string.default_mpu_app_key)
        }

        mBanner = LoopMeBanner.getInstance(appKey, this)
        mBanner?.let { banner ->
            banner.bindView(binding.bannerPlaceholder)
            banner.setListener(object : LoopMeBanner.Listener {
                override fun onLoopMeBannerLoadFail(
                    loopMeBanner: LoopMeBanner,
                    loopMeError: LoopMeError
                ) {
                    println(loopMeError.message)
                    Toast.makeText(
                        this@BannerActivity,
                        "LoadFail: " + loopMeError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onLoopMeBannerLoadSuccess(loopMeBanner: LoopMeBanner) {
                    banner.show()
                }

                override fun onLoopMeBannerShow(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerClicked(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerExpired(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerHide(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerLeaveApp(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerVideoDidReachEnd(loopMeBanner: LoopMeBanner) {}
            })
            banner.setAutoLoading(false)
            banner.load()
        }
    }
}