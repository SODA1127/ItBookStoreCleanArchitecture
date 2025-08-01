package com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book

import android.view.View
import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadRetryModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.AdapterListener
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BooksListener
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookLoadRetryBinding

class BookLoadRetryViewHolder(
    private val binding: ViewholderBookLoadRetryBinding
): ModelViewHolder<BookLoadRetryModel, AdapterListener>(binding) {

    override fun reset() = with(binding) {
        errorCauseTextView.visibility = View.GONE
    }

    override fun bindData(model: BookLoadRetryModel) {
        super.bindData(model)
        with(binding) {
            val errorMessage = model.errorMessage
            if (errorMessage?.isNotEmpty() == true) {
                errorCauseTextView.visibility = View.VISIBLE
                errorCauseTextView.text = errorMessage
            }
        }
    }

    override fun bindViews(model: BookLoadRetryModel, listener: AdapterListener) {
        if (listener is BooksListener) {
            binding.root.setOnClickListener {
                listener.onClickLoadRetry()
            }
        }
    }

}
