package com.soda1127.itbookstorecleanarchitecture

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.soda1127.itbookstorecleanarchitecture.databinding.ActivityMainBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import com.soda1127.itbookstorecleanarchitecture.tabs.bookmark.BookmarkTabFragment
import com.soda1127.itbookstorecleanarchitecture.tabs.search.SearchTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews(binding)
    }

    private fun initViews(binding: ActivityMainBinding) {
        initBottomNavigationListener(binding)

        showFragment(SearchTabFragment.TAG)
    }

    private fun initBottomNavigationListener(binding: ActivityMainBinding) {
        binding.bottomNav.setOnItemSelectedListener(OnItemSelectedListener { item ->
            return@OnItemSelectedListener when(item.itemId) {
                R.id.menu_search -> {
                    showFragment(SearchTabFragment.TAG)
                    true
                }
                R.id.menu_bookmark -> {
                    showFragment(BookmarkTabFragment.TAG)
                    true
                }
                else -> false
            }
        })
    }

    private fun showFragment(tag: String) {
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

    private fun getFragmentByTag(tag: String): Fragment? {
        return when(tag) {
            SearchTabFragment.TAG -> SearchTabFragment.newInstance()
            BookmarkTabFragment.TAG -> BookmarkTabFragment.newInstance()
            else -> null
        }
    }

}
