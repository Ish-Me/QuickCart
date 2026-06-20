package com.ishaan.quickcart.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.ishaan.quickcart.data.firebase.AuthRepository
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Resource

class AuthUseCase {

    private val repository = AuthRepository()

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    val isLoggedIn: Boolean
        get() = repository.isLoggedIn

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        if (email.isBlank()) return Resource.Error("Email cannot be empty")
        if (password.isBlank()) return Resource.Error("Password cannot be empty")
        if (password.length < 6) return Resource.Error("Password must be at least 6 characters")
        return repository.login(email.trim(), password)
    }

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ): Resource<FirebaseUser> {
        if (name.isBlank()) return Resource.Error("Name cannot be empty")
        if (email.isBlank()) return Resource.Error("Email cannot be empty")
        if (password.isBlank()) return Resource.Error("Password cannot be empty")
        if (password.length < 6) return Resource.Error("Password must be at least 6 characters")
        if (phone.isBlank()) return Resource.Error("Phone cannot be empty")
        if (address.isBlank()) return Resource.Error("Address cannot be empty")
        return repository.signup(name.trim(), email.trim(), password, phone.trim(), address.trim())
    }

    suspend fun getUserProfile(uid: String): Resource<User> {
        return repository.getUserProfile(uid)
    }

    fun logout() {
        repository.logout()
    }
}