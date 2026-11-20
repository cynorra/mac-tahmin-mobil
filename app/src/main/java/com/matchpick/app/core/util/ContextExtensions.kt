package com.matchpick.app.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Safely traverses context wrappers to retrieve the underlying Activity instance.
 * Useful when Jetpack Compose or localization wraps the Context in a ContextWrapper.
 */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
