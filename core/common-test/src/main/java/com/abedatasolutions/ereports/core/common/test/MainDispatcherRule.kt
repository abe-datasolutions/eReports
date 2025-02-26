package com.abedatasolutions.ereports.core.common.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit [TestRule] that sets the Main dispatcher to [testDispatcher]
 * for the duration of the test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = TestDispatcherProvider.dispatcher,
) : TestWatcher() {
    override fun starting(description: Description) {
        TestDispatcherProvider.dispatcher = testDispatcher
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description){
        Dispatchers.resetMain()
        testDispatcher.cancelChildren()
    }
}