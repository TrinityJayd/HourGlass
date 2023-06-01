package com.trinityjayd.hourglass

import java.util.regex.Pattern


class ValidationMethods {

    private val check: Regex? = null

    fun onlyLetters(text: String): Boolean {
        var isValid: Boolean
        //Check if only letters
        val lettersPattern = "^[a-zA-Z\\s]*$"
        val letters = Pattern.compile(lettersPattern)
        isValid = letters.matcher(text).matches()
        return isValid
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return email.matches(emailRegex)
    }

    fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")
        return password.matches(passwordRegex)
    }

    







}