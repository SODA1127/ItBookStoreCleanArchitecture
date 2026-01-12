package com.soda1127.itbookstorecleanarchitecture.screen.main

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import com.soda1127.itbookstorecleanarchitecture.testbase.JUnit5Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainViewModelTest: JUnit5Test() {

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    override fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun `initial state is correct`() = runTest {
        Assertions.assertEquals(0, viewModel.currentTabIndex.first())
        Assertions.assertEquals(SlideDirection.Left, viewModel.forwardSlideDirection.first())
    }

    @Test
    fun `onTabSelected updates index and direction correctly when moving forward`() = runTest {
        // Initial 0. Move to 1.
        // 0 -> 1 : Should be Left (Enter from Right? Wait, let's check logic)
        // Logic: if (currentIndex < index) SlideDirection.Left else SlideDirection.Right
        // SlideDirection.Left means "Towards Left", i.e., entering from Right to Left.

        viewModel.onTabSelected(1)

        Assertions.assertEquals(1, viewModel.currentTabIndex.first())
        Assertions.assertEquals(SlideDirection.Left, viewModel.forwardSlideDirection.first())
    }

    @Test
    fun `onTabSelected updates index and direction correctly when moving backward`() = runTest {
        // Move to 2 first
        viewModel.onTabSelected(2)

        // 2 -> 1 : Should be Right
        // Logic: if (2 < 1) False -> SlideDirection.Right
        // SlideDirection.Right means "Towards Right", i.e., entering from Left to Right.

        //viewModel.onTabSelected(1)

        Assertions.assertEquals(1, viewModel.currentTabIndex.first())
        Assertions.assertEquals(SlideDirection.Right, viewModel.forwardSlideDirection.first())
    }
}
