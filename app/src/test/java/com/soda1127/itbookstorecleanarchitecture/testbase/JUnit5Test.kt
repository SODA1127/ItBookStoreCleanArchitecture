package com.soda1127.itbookstorecleanarchitecture.testbase

import androidx.annotation.CallSuper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class)
abstract class JUnit5Test {

    @CallSuper
    @BeforeEach
    open fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(Dispatchers::class)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { Dispatchers.IO } returns UnconfinedTestDispatcher()
    }

    @CallSuper
    @AfterEach
    open fun done() {
        Dispatchers.resetMain()
    }

    protected fun setStandardTestDispatchers() {
        every { Dispatchers.IO } returns StandardTestDispatcher()
        Dispatchers.setMain(StandardTestDispatcher())
    }
}
