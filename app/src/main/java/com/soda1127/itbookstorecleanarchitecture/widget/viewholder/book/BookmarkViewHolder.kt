package com.soda1127.itbookstorecleanarchitecture.widget.viewholder.book

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.soda1127.itbookstorecleanarchitecture.extensions.clear
import com.soda1127.itbookstorecleanarchitecture.extensions.load
import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books.BookmarkListener
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderBookmarkBinding

class BookmarkViewHolder(
    private val binding: ViewholderBookmarkBinding,
): ModelViewHolder<BookModel, BookmarkListener>(binding) {

    override fun reset() = with(binding) {
        bookImageView.clear()
    }

    override fun bindData(model: BookModel) {
        super.bindData(model)
        with(binding) {
            bookImageView.load(model.image, 24f, CenterCrop())
            titleTextView.text = model.title

            val subtitle = model.subtitle
            if (subtitle.isEmpty()) {
                subtitleTextView.isGone = true
            } else {
                subtitleTextView.isVisible = true
            }
            subtitleTextView.text = model.subtitle

            val price = model.price
            if (price.isEmpty()) {
                priceTextView.isGone = true
            } else {
                priceTextView.isVisible = true
            }
            priceTextView.text = price

            likedButton.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context, if (model.isLiked == true) {
                        R.drawable.ic_heart_enable
                    } else {
                        R.drawable.ic_heart_disable
                    }
                )
            )
        }
    }

    override fun bindViews(model: BookModel, listener: BookmarkListener) {
        binding.root.setOnClickListener {
            listener.onClickBookItem(model)
        }
        binding.likedButton.setOnClickListener {
            listener.onClickLikedButton(model)
        }
    }

}
