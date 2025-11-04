package com.airwallex.android.core

import android.app.Activity
import com.airwallex.android.core.model.Device
import java.math.BigDecimal

/**
 * Model containing necessary information for handling card payment next actions,
 * particularly for 3DS authentication flows.
 *
 * @param paymentManager Manager for handling payment operations
 * @param clientSecret Client secret for the payment intent
 * @param device Device information for fingerprinting
 * @param paymentIntentId ID of the payment intent being processed
 * @param currency Currency code for the transaction
 * @param amount Transaction amount
 * @param activityProvider Lambda function that provides the current activity reference.
 *        This is crucial for handling configuration changes (e.g., screen rotation).
 *        Instead of capturing a static activity reference that becomes stale after
 *        configuration changes, this provider is called dynamically to always get
 *        the current, valid activity instance. This ensures that activities launched
 *        during async operations (like ThreeDSecurityActivity) use the correct activity
 *        context even after multiple screen rotations.
 */
@Suppress("LongParameterList")
class CardNextActionModel(
    val paymentManager: PaymentManager,
    val clientSecret: String,
    val device: Device?,
    val paymentIntentId: String?,
    val currency: String,
    val amount: BigDecimal,
    val activityProvider: (() -> Activity)
)