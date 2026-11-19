package com.matchpick.app.core.performance

import android.app.Activity
import android.os.Build
import android.view.FrameMetrics
import android.view.Window
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FrameRateMonitor @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) {
    fun startMonitoring(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.window.addOnFrameMetricsAvailableListener(
                Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
                    val frameDurationNs = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
                    val frameDurationMs = frameDurationNs / 1_000_000

                    // 120Hz hedefinde 1 frame = ~8.3ms, 60Hz hedefinde = ~16.6ms
                    // 34ms üzeri gecikmeler 2 frame kaybına (Jank) işaret eder
                    if (frameDurationMs > 34) {
                        crashlytics.log("JANK_DETECTED: Frame duration was ${frameDurationMs}ms on ${activity.localClassName}")
                    }
                },
                android.os.Handler(android.os.Looper.getMainLooper())
            )
        }
    }
}
