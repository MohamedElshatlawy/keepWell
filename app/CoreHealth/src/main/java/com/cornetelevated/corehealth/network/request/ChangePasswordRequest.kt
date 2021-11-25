package com.cornetelevated.corehealth.network.request

class ChangePasswordRequest(private val oldPass: String, private val newPass: String) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["OldPassword"] = oldPass
        map["Password"] = newPass
        map["ConfirmPassword"] = newPass
        return map
    }
}