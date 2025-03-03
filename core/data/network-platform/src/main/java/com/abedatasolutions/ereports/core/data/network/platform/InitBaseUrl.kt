package com.abedatasolutions.ereports.core.data.network.platform

import android.app.Application
import com.abedatasolutions.ereports.core.data.network.BaseUrl

fun Application.initBaseUrl(){
    BaseUrl(BuildConfig.BASE_URL)
}