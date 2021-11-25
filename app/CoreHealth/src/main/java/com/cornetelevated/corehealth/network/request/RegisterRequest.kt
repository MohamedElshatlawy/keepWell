package com.cornetelevated.corehealth.network.request

class RegisterRequest(
    val mobile: String,
    private val username: String,
    private val firstName: String,
    private val lastName: String,
    private val dob: String,
    private val email: String, private val password: String) {

    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["username"] = username
        map["firstName"] = firstName
        map["lastName"] = lastName
        map["email"] = email
        map["mobile"] = mobile
        map["password"] = password
        map["dob"] = dob
        map["IsPractice"] = true
        return map
    }
}

