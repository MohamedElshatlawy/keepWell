package com.cornetelevated.corehealth.extensions

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.isValidEmail(): Boolean {
    val emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
    val pattern: Pattern = Pattern.compile(emailRegex)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}


fun String.isValidPhone(): Boolean {
    val phoneRegex = "01[0125][0-9]{8}"
    val pattern: Pattern = Pattern.compile(phoneRegex)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isValidPassword(): Boolean {
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\\d]{8,}$"
    val pattern: Pattern = Pattern.compile(passwordRegex)
    val matcher: Matcher = pattern.matcher(this)

    val passwordRegexWithSpecialCharacters = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
    val spPattern: Pattern = Pattern.compile(passwordRegexWithSpecialCharacters)
    val spMatcher: Matcher = spPattern.matcher(this)

    return (matcher.matches()||spMatcher.matches())
}

fun String.isValidUsername(): Boolean {
    // val usernamePattern = "[a-zA-Z0-9]{2,20}"
    // val usernamePattern = "[a-zA-Z]{2,20}"
    val usernamePattern = "[a-z]{2,20}"
    val pattern: Pattern = Pattern.compile(usernamePattern)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}