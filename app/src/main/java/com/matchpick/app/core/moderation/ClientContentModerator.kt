package com.matchpick.app.core.moderation

import java.util.regex.Pattern

object ClientContentModerator {

    // Temel spam regex kontrolleri (URL yönlendirmeleri, gizlenmiş telefon numaraları)
    private val URL_PATTERN = Pattern.compile(
        """(https?://|www\.)[^\s/$.?#].[^\s]*""",
        Pattern.CASE_INSENSITIVE
    )
    private val PHONE_PATTERN = Pattern.compile(
        """(\+?\d{1,3}[-.\s]?)?(\(?\d{3}\)?[-.\s]?)?\d{3}[-.\s]?\d{4}"""
    )

    // EN ve TR temel küfür/argo kökleri (Örnekleme; prodüksiyonda genişletilmiş Set kullanılır)
    private val BANNED_ROOTS = hashSetOf(
        "spamlink", "freecash", "bettip", "scam", "fuck", "shit", "bitch",
        "sik", "amk", "aq", "orospu", "pic", "yarrak", "got"
    )

    fun validatePost(content: String): ModerationResult {
        val trimmed = content.trim()
        if (trimmed.length < 3) {
            return ModerationResult.Invalid("Metin çok kısa.")
        }
        if (trimmed.length > 280) {
            return ModerationResult.Invalid("Maksimum 280 karakter sınırını aştınız.")
        }

        // Spam link ve numara kontrolü
        if (URL_PATTERN.matcher(trimmed).find() || PHONE_PATTERN.matcher(trimmed).find()) {
            return ModerationResult.Invalid("Gönderiniz reklam, bağlantı veya iletişim bilgisi içeremez.")
        }

        // Normalizasyon: Leet speak (örn: f*ck, s!k) temizleme
        val normalized = trimmed.lowercase()
            .replace("@", "a")
            .replace("1", "i")
            .replace("0", "o")
            .replace(Regex("[^a-zA-ZçğıöşüÇĞİÖŞÜ\\s]"), "")

        val words = normalized.split("\\s+".toRegex())
        for (word in words) {
            if (BANNED_ROOTS.any { banned -> word.contains(banned) }) {
                return ModerationResult.Invalid("İçerik topluluk kurallarına aykırı ifadeler barındırıyor.")
            }
        }

        return ModerationResult.Valid
    }
}

sealed interface ModerationResult {
    data object Valid : ModerationResult
    data class Invalid(val reason: String) : ModerationResult
}
