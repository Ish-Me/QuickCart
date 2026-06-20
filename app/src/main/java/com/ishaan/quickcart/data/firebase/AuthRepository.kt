package com.ishaan.quickcart.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Constants
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // Save user profile to Firestore
            val userModel = User(
                uid = user.uid,
                name = name,
                email = email,
                phone = phone,
                address = address,
                isAdmin = false
            )
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.uid)
                .set(userModel)
                .await()

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun getUserProfile(uid: String): Resource<User> {
        return try {
            val doc = firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .await()

            android.util.Log.d("AuthDebug", "Raw Firestore data: ${doc.data}")

            val user = doc.toObject(User::class.java)
                ?: return Resource.Error("User profile not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch profile")
        }
    }
    suspend fun createUserDocument(uid: String, user: User) {
        try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(user)
                .await()
        } catch (e: Exception) {
            // Silent fail — user can still use the app
        }
    }

    fun logout() {
        auth.signOut()
    }
}