package com.airwallex.android.core.data
import androidx.activity.ComponentActivity
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.PaymentMethod
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexCVCParamTest {

    private lateinit var activity: ComponentActivity
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var session: AirwallexSession

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        activity = mockk()
        paymentMethod = mockk()
        session = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test AirwallexCVCParam creation`() {
        val paymentConsentId = "12345"

        val airwallexCVCParam = AirwallexCVCParam(
            activity = activity,
            paymentMethod = paymentMethod,
            session = session,
            paymentConsentId = paymentConsentId
        )

        assertEquals(activity, airwallexCVCParam.activity)
        assertEquals(paymentMethod, airwallexCVCParam.paymentMethod)
        assertEquals(session, airwallexCVCParam.session)
        assertEquals(paymentConsentId, airwallexCVCParam.paymentConsentId)
    }
}