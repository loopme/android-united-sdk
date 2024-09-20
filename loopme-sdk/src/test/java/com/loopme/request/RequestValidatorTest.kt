package com.loopme.request


import com.loopme.loaders.AdRequestType
import com.loopme.request.RequestBuilder.*
import com.loopme.request.RequestValidator.Companion.BANNER_HEIGHT
import com.loopme.request.RequestValidator.Companion.BANNER_WIDTH
import com.loopme.request.RequestValidator.Companion.EVENTS_EXT_PN
import com.loopme.request.RequestValidator.Companion.EVENTS_EXT_PV
import com.loopme.request.RequestValidator.Companion.SOURCE_EXT_PN
import com.loopme.request.RequestValidator.Companion.SOURCE_EXT_PV
import com.loopme.request.RequestValidator.Companion.VIDEO_HEIGHT
import com.loopme.request.RequestValidator.Companion.VIDEO_WIDTH
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors


class RequestValidatorTest {

    private val validator = RequestValidator()
    private val classLoader = ClassLoader.getSystemClassLoader()
    private val dummyAdRequestType = AdRequestType(true, true, true)

    @Test
    fun `No app object`() {
        val request = JSONObject(convertFileToString("no_app"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertTrue(violations.size == 1)
        assertEquals(RequestValidator.NOT_PRESENT, violations[APPKEY])
    }

    @Test
    fun `No appKey`() {
        val req = JSONObject(convertFileToString("no_appkey"))
        val isValid = validator.validateOrtbRequest(req, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertTrue(violations.size == 1)
        assertEquals(RequestValidator.NOT_PRESENT, violations[APPKEY])
    }

    @Test
    fun `Empty appKey`() {
        val request = JSONObject(convertFileToString("empty_appkey"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertTrue(violations.size == 1)
        assertEquals(RequestValidator.BLANK, violations[APPKEY])
    }

    @Test
    fun `No source_ext_omidpn,no source_ext_omidpv`() {
        val request = JSONObject(convertFileToString("all_required_not_present"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.NOT_PRESENT, violations[SOURCE_EXT_PN])
        assertEquals(RequestValidator.NOT_PRESENT, violations[SOURCE_EXT_PV])
    }

    @Test
    fun `Empty source_ext_omidpn and source_ext_omidpv`() {
        val request = JSONObject(convertFileToString("all_required_empty"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.BLANK, violations[SOURCE_EXT_PN])
        assertEquals(RequestValidator.BLANK, violations[SOURCE_EXT_PV])
    }

    @Test
    fun `No events_ext_omidpn,no events_ext_omidpv`() {
        val request = JSONObject(convertFileToString("all_required_not_present"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.NOT_PRESENT, violations[EVENTS_EXT_PN])
        assertEquals(RequestValidator.NOT_PRESENT, violations[EVENTS_EXT_PV])
    }

    @Test
    fun `Empty events_ext_omidpn and events_ext_omidpv`() {
        val request = JSONObject(convertFileToString("all_required_empty"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.BLANK, violations[EVENTS_EXT_PN])
        assertEquals(RequestValidator.BLANK, violations[EVENTS_EXT_PV])
    }

    @Test
    fun `Banner width and height zero`() {
        val request = JSONObject(convertFileToString("all_required_empty"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.VALUE_0, violations[BANNER_WIDTH])
        assertEquals(RequestValidator.VALUE_0, violations[BANNER_HEIGHT])
    }

    @Test
    fun `Banner width and height not present`() {
        val request = JSONObject(convertFileToString("all_required_not_present"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[BANNER_WIDTH])
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[BANNER_HEIGHT])
    }

    @Test
    fun `Video width and height invalid value`() {
        val request = JSONObject(convertFileToString("all_required_empty"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType.copy())
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[VIDEO_WIDTH])
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[VIDEO_HEIGHT])
    }

    @Test
    fun `Video width and height not present`() {
        val request = JSONObject(convertFileToString("all_required_not_present"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType.copy())
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[VIDEO_WIDTH])
        assertEquals(RequestValidator.NOT_PRESENT_OR_INVALID, violations[VIDEO_HEIGHT])
    }

    @Test
    fun `Banner only when is not a video not rewarded`() {
        val request = JSONObject(convertFileToString("banner_only"))
        val adRequestType = dummyAdRequestType.copy(isBanner = true, isVideo = false, isRewarded = false)
        val isValid = validator.validateOrtbRequest(request, adRequestType)
        val violations = validator.violations
        assertFalse(isValid)
        assertEquals(RequestValidator.VALUE_0, violations[BANNER_WIDTH])
        assertEquals(RequestValidator.VALUE_0, violations[BANNER_HEIGHT])
        assertFalse(violations.containsKey(VIDEO_HEIGHT))
        assertFalse(violations.containsKey(VIDEO_WIDTH))
    }

    @Test
    fun `Is valid for banner and video`() {
        val request = JSONObject(convertFileToString("proper_ad_request"))
        val isValid = validator.validateOrtbRequest(request, dummyAdRequestType)
        val violations = validator.violations
        assertTrue(isValid)
        assertEquals(0, violations.size)
    }

    private fun convertFileToString(fileName: String) =
        BufferedReader(
            InputStreamReader(
                classLoader.getResourceAsStream(fileName)
            )
        ).lines().collect(Collectors.joining("\n"))
}