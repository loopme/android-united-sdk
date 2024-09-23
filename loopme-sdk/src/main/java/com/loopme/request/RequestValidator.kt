package com.loopme.request

import com.loopme.loaders.AdRequestType
import com.loopme.request.RequestBuilder.*
import org.json.JSONObject

internal class RequestValidator {

    private var isValid = true

    /**
     * After calling validateOrtbRequest function, this field should be populated. If empty hashmap, it means, that request is valid
     */
    val violations by lazy { HashMap<String, String>() }


    /**
     * Return true if request valid, false otherwise
     */
    fun validateOrtbRequest(ortbRequest: JSONObject, adRequestType: AdRequestType): Boolean {
        isValid = true
        val appKey = ortbRequest.optJSONObject(APP)?.optString(APPKEY, FALLBACK)
        when {
            appKey == null || appKey == FALLBACK -> violate(APPKEY, NOT_PRESENT)
            appKey.isBlank() -> violate(APPKEY, BLANK)
        }

        val sourceExtJson = ortbRequest.optJSONObject(SOURCE)
            ?.optJSONObject(EXT)
        val sourceExtOmidpn = sourceExtJson
            ?.optString(OMID_PARTNER_NAME, FALLBACK)

        when {
            sourceExtOmidpn == null || sourceExtOmidpn == FALLBACK -> violate(SOURCE_EXT_PN, NOT_PRESENT)
            sourceExtOmidpn.isBlank() -> violate(SOURCE_EXT_PN, BLANK)
        }

        val sourceExtOmidpv = sourceExtJson
            ?.optString(OMID_PARTNER_VERSION, FALLBACK)

        when {
            sourceExtOmidpv == null || sourceExtOmidpv == FALLBACK -> violate(SOURCE_EXT_PV, NOT_PRESENT)
            sourceExtOmidpv.isBlank() -> violate(SOURCE_EXT_PV, BLANK)
        }

        val eventsExtJson = ortbRequest.optJSONObject(EVENTS)
            ?.optJSONObject(EXT)
        val eventsExtOmidpn = eventsExtJson
            ?.optString(OMID_PARTNER_NAME, FALLBACK)

        when {
            eventsExtOmidpn == null || eventsExtOmidpn == FALLBACK -> violate(EVENTS_EXT_PN, NOT_PRESENT)
            eventsExtOmidpn.isBlank() -> violate(EVENTS_EXT_PN, BLANK)
        }

        val eventsExtOmidpv = eventsExtJson
            ?.optString(OMID_PARTNER_VERSION, FALLBACK)

        when {
            eventsExtOmidpv == null || eventsExtOmidpv == FALLBACK -> violate(EVENTS_EXT_PV, NOT_PRESENT)
            eventsExtOmidpv.isBlank() -> violate(EVENTS_EXT_PV, BLANK)
        }

        if (adRequestType.isBanner) {
            val bannerJson = ortbRequest.optJSONArray(IMP)
                ?.getJSONObject(0)
                ?.optJSONObject(BANNER)
            if (bannerJson == null) {
                violate(IMP_BANNER, NOT_PRESENT)
            } else {
                val bannerWidth = bannerJson
                    .optInt(WIDTH, -1)
                when (bannerWidth) {
                    -1 -> violate(BANNER_WIDTH, NOT_PRESENT_OR_INVALID)
                    0 -> violate(BANNER_WIDTH, VALUE_0)
                }

                val bannerHeight = bannerJson
                    .optInt(HEIGHT, -1)
                when (bannerHeight) {
                    -1 -> violate(BANNER_HEIGHT, NOT_PRESENT_OR_INVALID)
                    0 -> violate(BANNER_HEIGHT, VALUE_0)
                }
            }
        }
        if (adRequestType.isVideo || adRequestType.isRewarded) {
            val videoJson = ortbRequest.optJSONArray(IMP)
                ?.getJSONObject(0)
                ?.optJSONObject(VIDEO)
            if (videoJson == null) {
                violate(IMP_VIDEO, NOT_PRESENT)
            } else {

                val videoWidth = videoJson
                    .optInt(WIDTH, -1)
                when (videoWidth) {
                    -1 -> violate(VIDEO_WIDTH, NOT_PRESENT_OR_INVALID)
                    0 -> violate(VIDEO_WIDTH, VALUE_0)
                }

                val bannerHeight = videoJson
                    .optInt(HEIGHT, -1)
                when (bannerHeight) {
                    -1 -> violate(VIDEO_HEIGHT, NOT_PRESENT_OR_INVALID)
                    0 -> violate(VIDEO_HEIGHT, VALUE_0)
                }
            }
        }

        return isValid
    }

    private fun violate(fieldName: String, issue: String): Boolean {
        isValid = false
        violations[fieldName] = issue
        return false
    }

    internal companion object {
        const val NOT_PRESENT = "not present or null"
        const val NOT_PRESENT_OR_INVALID = "not present or invalid"
        const val BLANK = "is blank"
        const val FALLBACK = "fallback"
        const val VALUE_0 = "is 0"
        const val SOURCE_EXT_PV = "$SOURCE.$EXT.$OMID_PARTNER_VERSION"
        const val SOURCE_EXT_PN = "$SOURCE.$EXT.$OMID_PARTNER_NAME"
        const val EVENTS_EXT_PV = "$EVENTS.$EXT.$OMID_PARTNER_VERSION"
        const val EVENTS_EXT_PN = "$EVENTS.$EXT.$OMID_PARTNER_NAME"
        const val BANNER_HEIGHT = "$IMP.$BANNER.$HEIGHT"
        const val BANNER_WIDTH = "$IMP.$BANNER.$WIDTH"
        const val VIDEO_HEIGHT = "$IMP.$VIDEO.$HEIGHT"
        const val VIDEO_WIDTH = "$IMP.$VIDEO.$WIDTH"
        const val IMP_VIDEO = "$IMP.$VIDEO"
        const val IMP_BANNER = "$IMP.$BANNER"
    }
}

internal class InvalidOrtbRequestException(message: String) : Exception(message)