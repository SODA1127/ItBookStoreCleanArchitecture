package com.soda1127.itbookstorecleanarchitecture.screen.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.ai.GeminiService
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookMemoEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookMemoRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailActivity.Companion.KEY_ISBN13
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository,
    private val bookMemoRepository: BookMemoRepository,
    private val geminiService: GeminiService,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _bookDetailStateFlow = MutableStateFlow<BookDetailState>(BookDetailState.Uninitialized)
    val bookDetailStateFlow: StateFlow<BookDetailState> = _bookDetailStateFlow

    private val isbn13 by lazy { savedStateHandle.get<String>(KEY_ISBN13) }

    override fun fetchData(): Job = viewModelScope.launch {
        setState(BookDetailState.Loading)
        try {
            val initialIsbn = isbn13 ?: run {
                setState(BookDetailState.Error.NotFound)
                return@launch
            }

            val initialBookInfo = bookStoreRepository.getBookInfo(initialIsbn).first()
            if (initialBookInfo == null) {
                setState(BookDetailState.Error.NotFound)
                return@launch
            }

            val successState = BookDetailState.Success(
                bookInfoEntity = initialBookInfo,
                isLiked = bookStoreRepository.getBookInWishList(initialIsbn).first() != null,
                memo = bookMemoRepository.getBookMemo(initialIsbn).first()?.memo ?: "",
                summaryState = BookDetailState.Success.SummaryState(isSummaryGenerating = true, isRatingSummaryGenerating = true)
            )
            setState(successState)

            launch {
                val summaryText = geminiService.generateBookSummary(initialBookInfo.title, initialBookInfo.url)
                (bookDetailStateFlow.value as? BookDetailState.Success)?.let { currentState ->
                    val summaryState = currentState.summaryState
                    setState(
                        currentState.copy(
                            summaryState = summaryState.copy(
                                bookSummary = summaryState.bookSummary.orEmpty().ifEmpty { "[내용요약]\n" } + summaryText.orEmpty(),
                                isSummaryGenerating = false
                            )
                        )
                    )
                }
            }

            launch {
                val ratingSummaryText = geminiService.generateRatingSummaryStr(initialBookInfo)
                (bookDetailStateFlow.value as? BookDetailState.Success)?.let { currentState ->
                    val summaryState = currentState.summaryState
                    setState(
                        currentState.copy(
                            summaryState = summaryState.copy(
                                ratingSummary = summaryState.ratingSummary.orEmpty().ifEmpty { "[평점요약]\n" } + ratingSummaryText.orEmpty(),
                                isRatingSummaryGenerating = false
                            )
                        )
                    )
                }
            }

            // '찜하기' 상태 관찰
            bookStoreRepository.getBookInWishList(initialIsbn).collect { bookInWishList ->
                (bookDetailStateFlow.value as? BookDetailState.Success)?.let { currentSuccessState ->
                    setState(currentSuccessState.copy(isLiked = bookInWishList != null))
                }
            }

            // '메모' 상태 관찰
            bookMemoRepository.getBookMemo(initialIsbn).collect { bookMemo ->
                (bookDetailStateFlow.value as? BookDetailState.Success)?.let { currentSuccessState ->
                    setState(currentSuccessState.copy(memo = bookMemo?.memo ?: ""))
                }
            }

        } catch (e: Exception) {
            // ✅ [핵심 수정 사항]
            // 잡힌 예외가 실제 오류인지, 아니면 SDK에 의해 포장된 취소인지 확인합니다.
            if (e.isCancellation()) {
                // 근본 원인이 CancellationException이므로 정상적인 취소로 간주합니다.
                Log.d("BookDetailViewModel", "Gemini generation was cancelled (wrapped exception detected).")
                // 여기서 아무것도 하지 않거나, CancellationException을 다시 던져
                // 상위 코루틴이 취소 흐름을 이어가도록 합니다.
                throw CancellationException("Operation was cancelled", e)
            } else {
                // 이것이 진짜 예기치 못한 오류입니다 (네트워크, API 키 문제 등).
                Log.e("BookDetailViewModel", "An unexpected error occurred during summary generation.", e)
                (bookDetailStateFlow.value as? BookDetailState.Success)?.let { currentState ->
                    setState(
                        currentState.copy(
                            summaryState = BookDetailState.Success.SummaryState(
                                bookSummary = "[요약 실패]",
                                isSummaryGenerating = false,
                                ratingSummary = "[평가 요약 실패]"
                            )
                        )
                    )
                }
            }
        }
    }

    fun toggleLikeButton() = viewModelScope.launch {
        try {
            when (val data = bookDetailStateFlow.value) {
                is BookDetailState.Success -> {
                    if (data.isLiked) {
                        bookStoreRepository.removeBookInWishList(data.bookInfoEntity.isbn13)
                    } else {
                        bookStoreRepository.addBookInWishList(data.bookInfoEntity.toBookEntity())
                    }
                    setState(
                        data.copy(
                            isLiked = data.isLiked.not()
                        )
                    )
                }

                else -> Unit
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setState(
                BookDetailState.Error.Default(e)
            )
        }
    }

    fun saveMemo(memo: String) = viewModelScope.launch {
        isbn13?.let { isbn13 ->
            bookMemoRepository.saveBookMemo(
                BookMemoEntity(
                    isbn13,
                    memo
                )
            )
            setState(
                BookDetailState.SaveMemo
            )
        } ?: kotlin.run {
            setState(
                BookDetailState.SaveMemo
            )
        }
    }

    private fun setState(state: BookDetailState) {
        _bookDetailStateFlow.value = state
    }
}

// ViewModel 파일 상단이나 별도의 유틸리티 파일에 추가합니다.
/**
 * 예외의 근본 원인(root cause)이 CancellationException인지 재귀적으로 확인합니다.
 * Firebase AI SDK처럼 CancellationException을 다른 예외로 감싸는 경우에 유용합니다.
 */
fun Throwable.isCancellation(): Boolean {
    var cause: Throwable? = this
    while (cause != null) {
        if (cause is CancellationException) {
            return true
        }
        cause = cause.cause
    }
    return false
}
