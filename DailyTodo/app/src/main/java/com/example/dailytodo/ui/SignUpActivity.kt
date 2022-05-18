package com.example.dailytodo.ui

import android.app.Dialog
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
import com.example.dailytodo.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var dialouge: Dialog
    private val viewModel : SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        allClickableFunction()

        tvSignUp.setOnClickListener {
             if (validation()){
                 dialouge= ConstValue.animation(this,0)
                 viewModel.signUpEmailAndPassword(etSignEmail.text.toString().trim(),etSignPassword.text.toString().trim())
             }
        }

        lifecycleScope.launch {
            viewModel.signUpResponse.collect {
                if (it!=null){
                    dialouge.dismiss()
                    onBackPressed()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect {
                if(it!=null) {
                    dialouge.dismiss()
                    Toast.makeText(this@SignUpActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun allClickableFunction(){
        ivSignConfirmPassHide.setOnClickListener {
            ivSignConfirmPassShow.show()
            ivSignConfirmPassHide.gone()
            etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        ivSignConfirmPassShow.setOnClickListener {
            ivSignConfirmPassShow.gone()
            ivSignConfirmPassHide.show()
            etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        }
        ivSignPassHide.setOnClickListener {
            ivSignPassShow.show()
            ivSignPassHide.gone()
            etSignPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        ivSignPassShow.setOnClickListener {
            ivSignPassShow.gone()
            ivSignPassHide.show()
            etSignPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        }
        tvSignUp.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validation(): Boolean {
        if(etSignEmail.text.toString() == "")
        {
            etSignEmail.requestFocus()
            etSignEmail.error=getString(R.string.enter_your_email)
            Log.e("dataSetChange", "validation: namesaed")
            return false
        }
        if(etSignPassword.text.toString() == "") {
            etSignPassword.requestFocus()
            etSignPassword.error="Enter your valid password"
            return false
        }
        if(etConfirmPassword.text.toString() == "") {
            etConfirmPassword.requestFocus()
            etConfirmPassword.error="Enter your valid password"
            return false
        }

        Log.e("passwordCheack", "validation: "+etSignPassword.text.toString()+"  "+etConfirmPassword.text.toString() )
        if(etSignPassword.text.toString()!=etConfirmPassword.text.toString())
        {
            etSignPassword.requestFocus()
            etSignPassword.error=getString(R.string.password_match)
            return false
        }
        if(etSignPassword.text.toString().length<6)
        {
            etSignPassword.requestFocus()
            etSignPassword.error=getString(R.string.password_sort)
            return false
        }
        if(etConfirmPassword.text.toString().length<6)
        {
            etConfirmPassword.requestFocus()
            //   showPopUpError("Password is too sort")
            etConfirmPassword.error=getString(R.string.password_sort)
            return false
        }


        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}