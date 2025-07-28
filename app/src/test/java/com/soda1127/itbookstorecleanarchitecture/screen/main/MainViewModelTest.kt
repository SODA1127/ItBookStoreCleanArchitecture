package com.soda1127.itbookstorecleanarchitecture.screen.main

import com.soda1127.itbookstorecleanarchitecture.screen.main.MainNavigation
import com.soda1127.itbookstorecleanarchitecture.screen.main.MainViewModel
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import com.soda1127.itbookstorecleanarchitecture.R
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class MainViewModelTest: JUnit5Test() {

    private lateinit var sut: MainViewModel

    @BeforeEach
    override fun setup() {
        super.setup()
        sut = MainViewModel()
    }

    @Test
    fun `Test main tab navigation changed`() = runTest(UnconfinedTestDispatcher()) {
        val first = MainNavigation(R.id.menu_new)
        val second = MainNavigation(R.id.menu_bookmark)
        sut.navigationItemStateFlow.test(this) {
            assertValues(
                null,
                first,
                second
            )
        }
        sut.changeNavigation(first)
        sut.changeNavigation(second)
    }

}
