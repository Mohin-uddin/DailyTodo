package com.example.dailytodo.viewmodel

import android.app.Dialog
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dailytodo.data.model.LoginModel
import com.example.dailytodo.data.model.SignUpModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel() {



    private var _loginResponse = MutableStateFlow<LoginModel?>(null)
    var loginResponse : StateFlow<LoginModel?> = _loginResponse
    private var _error = MutableStateFlow<String?>(null)
    var error: StateFlow<String?> = _error
    fun signIn(email:String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                _loginResponse.value= LoginModel(it.result.user?.email.toString(),true,it.result.user?.uid.toString())

            }
            else
            {
                _error.value=it.exception.toString()
                Log.e("FirebaseAuthError", "signIn: ${it.exception}" )
            }
        }
    }


}