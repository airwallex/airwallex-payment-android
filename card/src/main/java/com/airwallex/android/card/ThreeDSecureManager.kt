package com.airwallex.android.card

import android.content.Context
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.Environment
import com.airwallex.android.core.log.Logger
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import org.json.JSONArray

object ThreeDSecureManager {

    private const val TAG = "ThreeDSecure"

    /**
     * Configure Cardinal Mobile SDK
     */
    private fun configureCardinal(applicationContext: Context) {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        cardinalConfigurationParameters.environment =
            if (AirwallexPlugins.environment == Environment.PRODUCTION) CardinalEnvironment.PRODUCTION else CardinalEnvironment.STAGING
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

    fun performCardinalInitialize(
        applicationContext: Context,
        serverJwt: String,
        completion: (consumerSessionId: String?, validateResponse: ValidateResponse?) -> Unit
    ) {
        configureCardinal(applicationContext)
        Logger.debug(TAG, "Start initialize ")
        // Setup the Initial Call to Cardinal
        Cardinal.getInstance().init(
            serverJwt,
            object : CardinalInitService {

                /**
                 * You may have your Submit button disabled on page load. Once you are set up
                 * for CCA, you may then enable it. This will prevent users from submitting
                 * their order before CCA is ready.
                 */
                override fun onSetupCompleted(consumerSessionId: String) {
                    Logger.debug(TAG, "onSetupCompleted $consumerSessionId")
                    completion.invoke(consumerSessionId, null)
                }

                /**
                 * If there was an error with setup, cardinal will call this function with
                 * validate response and empty serverJWT
                 * @param validateResponse
                 * @param serverJWT will be an empty
                 */
                override fun onValidated(validateResponse: ValidateResponse, serverJWT: String?) {
                    Logger.debug(TAG, "onValidated")
                    completion.invoke(null, validateResponse)
                }
            }
        )
    }

    enum class ThreeDSecureType {
        THREE_D_SECURE_1,
        THREE_D_SECURE_2
    }
}
