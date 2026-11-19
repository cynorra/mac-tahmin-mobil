package com.matchpick.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MatchPickApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Crashlytics User Identifier bağlama
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        // AdMob SDK'sını arka planda initialize ederek ilk frame gecikmesini önleme
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MatchPickApplication) {}
        }
    }
}
