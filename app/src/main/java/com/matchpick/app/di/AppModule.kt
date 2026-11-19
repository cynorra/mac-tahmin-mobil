package com.matchpick.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.matchpick.app.core.ads.AdConfig
import com.matchpick.app.core.social.SocialShareManager
import com.matchpick.app.data.local.FixtureDao
import com.matchpick.app.data.local.MatchPickDatabase
import com.matchpick.app.data.repository.AuthRepository
import com.matchpick.app.ui.screens.official.RewardedAdController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MatchPickDatabase {
        return Room.databaseBuilder(
            context,
            MatchPickDatabase::class.java,
            "matchpick_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFixtureDao(db: MatchPickDatabase): FixtureDao = db.fixtureDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepository(auth, firestore, context)

    @Provides
    @Singleton
    fun provideSocialShareManager(@ApplicationContext context: Context): SocialShareManager {
        return SocialShareManager(context)
    }

    @Provides
    @Singleton
    fun provideRewardedAdController(@ApplicationContext context: Context): RewardedAdController {
        return RewardedAdController(context).apply {
            loadAd(AdConfig.REWARDED_AD_UNIT_ID)
        }
    }
}
