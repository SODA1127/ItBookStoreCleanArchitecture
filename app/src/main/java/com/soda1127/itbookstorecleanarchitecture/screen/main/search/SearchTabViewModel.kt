package com.soda1127.itbookstorecleanarchitecture.screen.main.search

import androidx.lifecycle.viewModelScope
import com.soda1127.itbookstorecleanarchitecture.data.entity.BookEntity
import com.soda1127.itbookstorecleanarchitecture.data.entity.SearchHistoryEntity
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookSearchRepository
import com.soda1127.itbookstorecleanarchitecture.data.repository.BookStoreRepository
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadRetryModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadingModel
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.model.search.SearchHistoryModel
import com.soda1127.itbookstorecleanarchitecture.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchTabViewModel @Inject constructor(
    private val bookStoreRepository: BookStoreRepository,
    private val searchRepository: BookSearchRepository
) : BaseViewModel<SearchTabState>() {

    override fun getInitialState(): SearchTabState = SearchTabState.Uninitialized

    private var currentWishlist: List<BookEntity> = emptyList()

    init {
        viewModelScope.launch {
            bookStoreRepository.getBooksInWishList().collect { wishlist ->
                currentWishlist = wishlist
                updateCurrentStateWithWishlist(wishlist)
            }
            bookStoreRepository.observeBookmarkStatus().collect { (_, isbn13) ->
                /*currentWishlist = updatedWishlist
                updateCurrentStateWithWishlist(updatedWishlist)*/
            }
        }
    }

    private fun updateCurrentStateWithWishlist(wishlist: List<BookEntity>) {
        withState<SearchTabState.Success.SearchResult> { currentState ->
            val updatedList =
                currentState.modelList.map { item ->
                    if (item is BookModel) {
                        item.copy(isLiked = wishlist.any { it.isbn13 == item.isbn13 })
                    } else {
                        item
                    }
                }
            if (updatedList != currentState.modelList) {
                setState(currentState.copy(modelList = updatedList))
            }
        }
    }

    override fun fetchData(): Job =
        viewModelScope.launch {
            setState(SearchTabState.Loading)
            try {
                searchRepository.getAllSearchHistories().collect { searchHistories ->
                    setState(
                        SearchTabState.Success.SearchHistory(
                            searchHistories
                                .sortedByDescending { it.searchTimestamp }
                                .map {
                                    SearchHistoryModel(
                                        id = it.searchKeyword,
                                        text = it.searchKeyword
                                    )
                                }
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setState(SearchTabState.Error(e))
            }
        }

    fun searchByHistory(keyword: String) =
        viewModelScope.launch {
            searchRepository.getSearchHistory(keyword).collect { searchHistory ->
                searchHistory?.let {
                    searchRepository.deleteSearchHistory(searchHistory.searchKeyword)
                    searchByKeyword(searchHistory.searchKeyword)
                }
            }
        }

    fun removeHistory(keyword: String) =
        viewModelScope.launch {
            try {
                searchRepository.deleteSearchHistory(keyword)
                searchRepository.getAllSearchHistories().collect { searchHistories ->
                    setState(
                        SearchTabState.Success.SearchHistory(
                            searchHistories
                                .sortedByDescending { it.searchTimestamp }
                                .map {
                                    SearchHistoryModel(
                                        id = it.searchKeyword,
                                        text = it.searchKeyword
                                    )
                                }
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setState(SearchTabState.Error(e))
            }
        }

    fun searchByKeyword(keyword: String) =
        viewModelScope.launch {
            try {
                setState(SearchTabState.Loading)
                searchRepository.saveSearchHistory(
                    SearchHistoryEntity(
                        searchKeyword = keyword,
                        searchTimestamp = Date().time
                    )
                )
                bookStoreRepository.searchBooksByKeyword(keyword).collect { (bookList, page, total) ->
                    setState(
                        SearchTabState.Success.SearchResult(
                            bookList.map { book ->
                                BookModel(
                                    id = book.isbn13,
                                    title = book.title,
                                    subtitle = book.subtitle,
                                    isbn13 = book.isbn13,
                                    price = book.price,
                                    image = book.image,
                                    url = book.url,
                                    isLiked =
                                        currentWishlist.any {
                                            it.isbn13 == book.isbn13
                                        }
                                )
                            },
                            keyword,
                            page,
                            total
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                setState(SearchTabState.Error(e, keyword))
            }
        }

    private var isLoading: Boolean = false
    private var isErrorOccurred: Boolean = false
    fun loadMoreSearchResult(isLoadRetry: Boolean = false) =
        viewModelScope.launch {
            try {
                if (isLoadRetry) {
                    isErrorOccurred = false
                }
                if (isLoading || isErrorOccurred) return@launch
                withState<SearchTabState.Success.SearchResult> { state ->
                    if (state.totalResultCount == 0) return@launch

                    /**
                     * 데이터 로드 요청 중 상태라면 표현 이 때, 기존 Retry Button이 보함 될 수 있으므로 마지막에 있는지 체크 후
                     * 로딩 추가
                     */
                    setState(
                        state.copy(
                            modelList = state.modelList.toMutableList().apply {
                                val lastModel = last()
                                if (lastModel is BookLoadRetryModel && lastModel.id == "RetryButton"
                                ) {
                                    remove(lastModel)
                                }
                                add(BookLoadingModel(id = "loading"))
                            }
                        )
                    )

                    state.searchKeyword?.let { keyword ->
                        val modelList = state.modelList.toMutableList()
                        isLoading = true
                        val nextPage = state.currentPage?.toInt()?.plus(1).toString()
                        bookStoreRepository
                            .searchBooksByKeyword(keyword, nextPage).collect { (bookList, page, total) ->
                                val appendModelList =
                                    bookList.map { book ->
                                        BookModel(
                                            id = book.isbn13,
                                            title = book.title,
                                            subtitle = book.subtitle,
                                            isbn13 = book.isbn13,
                                            price = book.price,
                                            image = book.image,
                                            url = book.url,
                                            isLiked =
                                                currentWishlist.any {
                                                    it.isbn13 == book.isbn13
                                                }
                                        )
                                    }

                                setState(
                                    state.copy(
                                        modelList =
                                            modelList.apply {
                                                val lastModel = last()
                                                if (lastModel is BookLoadingModel && lastModel.id == "loading") {
                                                    remove(lastModel)
                                                }
                                                if (lastModel is BookLoadRetryModel && lastModel.id == "RetryButton") {
                                                    remove(lastModel)
                                                }
                                                addAll(appendModelList)
                                            },
                                        currentPage = page,
                                        totalResultCount = total
                                    )
                                )
                                isLoading = false
                                isErrorOccurred = false
                            }
                    }
                }
            } catch (e: Exception) {
                if (isErrorOccurred) return@launch
                withState<SearchTabState.Success.SearchResult> { state ->
                    val modelList = state.modelList.toMutableList()
                    setState(
                        state.copy(
                            modelList =
                                modelList.apply {
                                    val lastModel = last()
                                    if (lastModel is BookLoadingModel && lastModel.id == "loading") {
                                        remove(lastModel)
                                    }
                                    if (filterIsInstance<BookLoadRetryModel>().isEmpty()) {
                                        add(
                                            BookLoadRetryModel(
                                                id = "RetryButton",
                                                errorMessage =
                                                    e.localizedMessage
                                            )
                                        )
                                    }
                                },
                        )
                    )
                }
                isErrorOccurred = true
                isLoading = false
            }
        }

    fun toggleLikeButton(book: BookModel) {
        viewModelScope.launch {
            if (book.isLiked == true) {
                bookStoreRepository.removeBookInWishList(book.isbn13)
            } else {
                bookStoreRepository.addBookInWishList(
                    BookEntity(
                        title = book.title,
                        subtitle = book.subtitle,
                        isbn13 = book.isbn13,
                        price = book.price,
                        image = book.image,
                        url = book.url
                    )
                )
            }
        }
    }

    fun handleOnRetry(state: SearchTabState) {
        when (val state = state) {
            is SearchTabState.Success.SearchResult -> { // Retry for load more
                loadMoreSearchResult(true)
            }

            is SearchTabState.Error -> { // Retry for general error
                if (state.searchKeyword != null) {
                    searchByKeyword(state.searchKeyword)
                } else {
                    fetchData()
                }
            }

            else -> {}
        }
    }
}
