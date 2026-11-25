package com.matchpick.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MatchPickApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Crashlytics User Identifier bağlama
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        // AdMob SDK'sını initialize et (Gerçek üretim reklamları)
        try {
            MobileAds.initialize(this) { status ->
                android.util.Log.d("GoogleAdsAnalytics", "MobileAds initialized: $status")
            }
        } catch (e: Exception) {
            android.util.Log.e("GoogleAdsAnalytics", "MobileAds initialization error", e)
        }
    }
}
