package com.abedatasolutions.ereports.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

fun interface DispatcherProvider {
    operator fun invoke(reference: DispatcherReference): CoroutineDispatcher
}