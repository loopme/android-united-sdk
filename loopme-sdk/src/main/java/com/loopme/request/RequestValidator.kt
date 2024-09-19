package com.loopme.request

import com.loopme.request.RequestBuilder.*
import org.json.JSONObject

class RequestValidator {

    val violations by lazy { HashMap<String, String>() }

    // return true if valid, false if not
    // fail the bigger process if false
    // send error with string of violations
    fun validateOrtbRequest(ortbRequest: JSONObject): Boolean {
        var isValid = true
        val app = ortbRequest.optJSONObject(APP)
        if (app == null) {
            isValid = false
            violations[APP] = NOT_PRESENT
        } else {
            if (app.optString(APPKEY).isNullOrBlank()) {
                isValid = false
                violations[APP] = NULL_OR_BLANK
            }
        }
        return isValid
    }

    companion object {
        const val NOT_PRESENT = "field not present"
        const val NULL_OR_BLANK = "is null or blank"
    }
}