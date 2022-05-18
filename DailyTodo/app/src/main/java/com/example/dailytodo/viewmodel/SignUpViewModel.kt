package com.example.dailytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dailytodo.data.model.SignUpModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private var _signUpResponse = MutableStateFlow<SignUpModel?>(null)
    var signUpResponse: StateFlow<SignUpModel?> = _signUpResponse
    private var _error = MutableStateFlow<String?>(null)
    var error: StateFlow<String?> = _error
    fun signUpEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.e("signUpCheck", "createUserWithEmail:success")
                val user = firebaseAuth.currentUser
                _signUpResponse.value = SignUpModel(user?.email!!, user.uid)
            } else {
                _error.value = task.exception.toString()
            }
        }

    }
}