package com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.books

import com.soda1127.itbookstorecleanarchitecture.model.book.BookModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.AdapterListener

interface BooksListener: AdapterListener {

    fun onClickBookItem(model: BookModel)

    fun onClickLoadRetry()

}
