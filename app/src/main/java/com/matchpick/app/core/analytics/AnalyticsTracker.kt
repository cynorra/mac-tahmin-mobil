package com.matchpick.app.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    // İlk tahmin kaydedildiğinde (En kritik onboarding dönüşümü)
    fun logFirstPredictionMade(fixtureId: String, pick: String) {
        val bundle = Bundle().apply {
            putString("fixture_id", fixtureId)
            putString("prediction_pick", pick)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("first_prediction_made", bundle)
    }

    // Sosyal medyaya viral paylaşım yapıldığında
    fun logShareEvent(platform: String, referralCode: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, platform)
            putString("referral_code", referralCode)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    // Ödüllü reklam izleme tamamlandığında
    fun logRewardedAdCompleted(adUnitId: String, rewardType: String) {
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("reward_type", rewardType)
        }
        firebaseAnalytics.logEvent("rewarded_ad_completed", bundle)
    }

    // Seri kilometre taşı (Örn: 3 maç, 5 maç art arda bilindiğinde)
    fun logStreakMilestone(streakCount: Int) {
        val bundle = Bundle().apply {
            putLong("streak_count", streakCount.toLong())
        }
        firebaseAnalytics.logEvent("streak_milestone_$streakCount", bundle)
    }
}
