package com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.soda1127.itbookstorecleanarchitecture.extensions.clear
import com.soda1127.itbookstorecleanarchitecture.extensions.load
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BooksListener
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookBinding

class BookViewHolder(
    private val binding: ViewholderBookBinding,
): ModelViewHolder<BookModel, BooksListener>(binding) {

    override fun reset() = with(binding) {
        bookImageView.clear()
    }

    override fun bindData(model: BookModel) {
        super.bindData(model)
        with(binding) {
            bookImageView.load(model.image, 24f, CenterCrop())
            titleTextView.text = model.title
            subtitleTextView.text = model.subtitle
            priceTextView.text = model.price
        }
    }

    override fun bindViews(model: BookModel, listener: BooksListener) {
        binding.root.setOnClickListener {
            listener.onClickBookItem(model)
        }
    }

}
