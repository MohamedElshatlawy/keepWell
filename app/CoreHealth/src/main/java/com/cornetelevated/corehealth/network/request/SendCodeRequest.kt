package com.cornetelevated.corehealth.network.request

class SendCodeRequest(private val mobile: String) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["mobile"] = mobile
        return map
    }
}