package com.ishaan.quickcart.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authUseCase = AppModule.authUseCase

    private val _profileState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val profileState: StateFlow<Resource<User>> = _profileState

    val currentUserId: String
        get() = authUseCase.currentUser?.uid ?: ""

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading
            _profileState.value = authUseCase.getUserProfile(currentUserId)
        }
    }

    fun logout() {
        authUseCase.logout()
    }
}