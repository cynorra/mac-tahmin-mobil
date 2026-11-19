package com.matchpick.app.core.ads

/**
 * MatchPick Pro - Central AdMob & Monetization Configuration
 *
 * Gerçek AdMob yayıncı ve reklam birim kimliklerinizi (Publisher & Ad Unit IDs)
 * doğrudan bu dosya üzerinden veya Gradle BuildConfig üzerinden tek noktadan güncelleyebilirsiniz.
 *
 * Varsayılan ID'ler, Google'ın resmi AdMob Test Kimlikleridir (Hesap güvenliği ve ceza almamak için).
 * Gerçek reklam yayınına geçerken kullanıcıdan alınan gerçek ID'ler buraya girilir.
 */
object AdConfig {
    /**
     * 1. AdMob Uygulama Kimliği (App ID)
     * AndroidManifest.xml içerisindeki com.google.android.gms.ads.APPLICATION_ID meta-datasında kullanılır.
     * Format: ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX
     */
    const val APP_ID: String = "ca-app-pub-3940256099942544~3347511713"

    /**
     * 2. Banner Reklam Birim Kimliği (Adaptive Banner)
     * Topluluk akışı içerisinde analist kartlarının arasında gösterilir.
     * Format: ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
     */
    const val BANNER_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/6300978111"

    /**
     * 3. Ödüllü Reklam Birim Kimliği (Rewarded Video Ad)
     * VIP Pro Bankolar ekranında yapay zeka maç analizlerinin kilidini açmak için izletilir.
     * Format: ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
     */
    const val REWARDED_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/5224354917"

    /**
     * 4. Geçiş (Interstitial) Reklam Birim Kimliği
     * Sekme ve maç detayları arasında 4 dakikalık akıllı frekans kısıtlamasıyla gösterilir.
     * Format: ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
     */
    const val INTERSTITIAL_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/1033173712"
}
