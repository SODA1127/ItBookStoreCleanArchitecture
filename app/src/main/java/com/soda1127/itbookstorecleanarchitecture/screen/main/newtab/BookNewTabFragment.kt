package com.soda1127.itbookstorecleanarchitecture.screen.main.newtab

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.soda1127.itbookstorecleanarchitecture.model.Model
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseFragment
import com.soda1127.itbookstorecleanarchitecture.screen.base.ScrollableScreen
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.ModelRecyclerAdapter
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BooksListener
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.databinding.FragmentNewTabBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookNewTabFragment : BaseFragment<BookNewTabViewModel, FragmentNewTabBinding>(), ScrollableScreen {

    override val binding by viewBinding(FragmentNewTabBinding::inflate)

    override val vm by viewModels<BookNewTabViewModel>()

    private val adapter by lazy {
        ModelRecyclerAdapter<Model, BooksListener>(object : BooksListener {

            override fun onClickBookItem(model: BookModel) {
                startActivity(
                    BookDetailActivity.newIntent(requireContext(), model.isbn13, model.title)
                )
            }

            override fun onClickLoadRetry() = Unit

        })
    }

    override fun initViews(): Unit = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData() {
        lifecycleScope.launchWhenStarted {
            vm.newTabStateFlow.collect { state ->
                when (state) {
                    is NewTabState.Loading -> handleLoading()
                    is NewTabState.Success -> handleSuccess(state)
                    is NewTabState.Error -> handleError(state)
                    else -> Unit
                }
            }
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.visibility = View.VISIBLE
        errorContainerGroup.visibility = View.GONE
    }

    private fun handleSuccess(state: NewTabState.Success) = with(binding) {
        progressBar.visibility = View.GONE
        adapter.submitList(state.modelList)
    }

    private fun handleError(state: NewTabState.Error) = with(binding) {
        progressBar.visibility = View.GONE
        errorContainerGroup.visibility = View.VISIBLE
        errorMessageTextView.text = getString(R.string.error_occurred, state.e.localizedMessage)
        retryButton.setOnClickListener {
            vm.fetchData()
        }
    }

    override fun scrollUp() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    companion object {

        fun newInstance(bundle: Bundle = bundleOf()) = BookNewTabFragment().apply {
            arguments = bundle
        }

        val TAG: String = BookNewTabFragment::class.simpleName!!

    }

}
