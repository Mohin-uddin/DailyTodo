package com.example.dailytodo.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dailytodo.R
import com.example.dailytodo.utils.ConstValue
import com.example.dailytodo.utils.gone
import com.example.dailytodo.utils.show
import com.example.dailytodo.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var dialouge: Dialog
    private  val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hideAndShowPassword()

        tvSignIn.setOnClickListener {
            if(validation()){
                dialouge= ConstValue.animation(this,0)
                viewModel.signIn(etLoginEmail.text.toString().trim(),etLoginPassword.text.toString().trim())
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        lifecycleScope.launch {
            viewModel.loginResponse.collect {
                if (it!=null)
                dialouge.dismiss()
                if (it?.isSuccess == true){
                    val preference = getSharedPreferences("Todo", Context.MODE_PRIVATE)
                    val editor = preference.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putString("userID", it.userId)
                    editor.apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect {
                if(it!=null) {
                    dialouge.dismiss()
                    Toast.makeText(this@LoginActivity, "There is no user record corresponding to this identifier", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun validation(): Boolean{
        if(etLoginEmail.text.toString() == "")
        {
            etLoginEmail.requestFocus()
            etLoginEmail.error=getString(R.string.enter_your_email)
            Log.e("dataSetChange", "validation: namesaed")
            return false
        }
        if(etLoginPassword.text.toString() == "") {
            etLoginPassword.requestFocus()
            etLoginPassword.error="Enter your valid password"
            return false
        }
        if(etLoginEmail.text.isEmpty()||etLoginPassword.text.isEmpty())
            return false
        else if(etLoginPassword.text.length<6){
            etLoginPassword.requestFocus()
            etLoginPassword.error="Invalid password"
            return false
        }

        return true
    }
    fun hideAndShowPassword(){
        ivVHide.setOnClickListener {
            ivPView.show()
            ivVHide.gone()
            etLoginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        ivPView.setOnClickListener {
            ivVHide.show()
            ivPView.gone()
            etLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}