package com.loopme.request

import org.json.JSONObject
import org.junit.Assert.*
import com.loopme.request.RequestBuilder.*


import org.junit.Before
import org.junit.Test

class RequestValidatorTest {

    private val validator = RequestValidator()

    @Before
    fun setUp() {
    }

    @Test
    fun `test one`() {
        assertTrue(true)
    }

    @Test
    fun `No app object`() {
        val req = JSONObject(NO_APP)
        val isValid = validator.validateOrtbRequest(req)
        val violations = validator.violations
        assertFalse(isValid)
        assertTrue(violations.size == 1)
        assertEquals(RequestValidator.NOT_PRESENT, violations[APP])
    }

    private companion object {
        const val NO_APP = """{
	"tmax": 700,
	"bcat": ["IAB25-3", "IAB25", "IAB26"],
	"id": "c2c226ca-d2b1-4088-9f5e-d773db0c8753",
	"source": {
		"ext": {
			"omidpn": "Loopme",
			"omidpv": "9.0.6"
		}
	},
	"events": {
		"apis": [7],
		"ext": {
			"omidpn": "Loopme",
			"omidpv": "9.0.6"
		}
	},
	"regs": {
		"coppa": 0,
		"ext": {
			"us_privacy": "1---"
		}
	},
	"user": {
		"ext": {
			"consent_type": 2
		}
	},
	"device": {
		"js": 1,
		"os": "android",
		"devicetype": 4,
		"w": 1440,
		"h": 2892,
		"ifa": "835fa444-cd80-4acf-b0fb-34011201af1e",
		"osv": "15",
		"connectiontype": 2,
		"language": "en",
		"make": "Google",
		"hwv": "ranchu",
		"ua": "Mozilla\/5.0 (Linux; Android 15; sdk_gphone64_arm64 Build\/AP31.240617.003; wv) AppleWebKit\/537.36 (KHTML, like Gecko) Version\/4.0 Chrome\/127.0.6533.105 Mobile Safari\/537.36",
		"dnt": "0",
		"model": "sdk_gphone64_arm64",
		"ext": {
			"timezone": "Central European Standard Time",
			"orientation": "p",
			"chargelevel": "100",
			"batterylevel": 8,
			"batterysaver": 0,
			"plugin": -1
		}
	},
	"imp": [{
		"banner": {
			"id": 1,
			"battr": [3, 8],
			"w": 320,
			"h": 480,
			"api": [5, 2, 7]
		},
		"video": {
			"maxduration": 999,
			"linearity": 1,
			"boxingallowed": 1,
			"startdelay": 1,
			"sequence": 1,
			"minduration": 5,
			"maxbitrate": 1024,
			"protocols": [2, 3, 7, 8],
			"battr": [3, 8],
			"mimes": ["video\/mp4"],
			"delivery": [2],
			"w": 320,
			"h": 480,
			"api": [5, 2, 7],
			"skip": 1,
			"skipafter": 5,
			"ext": {
				"rewarded": 0
			}
		},
		"secure": 1,
		"bidfloor": 0,
		"displaymanager": "LOOPME_SDK",
		"displaymanagerver": "9.0.6",
		"id": "1726737191780",
		"metric": [],
		"instl": 1,
		"ext": {
			"it": "normal"
		}
	}],
	"ext": {
		"sdk_init_time": 5
	}
}"""
    }
}