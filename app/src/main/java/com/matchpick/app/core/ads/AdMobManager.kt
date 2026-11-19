package com.matchpick.app.core.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private var interstitialAd: InterstitialAd? = null
    private var lastInterstitialShownTime: Long = 0
    private const val MIN_INTERVAL_MS = 240_000L // 4 dakika frekans kısıtı

    fun loadInterstitial(context: Context, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (interstitialAd != null && (currentTime - lastInterstitialShownTime) > MIN_INTERVAL_MS) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialShownTime = System.currentTimeMillis()
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onAdDismissed()
        }
    }
}

@Composable
fun AdaptiveBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                this.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        android.util.Log.d("GoogleAdsAnalytics", "🎯 [CONVERSION] Banner Ad Clicked: unit=$adUnitId")
                    }
                    override fun onAdImpression() {
                        super.onAdImpression()
                        android.util.Log.d("GoogleAdsAnalytics", "👁️ [CONVERSION] Banner Ad Impression: unit=$adUnitId")
                    }
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        android.util.Log.w("GoogleAdsAnalytics", "Banner ad failed to load: ${error.message}")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
