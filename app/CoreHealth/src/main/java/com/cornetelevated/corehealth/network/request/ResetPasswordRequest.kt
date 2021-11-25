package com.cornetelevated.corehealth.network.request

class ResetPasswordRequest(private val userId: Int, private val newPass: String) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["Password"] = newPass
        map["Id"] = userId
        return map
    }
}