package com.airwallex.paymentacceptance.repo

import android.text.TextUtils
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PurchaseOrder
import com.airwallex.android.core.model.parser.ClientSecretParser
import com.airwallex.android.core.model.parser.PaymentIntentParser
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.api.Api
import com.airwallex.paymentacceptance.api.ApiFactory
import com.airwallex.paymentacceptance.products
import com.airwallex.paymentacceptance.shipping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

class LocalMockRepository: BaseRepository {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

    override suspend fun login() {
        val response = api.authentication(Settings.apiKey, Settings.clientId)
        Settings.token = JSONObject(response.string())["token"].toString()
    }

    override suspend fun getPaymentIntentFromServer(
        force3DS: Boolean,
        customerId: String?
    ): PaymentIntent {
        if (customerId == null) {
            login()
        }
        return withContext(Dispatchers.IO) {
            val body = mutableMapOf(
                "request_id" to UUID.randomUUID().toString(),
                "amount" to Settings.price.toDouble(),
                "currency" to Settings.currency,
                "merchant_order_id" to UUID.randomUUID().toString(),
                "order" to PurchaseOrder.Builder()
                    .setProducts(products)
                    .setShipping(shipping)
                    .setType("physical_goods")
                    .build()
                    .toParamMap(),
                "descriptor" to "Airwallex - T-sh  irt",
                "metadata" to mapOf("id" to 1),
                "email" to "yimadangxian@airwallex.com",
                "return_url" to Settings.returnUrl
            )
            if (force3DS) {
                body["payment_method_options"] =
                    mapOf("card" to mapOf("three_ds_action" to "FORCE_3DS"))
            }
            Settings.cachedCustomerId?.let { body.put("customer_id", it) }
            customerId?.let { body.put("customer_id", it) }
            val paymentIntentResponse = api.createPaymentIntent(body)
            PaymentIntentParser().parse(JSONObject(paymentIntentResponse.string()))
        }
    }

    override suspend fun getCustomerIdFromServer(): String {
        return withContext(Dispatchers.IO) {
            login()
            Settings.cachedCustomerId.takeIf { !it.isNullOrEmpty() } ?: run {
                val customerResponse = api.createCustomer(
                    mutableMapOf(
                        "request_id" to UUID.randomUUID().toString(),
                        "merchant_customer_id" to UUID.randomUUID().toString(),
                        "first_name" to "John",
                        "last_name" to "Doe",
                        "email" to "john.doe@airwallex.com",
                        "phone_number" to "13800000000",
                        "additional_info" to mapOf(
                            "registered_via_social_media" to false,
                            "registration_date" to "2019-09-18",
                            "first_successful_order_date" to "2019-09-18"
                        ),
                        "metadata" to mapOf(
                            "id" to 1
                        )
                    )
                )
                val customerId = JSONObject(customerResponse.string())["id"].toString()
                Settings.cachedCustomerId = customerId
                customerId
            }
        }
    }

    override suspend fun getClientSecretFromServer(customerId: String): String {
        return withContext(Dispatchers.IO) {
            val clientSecretResponse = api.createClientSecret(customerId)
            ClientSecretParser().parse(JSONObject(clientSecretResponse.string())).value
        }
    }
}