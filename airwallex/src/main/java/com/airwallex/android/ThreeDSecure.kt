package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
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

    const val THREE_DS_RETURN_URL = "http://requestbin.net/r/1il2qkm1"

    private fun configureCardinal(applicationContext: Context) {
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
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

    internal fun performVerification(
        applicationContext: Context,
        serverJwt: String,
        onSetupCompleted: (consumerSessionId: String?, validateResponse: ValidateResponse?) -> Unit
    ) {
        configureCardinal(applicationContext)
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

    internal fun performCardinalAuthentication(
        activity: Activity,
        threeDSecureLookup: ThreeDSecureLookup
    ) {
        val extras = Bundle()
        extras.putParcelable(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_LOOKUP, threeDSecureLookup)

        val intent = Intent(activity, ThreeDSecureActivity::class.java)
        intent.putExtras(extras)

        activity.startActivityForResult(intent, ThreeDSecureActivity.THREE_D_SECURE)
    }

    internal fun onActivityResult(
        data: Intent,
        completion: (validateResponse: ValidateResponse, exception: AirwallexException?) -> Unit
    ) {
        val resultUri = data.data
        if (resultUri != null) {
            // 1.0 Flow
        } else {
            // 2.0 Flow
            val validateResponse =
                data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse
            val jwt = data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_D_JWT)

            Logger.debug("3DS actionCode " + validateResponse.actionCode)
            when (validateResponse.actionCode!!) {
                CardinalActionCode.FAILURE, CardinalActionCode.SUCCESS, CardinalActionCode.NOACTION -> {
                    completion.invoke(validateResponse, null)
                }
                CardinalActionCode.ERROR, CardinalActionCode.TIMEOUT -> {
                    completion.invoke(
                        validateResponse,
                        ThreeDSException(AirwallexError(message = validateResponse.errorDescription))
                    )
                }
                CardinalActionCode.CANCEL -> {
                    completion.invoke(
                        validateResponse,
                        ThreeDSException(AirwallexError(message = validateResponse.errorDescription))
                    )
                }
            }
        }
    }
}
