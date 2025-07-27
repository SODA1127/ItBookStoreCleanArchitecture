package com.soda1127.itbookstorecleanarchitecture.widget.adapter.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.soda1127.example.bookstore.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.Model
import com.soda1127.example.bookstore.widget.adapter.listener.AdapterListener
import com.soda1127.example.bookstore.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book.BookLoadRetryViewHolder
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book.BookLoadingViewHolder
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book.BookViewHolder
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book.BookmarkViewHolder
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.search.SearchHistoryViewHolder
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookBinding
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookLoadRetryBinding
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookLoadingBinding
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookmarkBinding
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderSearchHistoryBinding

object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M: Model, L: AdapterListener> map(
        parent: ViewGroup,
        type: CellType,
    ): ModelViewHolder<M, L> {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = when (type) {
            CellType.BOOK_CELL -> BookViewHolder(
                ViewholderBookBinding.inflate(inflater, parent, false)
            )
            CellType.BOOKMARK_CELL -> BookmarkViewHolder(
                ViewholderBookmarkBinding.inflate(inflater, parent, false)
            )
            CellType.SEARCH_HISTORY_CELL -> SearchHistoryViewHolder(
                ViewholderSearchHistoryBinding.inflate(inflater, parent, false)
            )
            CellType.BOOK_LOADING_CELL -> BookLoadingViewHolder(
                ViewholderBookLoadingBinding.inflate(inflater, parent, false)
            )
            CellType.BOOK_LOAD_RETRY_CELL -> BookLoadRetryViewHolder(
                ViewholderBookLoadRetryBinding.inflate(inflater, parent, false)
            )
        }

        return viewHolder as ModelViewHolder<M, L>
    }

}
