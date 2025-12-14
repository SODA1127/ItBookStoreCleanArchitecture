package com.soda1127.itbookstorecleanarchitecture.model.book

import androidx.compose.runtime.Immutable
import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.Model

@Immutable
data class BookLoadRetryModel(
    override val id: String,
    override val type: CellType = CellType.BOOK_LOAD_RETRY_CELL,
    val errorMessage: String? = null,
) : Model(id, type)
