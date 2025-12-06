package com.soda1127.itbookstorecleanarchitecture.screen.main

import androidx.lifecycle.ViewModel
import com.soda1127.itbookstorecleanarchitecture.navigation.BottomNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val bottomNavItems = listOf(BottomNavItem.New, BottomNavItem.Search, BottomNavItem.Bookmark)
}
