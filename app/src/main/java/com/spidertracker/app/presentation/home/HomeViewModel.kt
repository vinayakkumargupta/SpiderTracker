package com.spidertracker.app.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onStartTrackingClicked() {
        // Logic handled by Navigation/Activity for permissions
    }
}

data class HomeUiState(
    val isLoading: Boolean = false
)
