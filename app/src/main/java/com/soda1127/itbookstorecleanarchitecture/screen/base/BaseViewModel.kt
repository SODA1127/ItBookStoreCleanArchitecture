package com.soda1127.itbookstorecleanarchitecture.screen.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : State, E : Event> : ViewModel() {

    open fun fetchData(): Job = viewModelScope.launch {  }

    abstract fun getInitialState(): S

    private val _stateFlow = MutableStateFlow(getInitialState())
    val stateFlow: StateFlow<S> = _stateFlow

    private val _eventFlow = MutableSharedFlow<E>()
    val eventFlow: SharedFlow<E> = _eventFlow

    protected val jobs = mutableListOf<Job>()

    override fun onCleared() {
        jobs.forEach { it.cancel() }
        super.onCleared()
    }

    fun setState(state: S) {
        _stateFlow.value = state
    }

    inline fun <reified S: State> withState(withState: (S) -> Unit) {
        val currentState = stateFlow.value
        if (currentState is S) {
            withState(currentState as S)
        }
    }

    fun sendEvent(event: E) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

}
