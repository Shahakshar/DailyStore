package com.example.dailystore.utils

import android.util.Patterns


fun validateEmail(email: String) : RegisterValidation{
    if (email.isEmpty()) {
        return RegisterValidation.Failed("Email cant be empty")
    }
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return RegisterValidation.Failed("Wrong format")
    }
    return RegisterValidation.Success
}

fun validationPassword(password: String): RegisterValidation {
    if(password.isEmpty()){
        return RegisterValidation.Failed("password cant be empty")
    }

    if(password.length < 6 || password.length > 10) {
        return RegisterValidation.Failed("please enter password in valid format")
    }
    return RegisterValidation.Success
}