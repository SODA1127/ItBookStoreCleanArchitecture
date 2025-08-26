package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.soda1127.itbookstorecleanarchitecture.extensions.hideSoftInput
import com.soda1127.itbookstorecleanarchitecture.model.Model
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseFragment
import com.soda1127.itbookstorecleanarchitecture.screen.base.ScrollableScreen
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.ModelRecyclerAdapter
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BooksListener
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.search.SearchHistoryListener
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.databinding.FragmentSearchTabBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchTabFragment: BaseFragment<SearchTabViewModel, FragmentSearchTabBinding>(), ScrollableScreen {

    override val binding by viewBinding(FragmentSearchTabBinding::inflate)

    override val vm by viewModels<SearchTabViewModel>()

    private val searchResultAdapter by lazy {
        ModelRecyclerAdapter<Model, BooksListener>(object : BooksListener {

            override fun onClickBookItem(model: BookModel) {
                startActivity(
                    BookDetailActivity.newIntent(requireContext(), model.isbn13, model.title)
                )
            }

            override fun onClickLoadRetry() {
                vm.loadMoreSearchResult(isLoadRetry = true)
            }

        })
    }

    private val searchHistoryAdapter by lazy {
        ModelRecyclerAdapter<Model, SearchHistoryListener>(object : SearchHistoryListener {

            override fun onClickSearchHistoryItem(keyword: String) {
                vm.searchByHistory(keyword)
                binding.root.hideSoftInput()
            }

            override fun onClickRemoveItem(keyword: String) {
                vm.removeHistory(keyword)
            }

        })
    }

    override fun initViews(): Unit = with(binding) {
        searchResultRecyclerView.adapter = searchResultAdapter
        searchHistoryRecyclerView.adapter = searchHistoryAdapter
        searchButton.setOnClickListener {
            vm.searchByKeyword(searchInput.text.toString(), withAIGeneration = true)
            binding.root.hideSoftInput()
        }
        searchResultRecyclerView.setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int -> nextPageRender() }
        searchInput.addTextChangedListener {
            if (it.toString().isEmpty()) {
                vm.fetchData()
            }
        }
    }

    private fun nextPageRender() {
        val lastVisibleItemPosition =
            (binding.searchResultRecyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
        val itemTotalCount = binding.searchResultRecyclerView.adapter!!.itemCount - 1
        if (itemTotalCount <= lastVisibleItemPosition) {
            vm.loadMoreSearchResult()
        }
    }

    override fun observeData() {
        lifecycleScope.launchWhenStarted {
            vm.searchTabStateFlow.collect { state ->
                when (state) {
                    is SearchTabState.Loading -> handleLoading(state)
                    is SearchTabState.Success.SearchResult -> handleSearchResult(state)
                    is SearchTabState.Success.SearchHistory -> handleSearchHistory(state)
                    is SearchTabState.Error -> handleError(state)
                    else -> Unit
                }
            }
        }
    }

    private fun handleLoading(state: SearchTabState.Loading) = with(binding) {
        errorContainerGroup.visibility = View.GONE
        emptyTextView.visibility = View.GONE
        searchResultRecyclerView.visibility = View.GONE
        searchHistoryRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        aiGenerationProgressBar.isVisible = state.withAIGeneration

        if (state.generatedKeyword != null) {
            searchInput.setText(state.generatedKeyword)
            searchInput.setSelection(searchInput.text.toString().length)
        }
    }

    private fun handleSearchResult(state: SearchTabState.Success.SearchResult) = with(binding) {
        searchInput.setText(state.searchKeyword ?: "")
        searchInput.setSelection(searchInput.text.toString().length)
        searchResultRecyclerView.visibility = View.VISIBLE
        searchHistoryRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

        val modelList = state.modelList
        if (modelList.isNotEmpty()) {
            emptyTextView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.VISIBLE
            emptyTextView.setText(R.string.empty_search_result)
        }

        aiGenerationProgressBar.isGone = true
        searchResultAdapter.submitList(modelList)
    }

    private fun handleSearchHistory(state: SearchTabState.Success.SearchHistory) = with(binding) {
        searchResultRecyclerView.visibility = View.GONE
        searchHistoryRecyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        val modelList = state.modelList
        if (modelList.isNotEmpty()) {
            emptyTextView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.VISIBLE
            emptyTextView.setText(R.string.empty_search_history)
        }
        searchHistoryAdapter.submitList(state.modelList)

        lifecycleScope.launch {
            delay(200)
            searchHistoryRecyclerView.scrollToPosition(0)
        }
    }

    private fun handleError(state: SearchTabState.Error) = with(binding) {
        progressBar.visibility = View.GONE
        errorContainerGroup.visibility = View.VISIBLE
        errorMessageTextView.text = getString(R.string.error_occurred, state.e.localizedMessage)
        retryButton.setOnClickListener {
            state.searchKeyword?.let { searchKeyword ->
                vm.searchByKeyword(searchKeyword)
            } ?: vm.fetchData()
        }
    }

    override fun scrollUp() {
        binding.searchResultRecyclerView.smoothScrollToPosition(0)
    }

    companion object {

        fun newInstance(bundle: Bundle = bundleOf()) = SearchTabFragment().apply {
            arguments = bundle
        }

        val TAG : String = SearchTabFragment::class.simpleName!!

    }

}
