package org.davincicodeos.updater

import android.app.Application
import com.google.android.material.color.DynamicColors

class UpdaterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}