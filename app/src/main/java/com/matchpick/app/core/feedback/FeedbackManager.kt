package com.matchpick.app.core.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.matchpick.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
        )
        .build()

    // Ham ses kaynakları (res/raw altına eklenecek hafif 50ms wav dosyaları)
    private var tapSoundId: Int = 0
    private var successSoundId: Int = 0

    init {
        // İhtiyaç duyulduğunda raw dosyalar üzerinden dinamik yüklenir
        // tapSoundId = soundPool.load(context, R.raw.snd_tap, 1)
        // successSoundId = soundPool.load(context, R.raw.snd_win, 1)
    }

    fun playSelectionFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
        if (tapSoundId != 0) soundPool.play(tapSoundId, 0.4f, 0.4f, 0, 0, 1.0f)
    }

    fun playWinFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
        if (successSoundId != 0) soundPool.play(successSoundId, 0.7f, 0.7f, 0, 0, 1.0f)
    }
}
