package com.cornetelevated.corehealth.network.response

import org.json.JSONObject

data class JSONResponse(var error: String, var success: Boolean, var response: JSONObject)