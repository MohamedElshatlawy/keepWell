package com.cornetelevated.corehealth.network.request

class SearchPhysycianRequest(var searchText: String, private var specialityId: Int, private var pageNumber: Int) {
    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["areaId"] = 0
        map["governorateId"] = 0
        map["specialityId"] = specialityId
        map["name"] = searchText
        map["pageNumber"] = pageNumber
        map["pageSize"] = 10
        return map
    }
}