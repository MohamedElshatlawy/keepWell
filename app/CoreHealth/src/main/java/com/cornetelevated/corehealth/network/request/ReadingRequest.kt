package com.cornetelevated.corehealth.network.request

class ReadingRequest(private var from: String, private var to: String) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["From"] = from
        map["To"] = to
        return map
    }
}