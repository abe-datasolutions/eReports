package com.abedatasolutions.ereports.core.common.platform

import android.app.Application
import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.common.logging.Logger

fun Application.initCommon(){
    DebugMode(BuildConfig.DEBUG)
    if (BuildConfig.DEBUG) Logger.setLogger(AndroidLogger)
}