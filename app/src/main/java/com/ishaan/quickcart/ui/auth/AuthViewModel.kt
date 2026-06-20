package com.ishaan.quickcart.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authUseCase = AppModule.authUseCase

    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState

    private val _signupState = MutableStateFlow<Resource<User>?>(null)
    val signupState: StateFlow<Resource<User>?> = _signupState

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    val isLoggedIn: Boolean
        get() = authUseCase.isLoggedIn

    val currentUserId: String
        get() = authUseCase.currentUser?.uid ?: ""

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = authUseCase.login(email, password)
            if (result is Resource.Success) {
                fetchUserProfile(result.data.uid)
            } else {
                _loginState.value = result as Resource<User>
            }
        }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _signupState.value = Resource.Loading
            val result = authUseCase.signup(name, email, password, phone, address)
            if (result is Resource.Success) {
                fetchUserProfile(result.data.uid)
            } else {
                _signupState.value = result as Resource<User>
            }
        }
    }

    fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            android.util.Log.d("AuthDebug", "Fetching profile for uid: $uid")
            when (val result = authUseCase.getUserProfile(uid)) {
                is Resource.Success -> {
                    android.util.Log.d("AuthDebug", "Profile fetched: isAdmin=${result.data.isAdmin}, name=${result.data.name}")
                    _userProfile.value = result.data
                    _loginState.value = Resource.Success(result.data)
                    _signupState.value = Resource.Success(result.data)
                }
                is Resource.Error -> {
                    android.util.Log.e("AuthDebug", "Profile fetch FAILED: ${result.message}")
                    val email = authUseCase.currentUser?.email ?: ""
                    val basicUser = com.ishaan.quickcart.data.model.User(
                        uid = uid,
                        name = email.substringBefore("@"),
                        email = email,
                        phone = "",
                        address = "",
                        isAdmin = false
                    )
                    _userProfile.value = basicUser
                    _loginState.value = Resource.Success(basicUser)
                    _signupState.value = Resource.Success(basicUser)
                }
                Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        authUseCase.logout()
        _userProfile.value = null
        _loginState.value = null
        _signupState.value = null
    }
}