package com.airwallex.android.core

import androidx.activity.ComponentActivity
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("LargeClass")
class DefaultAvailablePaymentMethodsServiceTest {

    @MockK
    private lateinit var mockPaymentManager: PaymentManager

    @MockK
    private lateinit var mockActivity: ComponentActivity

    @RelaxedMockK
    private lateinit var mockPaymentSession: AirwallexPaymentSession

    @RelaxedMockK
    private lateinit var mockRecurringSession: AirwallexRecurringSession

    @RelaxedMockK
    private lateinit var mockRecurringWithIntentSession: AirwallexRecurringWithIntentSession

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var service: DefaultAvailablePaymentMethodsService

    private val testPaymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
    private val testClientSecret = "test_client_secret_abc123"
    private val testCustomerId = "cus_test_customer_id"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mockkObject(AirwallexLogger)
        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexLogger.error(any(), any()) } just runs

        every { mockPaymentSession.paymentIntent } returns testPaymentIntent
        every { mockPaymentSession.hidePaymentConsents } returns false
        every { mockPaymentSession.locale } returns null
        val clientSecret = testPaymentIntent.clientSecret ?: ""
        every { mockPaymentSession.clientSecret } returns clientSecret

        every { mockRecurringSession.clientSecret } returns testClientSecret
        every { mockRecurringSession.hidePaymentConsents } returns true
        every { mockRecurringSession.locale } returns null

        service = DefaultAvailablePaymentMethodsService(
            paymentManager = mockPaymentManager,
            activityProvider = { mockActivity },
            setupAnalyticsLogger = {},
            resolveLanguageCode = { "en" }
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Tests for fetchAvailablePaymentMethodsAndConsents()
    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods and consents for payment session with customerId`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val wechatMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
        }
        val paymentMethods = listOf(cardMethod, wechatMethod)

        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val paymentConsents = listOf(consent1, consent2)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            serviceSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns paymentConsents

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(2, methods.size)
        assertEquals(2, consents.size)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents when customerId is null`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns null

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents when customerId is null
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents when hidePaymentConsents is true`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId
        every { mockPaymentSession.hidePaymentConsents } returns true

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents when hidePaymentConsents is true
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents for recurring session`() = runTest {
        val serviceSpy = spyk(service)

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockRecurringSession, testClientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockRecurringSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents for recurring session
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents filters methods by session paymentMethods list`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns null
        every { mockPaymentSession.paymentMethods } returns listOf("card", "wechatpay")

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val wechatMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
        }
        val alipayMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "alipay"
        }
        val allMethods = listOf(cardMethod, wechatMethod, alipayMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns allMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, _) = result.getOrThrow()
        assertEquals(2, methods.size)
        assertEquals(true, methods.any { it.name == PaymentMethodType.CARD.value })
        assertEquals(true, methods.any { it.name == "wechatpay" })
        assertEquals(false, methods.any { it.name == "alipay" })
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents adds maestro when mastercard is present`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns null
        every { mockPaymentSession.paymentMethods } returns null

        val cardMethod = AvailablePaymentMethodType(
            name = PaymentMethodType.CARD.value,
            cardSchemes = listOf(
                com.airwallex.android.core.model.CardScheme("visa"),
                com.airwallex.android.core.model.CardScheme("mastercard"),
            ),
        )

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (methods, _) = result.getOrThrow()
        val schemeNames = methods.first { it.name == PaymentMethodType.CARD.value }
            .cardSchemes.orEmpty().map { it.name }
        assertEquals(listOf("visa", "mastercard", "maestro"), schemeNames)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents does not add maestro when mastercard is absent`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns null
        every { mockPaymentSession.paymentMethods } returns null

        val cardMethod = AvailablePaymentMethodType(
            name = PaymentMethodType.CARD.value,
            cardSchemes = listOf(com.airwallex.android.core.model.CardScheme("visa")),
        )

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (methods, _) = result.getOrThrow()
        val schemeNames = methods.first { it.name == PaymentMethodType.CARD.value }
            .cardSchemes.orEmpty().map { it.name }
        assertEquals(listOf("visa"), schemeNames)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents does not duplicate maestro when already present`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns null
        every { mockPaymentSession.paymentMethods } returns null

        val cardMethod = AvailablePaymentMethodType(
            name = PaymentMethodType.CARD.value,
            cardSchemes = listOf(
                com.airwallex.android.core.model.CardScheme("mastercard"),
                com.airwallex.android.core.model.CardScheme("maestro"),
            ),
        )

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (methods, _) = result.getOrThrow()
        val schemeNames = methods.first { it.name == PaymentMethodType.CARD.value }
            .cardSchemes.orEmpty().map { it.name }
        assertEquals(listOf("mastercard", "maestro"), schemeNames)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents filters consents to only card type for payment session`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        val cardConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val wechatConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns "wechatpay"
            }
        }
        val allConsents = listOf(cardConsent, wechatConsent)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            serviceSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns allConsents

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(1, consents.size)
        assertEquals(PaymentMethodType.CARD.value, consents[0].paymentMethod?.type)
    }

    // Tests for needRequestConsent scenarios
    @Test
    fun `fetchAvailablePaymentMethodsAndConsents requests consents for Session with customerId`() = runTest {
        val serviceSpy = spyk(service)

        val mockSession = mockk<Session> {
            every { customerId } returns testCustomerId
            every { clientSecret } returns testPaymentIntent.clientSecret
            every { paymentMethods } returns null
            every { hidePaymentConsents } returns false
        }

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        val consent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consents = listOf(consent)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            serviceSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns consents

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, returnedConsents) = result.getOrThrow()
        assertEquals(1, returnedConsents.size)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents does not request consents for Session with empty customerId`() = runTest {
        val serviceSpy = spyk(service)

        val mockSession = mockk<Session> {
            every { customerId } returns ""
            every { clientSecret } returns testPaymentIntent.clientSecret
            every { paymentMethods } returns null
        }

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(0, consents.size) // No consents for empty customerId
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents does not request consents for Session with hidePaymentConsents true`() = runTest {
        val serviceSpy = spyk(service)

        val mockSession = mockk<Session> {
            every { customerId } returns testCustomerId
            every { clientSecret } returns testPaymentIntent.clientSecret
            every { paymentMethods } returns null
            every { hidePaymentConsents } returns true
        }

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(0, consents.size) // No consents when hidePaymentConsents is true
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents does not request consents for AirwallexRecurringWithIntentSession`() = runTest {
        val serviceSpy = spyk(service)

        every { mockRecurringWithIntentSession.customerId } returns testCustomerId
        every { mockRecurringWithIntentSession.clientSecret } returns testPaymentIntent.clientSecret
        every { mockRecurringWithIntentSession.paymentMethods } returns null
        every { mockRecurringWithIntentSession.hidePaymentConsents } returns true

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockRecurringWithIntentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockRecurringWithIntentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(0, consents.size) // No consents for AirwallexRecurringWithIntentSession
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents requests consents with correct parameters for AirwallexPaymentSession`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId
        every { mockPaymentSession.hidePaymentConsents } returns false

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        val paymentMethods = listOf(cardMethod)

        val consent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consents = listOf(consent)

        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            serviceSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns consents

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, returnedConsents) = result.getOrThrow()
        assertEquals(1, returnedConsents.size)
    }

    // Tests for retrieveAvailablePaymentConsentsPaged and loadPagedItems pagination logic
    @Test
    fun `pagination logic handles single page response`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }

        val singlePageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent1, consent2)
            every { hasMore } returns false // Single page
        }

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        } returns singlePageResponse

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(2, consents.size)

        coVerify(exactly = 1) {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles multiple pages`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent3 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }

        val firstPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent1, consent2)
            every { hasMore } returns true
        }

        val secondPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent3)
            every { hasMore } returns false
        }

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(
                match { params ->
                    params.pageNum == 0
                }
            )
        } returns firstPageResponse

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(
                match { params ->
                    params.pageNum == 1
                }
            )
        } returns secondPageResponse

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(3, consents.size) // 2 from first page + 1 from second page

        coVerify(exactly = 2) {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles empty first page`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val emptyPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns emptyList()
            every { hasMore } returns false
        }

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        } returns emptyPageResponse

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(0, consents.size)

        coVerify(exactly = 1) {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles three pages correctly`() = runTest {
        val serviceSpy = spyk(service)
        every { mockPaymentSession.customerId } returns testCustomerId

        val consents = createCardConsents(count = 4)
        val pages = listOf(
            createConsentPage(listOf(consents[0]), hasMore = true),
            createConsentPage(listOf(consents[1], consents[2]), hasMore = true),
            createConsentPage(listOf(consents[3]), hasMore = false)
        )

        setupPaginatedConsentMocks(serviceSpy, pages)
        setupPaymentMethodMocks(serviceSpy)

        val result = serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(true, result.isSuccess)
        val (_, retrievedConsents) = result.getOrThrow()
        assertEquals(4, retrievedConsents.size) // 1 + 2 + 1 = 4 total

        coVerify(exactly = 3) {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic increments pageNum correctly`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val requestedPageNums = mutableListOf<Int>()

        val page1Response = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(
                mockk {
                    every { paymentMethod } returns mockk {
                        every { type } returns PaymentMethodType.CARD.value
                    }
                }
            )
            every { hasMore } returns true
        }

        val page2Response = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(
                mockk {
                    every { paymentMethod } returns mockk {
                        every { type } returns PaymentMethodType.CARD.value
                    }
                }
            )
            every { hasMore } returns false
        }

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(any())
        } answers {
            val params = firstArg<RetrieveAvailablePaymentConsentsParams>()
            requestedPageNums.add(params.pageNum)
            if (params.pageNum == 0) page1Response else page2Response
        }

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(listOf(0, 1), requestedPageNums)
    }

    @Test
    fun `pagination logic uses correct parameters for consent retrieval`() = runTest {
        val serviceSpy = spyk(service)

        every { mockPaymentSession.customerId } returns testCustomerId

        val capturedParams = slot<RetrieveAvailablePaymentConsentsParams>()

        val singlePageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns emptyList()
            every { hasMore } returns false
        }

        coEvery {
            serviceSpy.retrieveAvailablePaymentConsents(capture(capturedParams))
        } returns singlePageResponse

        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            serviceSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        serviceSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        assertEquals(testPaymentIntent.clientSecret, capturedParams.captured.clientSecret)
        assertEquals(testCustomerId, capturedParams.captured.customerId)
        assertEquals(0, capturedParams.captured.pageNum) // First page
        assertNull(capturedParams.captured.nextTriggeredBy)
        assertEquals(PaymentConsent.PaymentConsentStatus.VERIFIED, capturedParams.captured.status)
    }

    // Helper methods for pagination tests
    private fun createCardConsents(count: Int): List<PaymentConsent> {
        return List(count) {
            mockk {
                every { paymentMethod } returns mockk {
                    every { type } returns PaymentMethodType.CARD.value
                }
            }
        }
    }

    private fun createConsentPage(items: List<PaymentConsent>, hasMore: Boolean): Page<PaymentConsent> {
        return mockk {
            every { this@mockk.items } returns items
            every { this@mockk.hasMore } returns hasMore
        }
    }

    private fun setupPaginatedConsentMocks(
        spy: DefaultAvailablePaymentMethodsService,
        pages: List<Page<PaymentConsent>>
    ) {
        pages.forEachIndexed { index, page ->
            coEvery {
                spy.retrieveAvailablePaymentConsents(match { it.pageNum == index })
            } returns page
        }
    }

    private fun setupPaymentMethodMocks(spy: DefaultAvailablePaymentMethodsService) {
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns null
        }
        coEvery {
            spy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)
    }
}
