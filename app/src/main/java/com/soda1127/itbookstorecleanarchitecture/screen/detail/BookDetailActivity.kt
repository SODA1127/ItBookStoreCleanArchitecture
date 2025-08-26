package com.soda1127.itbookstorecleanarchitecture.screen.detail

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.soda1127.itbookstorecleanarchitecture.extensions.load
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookInfoEntity
import com.soda1127.itbookstorecleanarchitecture.databinding.ActivityBookDetailBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("NO_REFLECTION_IN_CLASS_PATH")
@AndroidEntryPoint
class BookDetailActivity : BaseActivity<BookDetailViewModel, ActivityBookDetailBinding>() {

    override val vm by viewModels<BookDetailViewModel>()

    override val binding by viewBinding(ActivityBookDetailBinding::inflate)

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vm.saveMemo(binding.bookMemoInput.text.toString())
            finish()
        }
    }

    override fun initViews() = with(binding) {
        onBackPressedDispatcher.addCallback(this@BookDetailActivity, onBackPressedCallback)

        toolbar.setNavigationOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        titleTextView.text = intent.getStringExtra(KEY_TITLE)

        likedButton.visibility = View.GONE

    }

    override fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.bookDetailStateFlow.collect { state ->
                    when (state) {
                        is BookDetailState.Loading -> handleLoading()
                        is BookDetailState.Success -> handleSuccess(state)
                        is BookDetailState.Error -> handleError(state)
                        is BookDetailState.SaveMemo -> onBackPressedCallback.handleOnBackPressed()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleLoading() = with(binding) {
        errorContainerGroup.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun handleSuccess(state: BookDetailState.Success) = with(binding) {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
        likedButton.visibility = View.VISIBLE

        likedButton.setOnClickListener {
            vm.toggleLikeButton()
        }

        likedButton.setImageDrawable(
            ContextCompat.getDrawable(
                this@BookDetailActivity, if (state.isLiked) {
                    R.drawable.ic_heart_enable
                } else {
                    R.drawable.ic_heart_disable
                }
            )
        )

        val bookInfoEntity = state.bookInfoEntity

        bookImageView.load(bookInfoEntity.image)

        bookMemoInput.setText(state.memo)

        if (state.summaryState.isSummaryGenerating) {
            bookSummaryTextView.text = "내용 요약 생성중..."
            summaryProgressBar.visibility = View.VISIBLE
        } else {
            bookSummaryTextView.text = state.summaryState.bookSummary ?: "요약문이 존재하지 않습니다."
            summaryProgressBar.visibility = View.GONE
        }

        if (state.summaryState.isRatingSummaryGenerating) {
            ratingSummaryTextView.text = "평점 요약 생성중..."
            ratingSummaryProgressBar.visibility = View.VISIBLE
        } else {
            ratingSummaryTextView.text = state.summaryState.ratingSummary ?: "평점 요약이 존재하지 않습니다."
            ratingSummaryProgressBar.visibility = View.GONE
        }

        ratingBar.numStars = 5
        ratingBar.rating = bookInfoEntity.rating

        var infoText = ""
        BookInfoEntity::class.members.forEach { property ->
            if (property is kotlin.reflect.KProperty<*>) {
                val value = property.call(bookInfoEntity) // call() 함수 사용
                infoText += "[${property.name}] : ${value}\n\n"
            }
        }


        bookInfoTextView.text = infoText
    }

    private fun handleError(state: BookDetailState.Error) = with(binding) {
        when (state) {
            is BookDetailState.Error.Default -> {
                scrollView.visibility = View.GONE
                progressBar.visibility = View.GONE
                errorContainerGroup.visibility = View.VISIBLE
                errorMessageTextView.text = getString(R.string.error_occurred, state.e.localizedMessage)
                retryButton.setOnClickListener {
                    vm.fetchData()
                }
            }
            is BookDetailState.Error.NotFound -> {
                Toast.makeText(this@BookDetailActivity, "책정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }



    companion object {

        const val KEY_ISBN13 = "KEY_ISBN13"
        const val KEY_TITLE = "KEY_TITLE"

        fun newIntent(context: Context, isbn13: String, title: String) =
            Intent(context, BookDetailActivity::class.java).apply {
                putExtra(KEY_ISBN13, isbn13)
                putExtra(KEY_TITLE, title)
            }

    }

}
