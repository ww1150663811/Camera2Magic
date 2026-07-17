package com.nothing.camera2magic.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val repository: ConfigRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        _uiState.value = SettingsUiState(
            playSound = repository.playSound,
            enableLog = repository.enableLog,
            injectMenu = repository.injectMenu,
            manualRotation = repository.manualRotation
        )
    }
    fun onPlaySoundToggled() {
        _uiState.update { currentState ->
            val newState = !currentState.playSound
            repository.playSound = newState
            currentState.copy(playSound = newState)
        }
    }

    fun onEnableLogToggled() {
        _uiState.update { currentState ->
            val newState = !currentState.enableLog
            repository.enableLog = newState
            currentState.copy(enableLog = newState)
        }
    }

    fun onInjectMenuToggled() {
        _uiState.update { currentState ->
            val newState = !currentState.injectMenu
            repository.injectMenu = newState
            currentState.copy(injectMenu = newState)
        }
    }
    fun onManualRotationClicked() {
        _uiState.update { currentState ->
            val newRotation = (currentState.manualRotation + 90) % 360
            repository.manualRotation = newRotation
            currentState.copy(manualRotation = newRotation)
        }
    }
}

data class SettingsUiState(
    val playSound: Boolean = false,
    val enableLog: Boolean = false,
    val injectMenu: Boolean = false,
    val manualRotation: Int = 0
)
