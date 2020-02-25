package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ThreeDSecureLookup
import com.airwallex.android.view.ThreeDSecureActivity
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

object ThreeDSecure {

    fun onActivityResult(
        resultCode: Int,
        data: Intent
    ) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val threeDSecureLookup: ThreeDSecureLookup? =
            data.getParcelableExtra(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_LOOKUP)
        val validateResponse =
            data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse
        val jwt = data.getStringExtra(ThreeDSecureActivity.EXTRA_JWT)

        when (validateResponse.getActionCode()!!) {
            CardinalActionCode.FAILURE, CardinalActionCode.SUCCESS, CardinalActionCode.NOACTION -> {
                // SUCCESS
                authenticateCardinalJWT(threeDSecureLookup, jwt)
            }
            CardinalActionCode.ERROR, CardinalActionCode.TIMEOUT -> {
                // failed
                // validateResponse.getErrorDescription()
            }
            CardinalActionCode.CANCEL -> {
                // canceled
            }
        }
    }

    private fun authenticateCardinalJWT(threeDSecureLookup: ThreeDSecureLookup?, jwt: String?) {
        // TODO
    }

    fun performVerification(activity: Activity, serverJwt: String) {
        val lookupListener: ThreeDSecureLookupListener = object : ThreeDSecureLookupListener {

            override fun onLookupComplete(
                threeDSecureLookup: ThreeDSecureLookup
            ) {
                performCardinalAuthentication(activity, threeDSecureLookup)
            }
        }
        performVerification(activity.applicationContext, serverJwt, lookupListener)
    }

    private fun performVerification(
        applicationContext: Context,
        serverJwt: String,
        lookupListener: ThreeDSecureLookupListener
    ) {
        configureCardinal(applicationContext)
        Cardinal.getInstance().init(serverJwt, object : CardinalInitService {

            /**
             * You may have your Submit button disabled on page load. Once you are set up
             * for CCA, you may then enable it. This will prevent users from submitting
             * their order before CCA is ready.
             */
            override fun onSetupCompleted(consumerSessionId: String) {
                performThreeDSecureLookup(consumerSessionId, lookupListener)
            }

            /**
             * If there was an error with setup, cardinal will call this function with
             * validate response and empty serverJWT
             * @param validateResponse
             * @param serverJwt will be an empty
             */
            override fun onValidated(validateResponse: ValidateResponse, serverJWT: String) {
                // TODO
            }
        })
    }

    private fun performThreeDSecureLookup(
        consumerSessionId: String,
        lookupListener: ThreeDSecureLookupListener
    ) {
        // TODO Request merchant's server
        lookupListener.onLookupComplete(ThreeDSecureLookup())
    }

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

    private fun performCardinalAuthentication(
        activity: Activity,
        threeDSecureLookup: ThreeDSecureLookup
    ) {
        val extras = Bundle()
        extras.putParcelable(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_LOOKUP, threeDSecureLookup)

        val intent =
            Intent(activity.applicationContext, ThreeDSecureActivity::class.java)
        intent.putExtras(extras)

        activity.startActivityForResult(intent, ThreeDSecureActivity.THREE_D_SECURE)
    }
}