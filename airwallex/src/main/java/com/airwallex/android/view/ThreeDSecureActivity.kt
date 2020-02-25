package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver

class ThreeDSecureActivity : AppCompatActivity(), CardinalValidateReceiver {

    companion object {
        const val EXTRA_THREE_D_SECURE_LOOKUP = "EXTRA_THREE_D_SECURE_LOOKUP"
        const val EXTRA_VALIDATION_RESPONSE = "EXTRA_VALIDATION_RESPONSE"
        const val EXTRA_JWT = "EXTRA_JWT"
        const val THREE_D_SECURE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var extras = intent.extras
        if (extras == null) {
            extras = Bundle()
        }
        val threeDSecureLookup: ThreeDSecureLookup =
            extras.getParcelable(EXTRA_THREE_D_SECURE_LOOKUP) ?: return
        Cardinal.getInstance().cca_continue(
            threeDSecureLookup.transactionId,
            threeDSecureLookup.payload,
            this,
            this
        )
    }

    override fun onValidated(
        context: Context?,
        validateResponse: ValidateResponse?,
        jwt: String?
    ) {
        val result = Intent()
        result.putExtra(EXTRA_JWT, jwt)
        result.putExtra(
            EXTRA_THREE_D_SECURE_LOOKUP,
            intent.extras?.getParcelable(EXTRA_THREE_D_SECURE_LOOKUP) as? ThreeDSecureLookup
        )
        result.putExtra(EXTRA_VALIDATION_RESPONSE, validateResponse)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}