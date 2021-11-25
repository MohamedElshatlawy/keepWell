package com.cornetelevated.corehealth.network.request

class LoginRequest(private var email: String?, private var mobile: String?, private var password: String) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        email?.let { map["Email"] = it }
        mobile?.let {
            map["Mobile"] = it
            map["IsPractice"] = true
        }
        map["Password"] = password
        map["IsMobileLogin"] = true
        return map
    }
}