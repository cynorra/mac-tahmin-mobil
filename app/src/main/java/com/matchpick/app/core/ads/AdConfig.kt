package com.matchpick.app.core.ads

import com.matchpick.app.BuildConfig

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
 * Google AdMob Güvenlik Politikası:
 * Geliştirme (Debug) aşamasında ve emülatörde geçersiz trafik/tıklama cezası almamak için
 * resmi Google test reklam birimleri kullanılır; Yayın (Release) derlemesinde veya
 * canlıya çıkıldığında doğrudan gerçek AdMob birimleri üzerinden gelir elde edilir.
 */
object AdConfig {
    /**
     * 1. AdMob Uygulama Kimliği (App ID)
     * AndroidManifest.xml içerisindeki com.google.android.gms.ads.APPLICATION_ID meta-datasında kullanılır.
     */
    const val APP_ID: String = "ca-app-pub-4674211063760769~9806710318"

    /**
     * 2. Banner Reklam Birim Kimliği (Adaptive Banner)
     * Topluluk akışı ve VIP kartlar arasında gösterilir.
     */
    val BANNER_AD_UNIT_ID: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/6300978111" // Test ID
        } else {
            "ca-app-pub-4674211063760769/3533393113" // Canlı Gerçek ID
        }

    /**
     * 3. Ödüllü Reklam Birim Kimliği (Rewarded Video Ad)
     * VIP Pro Bankolar ekranında yapay zeka maç analizlerinin kilidini açmak için izletilir.
     */
    val REWARDED_AD_UNIT_ID: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5224354917" // Test ID
        } else {
            "ca-app-pub-4674211063760769/7825533906" // Canlı Gerçek ID
        }

    /**
     * 4. Geçiş (Interstitial) Reklam Birim Kimliği
     * Sekme ve maç detayları arasında 4 dakikalık akıllı frekans kısıtlamasıyla gösterilir.
     */
    val INTERSTITIAL_AD_UNIT_ID: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712" // Test ID
        } else {
            "ca-app-pub-4674211063760769/1395856920" // Canlı Gerçek ID
        }

    // Doğrudan Canlı ID Referansları
    const val PROD_APP_ID = "ca-app-pub-4674211063760769~9806710318"
    const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-4674211063760769/3533393113"
    const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-4674211063760769/7825533906"
    const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4674211063760769/1395856920"
}
