package com.soda1127.itbookstorecleanarchitecture.tabs.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.soda1127.itbookstorecleanarchitecture.databinding.FragmentSearchTabBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchTabFragment: Fragment() {

    private val binding: FragmentSearchTabBinding by viewBinding(FragmentSearchTabBinding::inflate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    private val viewModel by viewModels<SearchTabViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        viewModel.searchBooksByKeyword("android")
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.stateFlow.collect { state ->
                Log.e("Search Tab State", state.toString())
                when(state) {
                    is SearchTabState.Loading -> {
                        binding.loadingView.isVisible = true
                        binding.textView.isVisible = false
                    }
                    is SearchTabState.Result -> {
                        binding.loadingView.isVisible = false
                        binding.textView.isVisible = true
                        binding.textView.text = state.bookEntities.toString()
                    }
                    else -> Unit
                }
            }
        }
    }

    companion object {

        const val TAG = "SearchTabFragment"

        fun newInstance(bundle: Bundle = bundleOf()) = SearchTabFragment().apply {
            arguments = bundle
        }

    }

}
