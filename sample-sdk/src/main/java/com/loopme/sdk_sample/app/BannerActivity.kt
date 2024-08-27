package com.loopme.sdk_sample.app

import android.os.Bundle
import android.widget.FrameLayout
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
        dismissAndDestroyBanner()
    }

    private fun setupViews() {
        binding.loadMpuBtn.setOnClickListener {
            loadMPU()
        }

        binding.loadLongitudinalBtn.setOnClickListener {
            loadLongitudinal()

        }

        binding.destroyAdBtn.setOnClickListener {
            dismissAndDestroyBanner()
            enableLoadingButtons()
        }
    }

    private fun loadLongitudinal() {
        with(binding) {
            mpuPlaceholder.makeGone()
            longitudinalBannerPlaceholder.makeVisible()
            loadBanner(longitudinalBannerPlaceholder)
        }
    }

    private fun loadMPU() {
        with(binding) {
            mpuPlaceholder.makeVisible()
            longitudinalBannerPlaceholder.makeGone()
            loadBanner(mpuPlaceholder)
        }
    }

    private fun loadBanner(bannerPlaceholder: FrameLayout) {
        disableLoadingButtons()
        if (mBanner != null) {
            dismissAndDestroyBanner()
        }

        var appKey = binding.appKeyEt.text.toString()
        if (appKey.isBlank()) {
            appKey = getString(R.string.default_mpu_app_key)
        }

        mBanner = LoopMeBanner.getInstance(appKey, this)
        mBanner?.let { banner ->
            banner.bindView(bannerPlaceholder)
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
                    enableLoadingButtons()
                }

                override fun onLoopMeBannerLoadSuccess(loopMeBanner: LoopMeBanner) {
                    banner.show()
                }

                override fun onLoopMeBannerShow(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerClicked(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerExpired(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerHide(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerLeaveApp(loopMeBanner: LoopMeBanner) {}
                override fun onLoopMeBannerVideoDidReachEnd(loopMeBanner: LoopMeBanner) {
                    enableLoadingButtons()
                    dismissAndDestroyBanner()
                }
            })
            banner.setAutoLoading(false)
            banner.load()
        }
    }

    private fun dismissAndDestroyBanner() {
        binding.longitudinalBannerPlaceholder.makeGone()
        binding.mpuPlaceholder.makeGone()
        mBanner?.dismiss()
        mBanner?.destroy()
    }

    private fun disableLoadingButtons() {
        binding.loadMpuBtn.disable()
        binding.loadLongitudinalBtn.disable()
    }

    private fun enableLoadingButtons() {
        binding.loadMpuBtn.enable()
        binding.loadLongitudinalBtn.enable()
    }
}