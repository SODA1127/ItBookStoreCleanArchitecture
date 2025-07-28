package com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books

import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel

interface BookmarkListener: BooksListener {

    override fun onClickBookItem(model: BookModel)

    fun onClickLikedButton(model: BookModel)

}
