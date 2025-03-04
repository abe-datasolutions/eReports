package com.abedatasolutions.ereports.core.common.platform

import android.util.Log
import com.abedatasolutions.ereports.core.common.logging.Logger

object AndroidLogger: Logger {
    override fun recordException(t: Throwable) {
        t.printStackTrace()
    }

    override fun log(message: String) {
        Log.i("LogEvent", message)
    }

    override fun setCustomKey(key: String, value: String) {
        Log.d(key, value)
    }
}