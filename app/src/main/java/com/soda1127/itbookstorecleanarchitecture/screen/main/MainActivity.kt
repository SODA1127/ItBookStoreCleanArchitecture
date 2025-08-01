package com.soda1127.itbookstorecleanarchitecture.screen.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.soda1127.itbookstorecleanarchitecture.extensions.hideSoftInput
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.databinding.ActivityMainBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseActivity
import com.soda1127.itbookstorecleanarchitecture.screen.base.ScrollableScreen
import com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark.BookmarkTabFragment
import com.soda1127.itbookstorecleanarchitecture.screen.main.search.SearchTabFragment
import com.soda1127.itbookstorecleanarchitecture.screen.main.newtab.BookNewTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val binding by viewBinding(ActivityMainBinding::inflate)

    override val vm by viewModels<MainViewModel>()

    override fun initState() {
        super.initState()
        vm.changeNavigation(MainNavigation(R.id.menu_new))
    }

    override fun initViews() = with(binding) {
        bottomNav.setOnItemSelectedListener { item ->
            val navigationId = item.itemId
            if (binding.bottomNav.selectedItemId == navigationId) {
                scrollTop(navigationId)
            }
            when (navigationId) {
                R.id.menu_new -> {
                    showFragment(BookNewTabFragment.TAG)
                    true
                }
                R.id.menu_bookmark -> {
                    showFragment(BookmarkTabFragment.TAG)
                    true
                }
                R.id.menu_history -> {
                    showFragment(SearchTabFragment.TAG)
                    true
                }
                else -> false
            }
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            vm.navigationItemStateFlow.collect { navigation ->
                navigation ?: return@collect
                binding.bottomNav.selectedItemId = navigation.navigationMenuId
            }
        }
    }

    private fun scrollTop(navigationId: Int) =
        when(navigationId) {
            R.id.menu_new -> scrollFragmentTop(BookNewTabFragment.TAG)
            R.id.menu_bookmark -> scrollFragmentTop(BookmarkTabFragment.TAG)
            R.id.menu_history -> scrollFragmentTop(SearchTabFragment.TAG)
            else -> Unit
        }

    private fun scrollFragmentTop(tag: String) {
        binding.root.hideSoftInput()
        val foundFragment = supportFragmentManager.findFragmentByTag(tag)
        if (foundFragment is ScrollableScreen) {
            foundFragment.scrollUp()
        }
    }

    private fun showFragment(tag: String) {
        binding.root.hideSoftInput()
        val foundFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }
        foundFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            val fragment = getFragmentByTag(tag)
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment, tag)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun getFragmentByTag(tag: String): Fragment? =
        when (tag) {
            BookNewTabFragment.TAG -> BookNewTabFragment.newInstance()
            BookmarkTabFragment.TAG -> BookmarkTabFragment.newInstance()
            SearchTabFragment.TAG -> SearchTabFragment.newInstance()
            else -> null
        }

}
