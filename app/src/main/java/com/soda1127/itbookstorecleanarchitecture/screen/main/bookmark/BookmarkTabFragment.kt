package com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.soda1127.itbookstorecleanarchitecture.model.Model
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.ModelRecyclerAdapter
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BookmarkListener
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseFragment
import com.soda1127.itbookstorecleanarchitecture.screen.base.ScrollableScreen
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity
import com.soda1127.itbookstorecleanarchitecture.screen.main.MainNavigation
import com.soda1127.itbookstorecleanarchitecture.screen.main.MainViewModel
import com.soda1127.itbookstorecleanarchitecture.databinding.FragmentBookmarkTabBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkTabFragment: BaseFragment<BookmarkTabViewModel, FragmentBookmarkTabBinding>(), ScrollableScreen {

    override val binding by viewBinding(FragmentBookmarkTabBinding::inflate)

    override val vm by viewModels<BookmarkTabViewModel>()

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val adapter by lazy {
        ModelRecyclerAdapter<Model, BookmarkListener>(object : BookmarkListener {

            override fun onClickBookItem(model: BookModel) {
                startActivity(
                    BookDetailActivity.newIntent(requireContext(), model.isbn13, model.title)
                )
            }

            override fun onClickLikedButton(model: BookModel) {
                vm.toggleLikeButton(model)
            }

            override fun onClickLoadRetry() = Unit

        })
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        emptyButton.setOnClickListener {
            mainViewModel.changeNavigation(MainNavigation(R.id.menu_new))
        }
    }

    override fun observeData() {
        lifecycleScope.launchWhenStarted {
            vm.bookmarkStateFlow.collect { state ->
                when (state) {
                    is BookmarkState.Loading -> handleLoading()
                    is BookmarkState.Success -> handleSuccess(state)
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchData()
    }

    private fun handleLoading() = with(binding) {
        progressBar.visibility = View.VISIBLE
    }

    private fun handleSuccess(state: BookmarkState.Success) = with(binding) {
        progressBar.visibility = View.GONE
        val modelList = state.modelList
        if (modelList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyButton.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyButton.visibility = View.GONE
            adapter.submitList(modelList)
        }
    }

    override fun scrollUp() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    companion object {

        fun newInstance(bundle: Bundle = bundleOf()) = BookmarkTabFragment().apply {
            arguments = bundle
        }

        val TAG : String = BookmarkTabFragment::class.simpleName!!

    }

}
