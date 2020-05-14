package com.airwallex.android

import android.content.Context
import android.content.Intent
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import org.json.JSONArray
import java.util.*

internal object ThreeDSecure {

    const val THREE_DS_RETURN_URL = "http://requestbin.net/r/uu7y0yuu"

    /**
     * Configure Cardinal Mobile SDK
     */
    private fun configureCardinal(applicationContext: Context) {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        cardinalConfigurationParameters.environment = AirwallexPlugins.threeDSecureEnv
        cardinalConfigurationParameters.requestTimeout = 8000
        cardinalConfigurationParameters.challengeTimeout = 5

        val rTYPE = JSONArray()
        rTYPE.put(CardinalRenderType.OTP)
        rTYPE.put(CardinalRenderType.SINGLE_SELECT)
        rTYPE.put(CardinalRenderType.MULTI_SELECT)
        rTYPE.put(CardinalRenderType.OOB)
        rTYPE.put(CardinalRenderType.HTML)
        cardinalConfigurationParameters.renderType = rTYPE

        cardinalConfigurationParameters.uiType = CardinalUiType.BOTH

        val yourUICustomizationObject = UiCustomization()
        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject

        Cardinal.getInstance().configure(applicationContext, cardinalConfigurationParameters)
    }

    internal fun performCardinalInitialize(
        applicationContext: Context,
        serverJwt: String,
        completion: (consumerSessionId: String?, validateResponse: ValidateResponse?) -> Unit
    ) {
        configureCardinal(applicationContext)

        // Setup the Initial Call to Cardinal
        Cardinal.getInstance().init(serverJwt, object : CardinalInitService {

            /**
             * You may have your Submit button disabled on page load. Once you are set up
             * for CCA, you may then enable it. This will prevent users from submitting
             * their order before CCA is ready.
             */
            override fun onSetupCompleted(consumerSessionId: String) {
                Logger.debug("onSetupCompleted $consumerSessionId")
                completion.invoke(consumerSessionId, null)
            }

            /**
             * If there was an error with setup, cardinal will call this function with
             * validate response and empty serverJWT
             * @param validateResponse
             * @param serverJWT will be an empty
             */
            override fun onValidated(validateResponse: ValidateResponse, serverJWT: String?) {
                Logger.debug("onValidated")
                completion.invoke(null, validateResponse)
            }
        })
    }

    /**
     * Perform the 3DS authentication.
     *
     * @param fragment [ThreeDSecureFragment] will be responsible for handling callbacks to it's listeners
     * @param threeDSecureLookup Contains information about the 3DS verification request that will be invoked in this method.
     */
    @Suppress("DEPRECATION")
    internal fun performCardinalAuthentication(
        fragment: ThreeDSecureFragment,
        threeDSecureLookup: ThreeDSecureLookup
    ) {
        val intent = Intent(fragment.activity, ThreeDSecureActivity::class.java)
        intent.putExtra(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_LOOKUP, threeDSecureLookup)

        fragment.startActivityForResult(intent, ThreeDSecureActivity.THREE_D_SECURE)
    }

    internal fun onActivityResult(
        data: Intent,
        callback: ThreeDSecureCallback
    ) {
        when (data.getSerializableExtra(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_TYPE) as? ThreeDSecureType) {
            ThreeDSecureType.THREE_D_SECURE_1 -> {
                // 1.0 Flow
                val payload = data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_PAYLOAD)
                val cancel = data.getBooleanExtra(ThreeDSecureActivity.EXTRA_THREE_CANCEL, false)
                Logger.debug("3DS 1 response payload: $payload")
                if (payload != null) {
                    callback.onSuccess(payload)
                } else {
                    if (cancel) {
                        callback.onFailed(AirwallexError(message = "3DS canceled"))
                    } else {
                        callback.onFailed(AirwallexError(message = "3DS failed"))
                    }
                }
            }
            ThreeDSecureType.THREE_D_SECURE_2 -> {
                // 2.0 Flow
                val validateResponse = data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse

                if (validateResponse.actionCode != null && validateResponse.actionCode == CardinalActionCode.CANCEL) {
                    callback.onFailed(AirwallexError(message = "3DS canceled"))
                } else {
                    if (validateResponse.errorDescription.toLowerCase(Locale.getDefault()) == "success") {
                        Logger.debug("3DS 2 response processorTransactionId: ${validateResponse.payment.processorTransactionId}")
                        callback.onSuccess(validateResponse.payment.processorTransactionId)
                    } else {
                        callback.onFailed(AirwallexError(message = validateResponse.errorDescription))
                    }
                }
            }
        }
    }

    enum class ThreeDSecureType {
        THREE_D_SECURE_1,
        THREE_D_SECURE_2
    }
}
