package com.airwallex.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import org.json.JSONArray

internal object ThreeDSecure {

    const val THREE_DS_RETURN_URL = "http://com.airwallex.android"

    /**
     * Configure Cardinal Mobile SDK
     */
    private fun configureCardinal(applicationContext: Context) {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        // TODO should change to PRO when release
        cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING
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
        onSetupCompleted: (consumerSessionId: String?, validateResponse: ValidateResponse?) -> Unit
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
                onSetupCompleted.invoke(consumerSessionId, null)
            }

            /**
             * If there was an error with setup, cardinal will call this function with
             * validate response and empty serverJWT
             * @param validateResponse
             * @param serverJWT will be an empty
             */
            override fun onValidated(validateResponse: ValidateResponse, serverJWT: String?) {
                onSetupCompleted.invoke(null, validateResponse)
            }
        })
    }

    /**
     * Perform the 3DS authentication.
     *
     * @param fragment [ThreeDSecureFragment] will be responsible for handling callbacks to it's listeners
     * @param threeDSecureLookup Contains information about the 3DS verification request that will be invoked in this method.
     */
    internal fun performCardinalAuthentication(
        fragment: ThreeDSecureFragment,
        threeDSecureLookup: ThreeDSecureLookup
    ) {
        val extras = Bundle()
        extras.putParcelable(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_LOOKUP, threeDSecureLookup)

        val intent = Intent(fragment.context, ThreeDSecureActivity::class.java)
        intent.putExtras(extras)

        fragment.startActivityForResult(intent, ThreeDSecureActivity.THREE_D_SECURE)
    }

    internal fun onActivityResult(
        data: Intent,
        threeDSecureCallback: ThreeDSecureCallback
    ) {
        when (data.getSerializableExtra(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_TYPE) as? ThreeDSecureType) {
            ThreeDSecureType.THREE_D_SECURE_1 -> {
                // 1.0 Flow
                val payload = data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_PAYLOAD)
                Logger.debug("3DS 1 response payload: $payload")

                // TODO
            }
            ThreeDSecureType.THREE_D_SECURE_2 -> {
                // 2.0 Flow
                val validateResponse =
                    data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse

                val actionCode = validateResponse.actionCode
                Logger.debug("3DS 2 response code: $actionCode")

                if (actionCode == null) {
                    threeDSecureCallback.onFailed(AirwallexError(message = "No 3DS response code from Cardinal"))
                    return
                }
                when (actionCode) {
                    CardinalActionCode.FAILURE, CardinalActionCode.SUCCESS, CardinalActionCode.NOACTION -> {
                        threeDSecureCallback.onSuccess(validateResponse)
                    }
                    CardinalActionCode.ERROR, CardinalActionCode.TIMEOUT -> {
                        threeDSecureCallback.onFailed(AirwallexError(message = validateResponse.errorDescription))
                    }
                    CardinalActionCode.CANCEL -> {
                        threeDSecureCallback.onFailed(AirwallexError(message = "3DS canceled"))
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
