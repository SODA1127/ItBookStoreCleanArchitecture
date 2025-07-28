package com.soda1127.itbookstorecleanarchitecture.model.book

import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.Model

data class BookLoadingModel(
    override val id: String,
    override val type: CellType = CellType.BOOK_LOADING_CELL,
): Model(id, type)
