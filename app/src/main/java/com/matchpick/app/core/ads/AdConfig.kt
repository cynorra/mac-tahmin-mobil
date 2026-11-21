package com.matchpick.app.core.ads

/**
 * MatchPick Pro - Central AdMob & Monetization Configuration
 *
 * Gerçek AdMob Yayıncı & Reklam Birim Kimlikleri:
 * - Yayıncı Kimliği (Publisher): pub-4674211063760769
 * - Uygulama Kimliği (App ID): ca-app-pub-4674211063760769~9806710318
 * - Banner Reklam ID: ca-app-pub-4674211063760769/3533393113
 * - Ödüllü Reklam (Rewarded Video) ID: ca-app-pub-4674211063760769/7825533906
 * - Geçiş Reklamı (Interstitial) ID: ca-app-pub-4674211063760769/1395856920
 *
 * Tüm derlemelerde (debug dahil) gerçek üretim birimleri kullanılır.
 * Geçersiz trafik riskine karşı geliştirme cihazları MatchPickApplication içinde test cihazı olarak kayıtlıdır.
 */
object AdConfig {
    const val APP_ID: String = "ca-app-pub-4674211063760769~9806710318"
    const val BANNER_AD_UNIT_ID: String = "ca-app-pub-4674211063760769/3533393113"
    const val REWARDED_AD_UNIT_ID: String = "ca-app-pub-4674211063760769/7825533906"
    const val INTERSTITIAL_AD_UNIT_ID: String = "ca-app-pub-4674211063760769/1395856920"
}
