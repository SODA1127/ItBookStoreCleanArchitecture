package com.soda1127.itbookstorecleanarchitecture.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookEntity(
    val title: String,
    val subtitle: String,
    val isbn13: String,
    val price: String,
    val image: String,
    val url: String
): Parcelable
