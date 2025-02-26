package com.abedatasolutions.ereports.core.common.test

import com.abedatasolutions.ereports.core.common.coroutines.DispatcherProvider
import com.abedatasolutions.ereports.core.common.coroutines.DispatcherReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

object TestDispatcherProvider: DispatcherProvider {
    var dispatcher: TestDispatcher = StandardTestDispatcher()
    override fun invoke(reference: DispatcherReference): CoroutineDispatcher = dispatcher
}