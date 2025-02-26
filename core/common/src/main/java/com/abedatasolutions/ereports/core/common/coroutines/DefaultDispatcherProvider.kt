package com.abedatasolutions.ereports.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal object DefaultDispatcherProvider: DispatcherProvider {
    override fun invoke(reference: DispatcherReference): CoroutineDispatcher = when(reference){
        DispatcherReference.Default -> Dispatchers.Default
        DispatcherReference.IO -> Dispatchers.IO
        DispatcherReference.Main -> Dispatchers.Main
    }
}