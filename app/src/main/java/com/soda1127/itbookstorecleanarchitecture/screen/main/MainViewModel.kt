package com.soda1127.itbookstorecleanarchitecture.screen.main

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.lifecycle.ViewModel
import com.soda1127.itbookstorecleanarchitecture.navigation.BottomNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    private val _forwardSlideDirection = MutableStateFlow(SlideDirection.Left)
    val forwardSlideDirection: StateFlow<SlideDirection> = _forwardSlideDirection.asStateFlow()

    val bottomNavItems = listOf(BottomNavItem.New, BottomNavItem.Search, BottomNavItem.Bookmark)

    fun onTabSelected(index: Int) {
        val currentIndex = _currentTabIndex.value
        if (currentIndex != index) {
            _forwardSlideDirection.value =
                    if (currentIndex < index) {
                        SlideDirection.Left
                    } else {
                        SlideDirection.Right
                    }
            _currentTabIndex.value = index
        }
    }
}
