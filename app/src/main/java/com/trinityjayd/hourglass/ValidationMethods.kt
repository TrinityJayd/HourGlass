package com.trinityjayd.hourglass

import java.util.regex.Pattern


class ValidationMethods {

    fun onlyLetters(text: String): Boolean {
        val isValid: Boolean
        //Check if only letters
        val lettersPattern = "^[a-zA-Z\\s]*$"
        val letters = Pattern.compile(lettersPattern)
        isValid = letters.matcher(text).matches()
        return isValid
    }

    fun isEmailValid(email: String): Boolean {
        //Check if email is valid
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return email.matches(emailRegex)
    }

    fun isPasswordValid(password: String): Boolean {
        //Check if password is valid
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")
        return password.matches(passwordRegex)
    }

    







}