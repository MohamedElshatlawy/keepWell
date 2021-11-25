package com.cornetelevated.corehealth.network.request

class VerifyRequest(private val mobile: String?, private val code: Int) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        if (mobile == null) {
            map["ActivationNumber"] = code
        } else {
            mobile.let {
                map["mobile"] = it
                map["VerificationCode"] = code
            }
        }
        return map
    }
}