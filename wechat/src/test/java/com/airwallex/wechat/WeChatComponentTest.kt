package com.airwallex.wechat

import android.app.Activity
import android.content.Context
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.WeChat
import com.airwallex.android.wechat.WeChatComponent
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class WeChatComponentTest {
    lateinit var component: WeChatComponent
    lateinit var activity: Activity
    lateinit var context: Context
    lateinit var listener: Airwallex.PaymentResultListener
    private val actionData = mapOf(
        "appId" to "wx4c86d73fe4f82431",
        "nonceStr" to "DUY8tIYUmyKO6Lhb1jTBFKUBWNud6XXu",
        "package" to "Sign=WXPay",
        "partnerId" to "403011682",
        "prepayId" to "airwallex.com/pa/mock/wechat/hk/v2/qr_code_scanned",
        "sign" to "EDD7AFB573F30F4C131898D631AA5ED3DA8FE92289536A6BE43426E71F2A2798",
        "timeStamp" to "1629872988"
    )

    @Before
    fun setUp() {
        component = spyk(recordPrivateCalls = true)
        activity = mockk()
        context = mockk()
        listener = mockk(relaxed = true)

        val mockAPI = mockk<IWXAPI>()
        mockkStatic(WXAPIFactory::class)
        every { WXAPIFactory.createWXAPI(context, null, true) } returns mockAPI
    }

    @After
    fun unmockStatics() {
        unmockkStatic(WXAPIFactory::class)
    }

    @Test
    fun `test provider`() {
        assertNotNull(WeChatComponent.PROVIDER)
    }

    @Test
    fun `test handlePaymentIntentResponse when WeChat Pay is successfully initiated`() {
        every { component["initiateWeChatPay"](any<WeChat>()) } returns true
        handlePaymentIntentResponse(actionData)

        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.InProgress("id")) }
    }

    @Test
    fun `test handlePaymentIntentResponse when next action data is null`() {
        handlePaymentIntentResponse()

        verify(exactly = 1) { listener.onCompleted(any()) }
    }

    @Test
    fun `test handlePaymentIntentResponse when next action data is empty`() {
        every { component["initiateWeChatPay"](any<WeChat>()) } returns true
        handlePaymentIntentResponse(mapOf())

        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.InProgress("id")) }
    }

    private fun handlePaymentIntentResponse(actionData: Map<String, Any?>? = null) {
        component.handlePaymentIntentResponse(
            paymentIntentId = "id",
            nextAction = NextAction(
                type = NextAction.NextActionType.CALL_SDK,
                dcc = null,
                url = null,
                method = "post",
                data = actionData
            ),
            activity = activity,
            applicationContext = context,
            cardNextActionModel = null,
            listener = listener
        )
    }
}
