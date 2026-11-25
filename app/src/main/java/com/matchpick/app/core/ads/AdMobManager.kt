package com.matchpick.app.core.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object AdMobManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false
    private var lastInterstitialShownTime: Long = 0
    private var retryAttempt = 0
    private const val MIN_INTERVAL_MS = 240_000L // 4 dakika frekans kısıtı
    private val handler = Handler(Looper.getMainLooper())

    private val _consentReady = kotlinx.coroutines.flow.MutableStateFlow(false)
    val consentReady: kotlinx.coroutines.flow.StateFlow<Boolean> = _consentReady

    /** UMP (GDPR/AB-UK) onayı alındıysa veya gerekmiyorsa true. */
    fun canRequestAds(context: Context): Boolean =
        UserMessagingPlatform.getConsentInformation(context).canRequestAds()

    /**
     * Açılışta UMP onay formunu gerekiyorsa gösterir; reklam istemek serbestse [onReady] çağrılır.
     * AdMob konsolunda "Gizlilik ve mesajlaşma" altında GDPR mesajı yayınlanmış olmalıdır.
     */
    fun gatherConsent(activity: Activity, onReady: () -> Unit) {
        val info = UserMessagingPlatform.getConsentInformation(activity)
        if (info.canRequestAds()) {
            _consentReady.value = true
        }
        info.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        android.util.Log.w("GoogleAdsAnalytics", "UMP form error: ${formError.message}")
                    }
                    if (info.canRequestAds()) {
                        _consentReady.value = true
                        onReady()
                    }
                }
            },
            { error ->
                android.util.Log.w("GoogleAdsAnalytics", "UMP info update failed: ${error.message}")
                // UMP mesajı henüz AdMob panelinde yapılandırılmadıysa veya ağ hatası varsa bile reklam yüklemesini bloklama
                _consentReady.value = true
                onReady()
            }
        )
    }

    /** EEA/UK'de kullanicinin onay tercihini degistirebilmesi icin UMP gizlilik secenekleri gerekli mi? */
    fun isPrivacyOptionsRequired(context: Context): Boolean =
        UserMessagingPlatform.getConsentInformation(context).privacyOptionsRequirementStatus ==
            com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                android.util.Log.w("GoogleAdsAnalytics", "UMP privacy options error: ${formError.message}")
            }
        }
    }

    fun loadInterstitial(context: Context, adUnitId: String = AdConfig.INTERSTITIAL_AD_UNIT_ID) {
        val appContext = context.applicationContext
        if (isLoadingInterstitial || interstitialAd != null) return
        if (!canRequestAds(appContext) && !_consentReady.value) return
        isLoadingInterstitial = true
        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoadingInterstitial = false
                    retryAttempt = 0
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingInterstitial = false
                    interstitialAd = null
                    // Üstel geri çekilme ile yeniden dene (max 64 sn)
                    retryAttempt++
                    val delay = (1L shl minOf(retryAttempt, 6)) * 1000L
                    handler.postDelayed({ loadInterstitial(appContext, adUnitId) }, delay)
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        val now = System.currentTimeMillis()
        if (ad == null || (now - lastInterstitialShownTime) <= MIN_INTERVAL_MS) {
            if (ad == null) loadInterstitial(activity)
            onAdDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastInterstitialShownTime = System.currentTimeMillis()
                loadInterstitial(activity)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadInterstitial(activity)
                onAdDismissed()
            }
        }
        ad.show(activity)
    }
}

@Composable
fun AdaptiveBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp
    val consentReady by AdMobManager.consentReady.collectAsState()
    val adView = remember(adUnitId, widthDp) {
        AdView(context).apply {
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
            this.adUnitId = adUnitId
            this.adListener = object : AdListener() {
                override fun onAdClicked() {
                    android.util.Log.d("GoogleAdsAnalytics", "[CONVERSION] Banner Ad Clicked: unit=$adUnitId")
                }
                override fun onAdImpression() {
                    android.util.Log.d("GoogleAdsAnalytics", "[CONVERSION] Banner Ad Impression: unit=$adUnitId")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    android.util.Log.w("GoogleAdsAnalytics", "Banner ad failed to load: ${error.message}")
                }
            }
        }
    }
    androidx.compose.runtime.LaunchedEffect(adView, consentReady) {
        if (consentReady || AdMobManager.canRequestAds(context)) {
            adView.loadAd(AdRequest.Builder().build())
        }
    }
    DisposableEffect(adView) { onDispose { adView.destroy() } }

    AndroidView(modifier = modifier.fillMaxWidth(), factory = { adView })
}
