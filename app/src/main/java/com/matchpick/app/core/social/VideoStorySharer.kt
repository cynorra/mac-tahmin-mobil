package com.matchpick.app.core.social

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class VideoStorySharer(private val context: Context) {

    fun shareToTikTok(mediaUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, mediaUri)
            setPackage("com.zhiliaoapp.musically") // Global TikTok paketi
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // TikTok yüklü değilse standart paylaşım paneline fallback
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, mediaUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share your prediction"))
        }
    }

    fun shareToFacebookStories(mediaUri: Uri, appId: String) {
        val intent = Intent("com.facebook.stories.ADD_TO_STORY").apply {
            type = "image/png"
            putExtra("com.facebook.platform.extra.APPLICATION_ID", appId)
            putExtra(Intent.EXTRA_STREAM, mediaUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
