package com.airwallex.android.card

import android.app.Activity
import android.content.Context
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.AirwallexPaymentManager
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertNotNull

class CardComponentTest {
    private lateinit var component: CardComponent
    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var listener: Airwallex.PaymentResultListener

    @Before
    fun setUp() {
        mockkObject(ThreeDSecurityManager)
        activity = mockk()
        context = mockk()
        listener = mockk(relaxed = true)
        component = CardComponent()
    }

    @After
    fun unmockStatics() {
        unmockkObject(ThreeDSecurityManager)
    }

    @Test
    fun `test Provider`() {
        assertNotNull(CardComponent.PROVIDER)
    }

    @Test
    fun `test handlePaymentIntentResponse when next action is redirect with card model`() {
        val redirectAction = NextAction(
            type = NextAction.NextActionType.REDIRECT_FORM,
            data = null,
            dcc = null,
            url = null,
            method = null,
            fallbackUrl = null,
            packageName = null
        )
        val cardModel = CardNextActionModel(
            paymentManager = AirwallexPaymentManager(AirwallexApiRepository()),
            clientSecret = "tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            device = null,
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
            currency = "CNY",
            amount = BigDecimal.TEN,
            paymentConsentId = null
        )
        handlePaymentIntentResponse(redirectAction, cardModel)
        verify(exactly = 1) {
            ThreeDSecurityManager.handleThreeDSFlow("id", activity, null, redirectAction, cardModel, listener, resultCallBack = any())
        }
    }

    private fun handlePaymentIntentResponse(action: NextAction? = null, model: CardNextActionModel? = null) {
        component.handlePaymentIntentResponse(
            paymentIntentId = "id",
            nextAction = action,
            activity = activity,
            applicationContext = context,
            cardNextActionModel = model,
            listener = listener
        )
    }
}
