package com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book

import com.soda1127.itbookstorecleanarchitecture.model.book.BookLoadingModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.AdapterListener
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookLoadingBinding

class BookLoadingViewHolder(
    binding: ViewholderBookLoadingBinding
): ModelViewHolder<BookLoadingModel, AdapterListener>(binding) {

    override fun reset() = Unit

    override fun bindViews(model: BookLoadingModel, listener: AdapterListener) = Unit

}
