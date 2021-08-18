package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class TrackerRequestTest {

    private val trackerRequest by lazy {
        TrackerRequest.Builder()
            .setComplete(false)
            .setEmpty(false)
            .setType(TrackerRequest.TrackerType.CARD)
            .setError("aaa")
            .setIntentId("aaa")
            .setStatus("aaa")
            .setNextActionType("dcc")
            .setNextActionUrl("www.airwallex.com")
            .setBrand("brand")
            .setCardBin("bin")
            .setPath("path")
            .setReq(null)
            .setRes(null)
            .setHeader(null)
            .setOrigin("1111")
            .setCode(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED)
            .build()
    }

    @Test
    fun testParams() {
        assertEquals("1111", trackerRequest.origin)
        assertEquals(false, trackerRequest.complete)
        assertEquals(false, trackerRequest.empty)
        assertEquals(null, trackerRequest.application)
        assertEquals(TrackerRequest.TrackerType.CARD, trackerRequest.type)

        val code = trackerRequest.code!!
        assertEquals("onPaymentMethodCreated", code.value)
        assertEquals(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED, code)

        assertEquals("aaa", trackerRequest.error)
        assertEquals("aaa", trackerRequest.intentId)
        assertEquals("aaa", trackerRequest.status)
        assertEquals("dcc", trackerRequest.nextActionType)
        assertEquals("www.airwallex.com", trackerRequest.nextActionUrl)
        assertEquals("brand", trackerRequest.brand)
        assertEquals("bin", trackerRequest.cardBin)
        assertEquals("path", trackerRequest.path)
        assertEquals(null, trackerRequest.req)
        assertEquals(null, trackerRequest.res)
        assertEquals(null, trackerRequest.header)
    }

    @Test
    fun testToParamMap() {
        val trackerParams = trackerRequest.toParamMap()
        assertEquals(
            mapOf(
                "origin" to "1111",
                "complete" to "false",
                "empty" to "false",
                "application" to "android",
                "type" to "card",
                "code" to "onPaymentMethodCreated",
                "error" to "aaa",
                "intent_id" to "aaa",
                "status" to "aaa",
                "next_action_type" to "dcc",
                "next_action_url" to "www.airwallex.com",
                "brand" to "brand",
                "cardBin" to "bin",
                "path" to "path"
            ).toString(),
            trackerParams.toString()
        )
    }

    @Test
    fun testTrackerCode() {
        assertEquals("onReady", TrackerRequest.TrackerCode.ON_READY.value)
        assertEquals("onSubmit", TrackerRequest.TrackerCode.ON_SUBMIT.value)
        assertEquals(
            "onDynamicCurrencyConversion",
            TrackerRequest.TrackerCode.ON_DYNAMIC_CURRENCY_CONVERSION.value
        )
        assertEquals("onSuccess", TrackerRequest.TrackerCode.ON_SUCCESS.value)
        assertEquals("onError", TrackerRequest.TrackerCode.ON_ERROR.value)
        assertEquals("onCancel", TrackerRequest.TrackerCode.ON_CANCEL.value)
        assertEquals("onFocus", TrackerRequest.TrackerCode.ON_FOCUS.value)
        assertEquals("onBlur", TrackerRequest.TrackerCode.ON_BLUR.value)
        assertEquals("onChange", TrackerRequest.TrackerCode.ON_CHANGE.value)
        assertEquals("onClick", TrackerRequest.TrackerCode.ON_CLICK.value)
        assertEquals("onResize", TrackerRequest.TrackerCode.ON_RESIZE.value)
        assertEquals("onRedirect", TrackerRequest.TrackerCode.ON_REDIRECT.value)
        assertEquals("onComplete", TrackerRequest.TrackerCode.ON_COMPLETE.value)
        assertEquals("onChallenge", TrackerRequest.TrackerCode.ON_CHALLENGE.value)
        assertEquals("onChallengeSuccess", TrackerRequest.TrackerCode.ON_CHALLENGE_SUCCESS.value)
        assertEquals("onChallengeError", TrackerRequest.TrackerCode.ON_CHALLENGE_ERROR.value)
        assertEquals("onIntentRetrieved", TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED.value)
        assertEquals(
            "onIntentRetrievedError",
            TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED_ERROR.value
        )
        assertEquals(
            "onPaymentMethodCreated",
            TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED.value
        )
        assertEquals(
            "onPaymentMethodCreatedError",
            TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED_ERROR.value
        )
        assertEquals("onLogRequest", TrackerRequest.TrackerCode.ON_LOG_REQUEST.value)
        assertEquals("onLogResponse", TrackerRequest.TrackerCode.ON_LOG_RESPONSE.value)
        assertEquals("onSwitchMethod", TrackerRequest.TrackerCode.ON_SWITCH_METHOD.value)
        assertEquals(
            "onClickConfirmButton",
            TrackerRequest.TrackerCode.ON_CLICK_CONFIRM_BUTTON.value
        )
    }
}
