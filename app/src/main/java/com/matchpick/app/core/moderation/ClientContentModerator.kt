package com.matchpick.app.core.moderation

import androidx.annotation.StringRes
import com.matchpick.app.R
import java.util.regex.Pattern

object ClientContentModerator {

    private val URL_PATTERN = Pattern.compile(
        """(https?://|www\.)[^\s/$.?#].[^\s]*""",
        Pattern.CASE_INSENSITIVE
    )
    private val PHONE_PATTERN = Pattern.compile(
        """(\+?\d{1,3}[-.\s]?)?(\(?\d{3}\)?[-.\s]?)?\d{3}[-.\s]?\d{4}"""
    )

    private val BANNED_ROOTS = hashSetOf(
        "spamlink", "freecash", "bettip", "scam", "fuck", "shit", "bitch",
        "sik", "amk", "aq", "orospu", "pic", "yarrak", "got"
    )

    fun validatePost(content: String): ModerationResult {
        val trimmed = content.trim()
        if (trimmed.length < 3) {
            return ModerationResult.Invalid(R.string.mod_err_short)
        }
        if (trimmed.length > 280) {
            return ModerationResult.Invalid(R.string.mod_err_long)
        }

        if (URL_PATTERN.matcher(trimmed).find() || PHONE_PATTERN.matcher(trimmed).find()) {
            return ModerationResult.Invalid(R.string.mod_err_spam)
        }

        val normalized = trimmed.lowercase()
            .replace("@", "a")
            .replace("1", "i")
            .replace("0", "o")
            .replace(Regex("[^a-zA-ZçğıöşüÇĞİÖŞÜ\\s]"), "")

        val words = normalized.split("\\s+".toRegex())
        for (word in words) {
            if (BANNED_ROOTS.any { banned -> word.contains(banned) }) {
                return ModerationResult.Invalid(R.string.mod_err_profanity)
            }
        }

        return ModerationResult.Valid
    }
}

sealed interface ModerationResult {
    data object Valid : ModerationResult
    data class Invalid(@StringRes val reasonResId: Int) : ModerationResult
}
