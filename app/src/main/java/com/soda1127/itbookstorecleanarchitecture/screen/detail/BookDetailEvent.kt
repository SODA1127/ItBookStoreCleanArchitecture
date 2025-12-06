package com.soda1127.itbookstorecleanarchitecture.screen.detail

import com.soda1127.itbookstorecleanarchitecture.screen.base.Event

sealed class BookDetailEvent : Event {

    data class ShowToast(val message: String) : BookDetailEvent()

}
