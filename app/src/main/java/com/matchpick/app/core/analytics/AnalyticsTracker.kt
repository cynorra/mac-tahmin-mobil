package com.matchpick.app.core.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Ads & Firebase Analytics Dönüşüm Takip Yöneticisi (Conversion Tracker).
 *
 * Google Ads Evrensel Uygulama Kampanyalarında (UAC - App Campaigns) tCPA (Hedef EBM)
 * veya tROAS (Hedef ROAS) teklif stratejileri için kullanılan temel ve özel
 * dönüşüm metriklerini (Conversion Actions) kaydeder.
 */
@Singleton
class AnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    companion object {
        private const val TAG = "GoogleAdsAnalytics"
    }

    /**
     * 1. GOOGLE ADS TEMEL DÖNÜŞÜM: Maç Tahmini Gönderildi (Core In-App Action)
     * Google Ads App Kampanyalarında birincil dönüşüm hedefi (Primary Conversion) olarak atanır.
     */
    fun logPredictionSubmitted(
        fixtureId: String,
        pick: String,
        odds: Double = 1.0,
        league: String = "general"
    ) {
        val bundle = Bundle().apply {
            putString("fixture_id", fixtureId)
            putString("prediction_pick", pick)
            putDouble("odds", odds)
            putString("league", league)
            putLong("timestamp", System.currentTimeMillis())
            putString(FirebaseAnalytics.Param.ITEM_ID, fixtureId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, "Prediction_$pick")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "match_prediction")
        }
        firebaseAnalytics.logEvent("prediction_submitted", bundle)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        Log.d(TAG, "🎯 [CONVERSION] prediction_submitted: fixture=$fixtureId, pick=$pick, odds=$odds, league=$league")
    }

    /**
     * 2. GOOGLE ADS MONETİZASYON DÖNÜŞÜMÜ: Ödüllü Reklam İzlendi & VIP Kilit Açıldı
     * Reklam gelirini ve tROAS optimizasyonunu tetikleyen en değerli reklam dönüşüm metriğidir.
     */
    fun logVipPickUnlocked(
        fixtureId: String,
        recommendedPick: String = "",
        confidence: Int = 0,
        adUnitId: String = "ca-app-pub-3940256099942544/5224354917"
    ) {
        val bundle = Bundle().apply {
            putString("fixture_id", fixtureId)
            putString("recommended_pick", recommendedPick)
            putInt("confidence_rate", confidence)
            putString("ad_unit_id", adUnitId)
            putString("reward_type", "vip_unlock")
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("vip_pick_unlocked", bundle)
        firebaseAnalytics.logEvent("rewarded_ad_completed", bundle)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, Bundle().apply {
            putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId)
            putString(FirebaseAnalytics.Param.AD_FORMAT, "rewarded")
        })
        Log.d(TAG, "💎 [CONVERSION] vip_pick_unlocked / rewarded_ad: fixture=$fixtureId, pick=$recommendedPick, rate=$confidence%")
    }

    /**
     * 3. GOOGLE ADS VİRAL BÜYÜME DÖNÜŞÜMÜ: Referans Kodu Paylaşıldı / Kopyalandı
     * Viral organik edinim ve kullanıcı getirme (Referral Campaign) optimizasyonu.
     */
    fun logReferralAction(method: String, referralCode: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method) // "native_share" veya "clipboard_copy"
            putString("referral_code", referralCode)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
        firebaseAnalytics.logEvent("referral_action", bundle)
        Log.d(TAG, "🎁 [CONVERSION] referral_action: method=$method, code=$referralCode")
    }

    fun logShareEvent(platform: String, referralCode: String) {
        logReferralAction(platform, referralCode)
    }

    /**
     * 4. TOPLULUK ETKİLEŞİM DÖNÜŞÜMÜ: Yorum / Analiz Paylaşıldı
     */
    fun logCommunityPostPublished(region: String, hasAttachedPick: Boolean) {
        val bundle = Bundle().apply {
            putString("region", region)
            putBoolean("has_attached_pick", hasAttachedPick)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent("community_post_published", bundle)
        Log.d(TAG, "💬 [ENGAGEMENT] community_post_published: region=$region, hasPick=$hasAttachedPick")
    }

    /**
     * 5. TOPLULUK ETKİLEŞİMİ: Gönderi Beğenildi / Geri Alındı
     */
    fun logCommunityLikeToggled(postId: String, isLiked: Boolean) {
        val bundle = Bundle().apply {
            putString("post_id", postId)
            putBoolean("is_liked", isLiked)
        }
        firebaseAnalytics.logEvent("community_like_toggled", bundle)
        Log.d(TAG, "❤️ [ENGAGEMENT] community_like_toggled: postId=$postId, isLiked=$isLiked")
    }

    /**
     * 6. KULLANICI SEGMENTASYONU & DİL: Uygulama Dili Değiştirildi
     */
    fun logLanguageSwitched(newLanguage: String) {
        val bundle = Bundle().apply {
            putString("selected_language", newLanguage)
        }
        firebaseAnalytics.setUserProperty("app_language", newLanguage)
        firebaseAnalytics.logEvent("language_switched", bundle)
        Log.d(TAG, "🌍 [LOCALIZATION] language_switched: $newLanguage")
    }

    /**
     * 7. ONBOARDING & KULLANICI OTURUMU
     */
    fun logUserSessionStarted(userId: String, isGuest: Boolean) {
        firebaseAnalytics.setUserId(userId)
        firebaseAnalytics.setUserProperty("user_type", if (isGuest) "guest" else "registered")
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, if (isGuest) "anonymous" else "auth")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        Log.d(TAG, "🚀 [ONBOARDING] user_session_started: uid=$userId, isGuest=$isGuest")
    }

    /**
     * 8. PUAN KAZANIM DÖNÜŞÜMÜ (Virtual Currency)
     */
    fun logPointsEarned(points: Long, reason: String) {
        val bundle = Bundle().apply {
            putLong(FirebaseAnalytics.Param.VALUE, points)
            putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, "MatchPoints")
            putString("earn_reason", reason)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY, bundle)
        Log.d(TAG, "⭐ [POINTS] points_earned: +$points PTS (reason=$reason)")
    }

    /**
     * 9. SERİ KİLOMETRE TAŞI (Streak Retention Milestone)
     */
    fun logStreakMilestone(streakCount: Int) {
        val bundle = Bundle().apply {
            putLong("streak_count", streakCount.toLong())
        }
        firebaseAnalytics.logEvent("streak_milestone_$streakCount", bundle)
        Log.d(TAG, "🔥 [RETENTION] streak_milestone reached: $streakCount matches in a row!")
    }
}
