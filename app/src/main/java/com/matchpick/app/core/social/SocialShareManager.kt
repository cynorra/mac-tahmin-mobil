package com.matchpick.app.core.social

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class SocialShareManager(private val context: Context) {

    fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val imagesFolder = File(context.cacheDir, "shared_cards")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "match_prediction_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun shareToInstagramStory(bitmap: Bitmap) {
        val uri = saveBitmapToCache(bitmap)
        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(uri, "image/png")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            shareGeneric(uri, "Instagram Story")
        }
    }

    fun shareToWhatsApp(bitmap: Bitmap, text: String) {
        val uri = saveBitmapToCache(bitmap)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            shareGeneric(uri, text)
        }
    }

    fun shareGeneric(uri: Uri, shareText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "MatchPick Share"))
    }
}
