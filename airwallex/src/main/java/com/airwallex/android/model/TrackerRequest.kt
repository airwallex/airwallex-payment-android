package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class TrackerRequest internal constructor(

    /**
     * A unique string to identify a merchant, like website origin url or merchant account id
     */
    val origin: String? = null,

    /**
     * If the field is complete
     */
    val complete: Boolean? = null,

    /**
     * If the field is empty
     */
    val empty: Boolean? = null,

    /**
     * The application that add this log
     */
    val application: String? = null,

    /**
     * The element type
     */
    val type: TrackerType? = null,

    /**
     * The event type
     */
    val code: TrackerCode? = null,

    /**
     * The error that occurring
     */
    val error: String? = null,

    /**
     * The intent id you’re processing
     */
    val intentId: String? = null,

    /**
     * The status of the intent
     */
    val status: String? = null,

    /**
     * The next action type if have
     */
    val nextActionType: String? = null,

    /**
     * The next action url if have
     */
    val nextActionUrl: String? = null,

    /**
     * The brand of the card
     */
    val brand: String? = null,

    /**
     * The card bin of the card
     */
    val cardBin: String? = null,

    /**
     * The request path
     */
    val path: String? = null,

    /**
     * The request body
     */
    val req: @RawValue Map<String, Any?>? = null,

    /**
     * The request response
     */
    val res: @RawValue Map<String, Any?>? = null,

    /**
     * The request header
     */
    val header: @RawValue Map<String, Any?>? = null,

    ) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_ORIGIN = "origin"
        private const val FIELD_COMPLETE = "complete"
        private const val FIELD_EMPTY = "empty"
        private const val FIELD_APPLICATION = "application"
        private const val FIELD_TYPE = "type"
        private const val FIELD_CODE = "code"
        private const val FIELD_ERROR = "error"
        private const val FIELD_INTENT_ID = "intent_id"
        private const val FIELD_STATUS = "status"
        private const val FIELD_NEXT_ACTION_TYPE = "next_action_type"
        private const val FIELD_NEXT_ACTION_URL = "next_action_url"
        private const val FIELD_BRAND = "brand"
        private const val FIELD_CARD_BIN = "cardBin"
        private const val FIELD_PATH = "path"
        private const val FIELD_REQ = "req"
        private const val FIELD_RES = "res"
        private const val FIELD_HEADER = "header"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                origin?.let {
                    mapOf(FIELD_ORIGIN to it)
                }.orEmpty()
            )
            .plus(
                complete?.let {
                    mapOf(FIELD_COMPLETE to it)
                }.orEmpty()
            )
            .plus(
                empty?.let {
                    mapOf(FIELD_EMPTY to it)
                }.orEmpty()
            )
            .plus(
                mapOf(FIELD_APPLICATION to "android")
            )
            .plus(
                type?.let {
                    mapOf(FIELD_TYPE to it.value)
                }.orEmpty()
            )
            .plus(
                code?.let {
                    mapOf(FIELD_CODE to it.value)
                }.orEmpty()
            )
            .plus(
                error?.let {
                    mapOf(FIELD_ERROR to it)
                }.orEmpty()
            )
            .plus(
                intentId?.let {
                    mapOf(FIELD_INTENT_ID to it)
                }.orEmpty()
            )
            .plus(
                status?.let {
                    mapOf(FIELD_STATUS to it)
                }.orEmpty()
            )
            .plus(
                nextActionType?.let {
                    mapOf(FIELD_NEXT_ACTION_TYPE to it)
                }.orEmpty()
            )
            .plus(
                nextActionUrl?.let {
                    mapOf(FIELD_NEXT_ACTION_URL to it)
                }.orEmpty()
            )
            .plus(
                brand?.let {
                    mapOf(FIELD_BRAND to it)
                }.orEmpty()
            )
            .plus(
                cardBin?.let {
                    mapOf(FIELD_CARD_BIN to it)
                }.orEmpty()
            )
            .plus(
                path?.let {
                    mapOf(FIELD_PATH to it)
                }.orEmpty()
            )
            .plus(
                req?.let {
                    mapOf(FIELD_REQ to it)
                }.orEmpty()
            )
            .plus(
                res?.let {
                    mapOf(FIELD_RES to it)
                }.orEmpty()
            )
            .plus(
                header?.let {
                    mapOf(FIELD_HEADER to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<TrackerRequest> {
        private var origin: String? = null
        private var complete: Boolean? = null
        private var empty: Boolean? = null
        private var type: TrackerType? = null
        private var code: TrackerCode? = null
        private var error: String? = null
        private var intentId: String? = null
        private var status: String? = null
        private var nextActionType: String? = null
        private var nextActionUrl: String? = null
        private var brand: String? = null
        private var cardBin: String? = null
        private var path: String? = null
        private var req: @RawValue Map<String, Any?>? = null
        private var res: @RawValue Map<String, Any?>? = null
        private var header: @RawValue Map<String, Any?>? = null

        fun setOrigin(origin: String?): Builder = apply {
            this.origin = origin
        }

        fun setComplete(complete: Boolean?): Builder = apply {
            this.complete = complete
        }

        fun setEmpty(empty: Boolean?): Builder = apply {
            this.empty = empty
        }

        fun setType(type: TrackerType?): Builder = apply {
            this.type = type
        }

        fun setCode(code: TrackerCode?): Builder = apply {
            this.code = code
        }

        fun setError(error: String?): Builder = apply {
            this.error = error
        }

        fun setIntentId(intentId: String?): Builder = apply {
            this.intentId = intentId
        }

        fun setStatus(status: String?): Builder = apply {
            this.status = status
        }

        fun setNextActionType(nextActionType: String?): Builder = apply {
            this.nextActionType = nextActionType
        }

        fun setNextActionUrl(nextActionUrl: String?): Builder = apply {
            this.nextActionUrl = nextActionUrl
        }

        fun setBrand(brand: String?): Builder = apply {
            this.brand = brand
        }

        fun setCardBin(cardBin: String?): Builder = apply {
            this.cardBin = cardBin
        }

        fun setPath(path: String?): Builder = apply {
            this.path = path
        }

        fun setReq(req: @RawValue Map<String, Any>?): Builder = apply {
            this.req = req
        }

        fun setRes(res: @RawValue Map<String, Any>?): Builder = apply {
            this.res = res
        }

        fun setHeader(header: @RawValue Map<String, Any>?): Builder = apply {
            this.header = header
        }

        override fun build(): TrackerRequest {
            return TrackerRequest(
                origin = origin,
                complete = complete,
                empty = empty,
                type = type,
                code = code,
                error = error,
                intentId = intentId,
                status = status,
                nextActionType = nextActionType,
                nextActionUrl = nextActionUrl,
                brand = brand,
                cardBin = cardBin,
                path = path,
                req = req,
                res = res,
                header = header
            )
        }
    }

    @Parcelize
    enum class TrackerType(val value: String) : Parcelable {
        CARD_NUMBER("cardNumber"),

        EXPIRY("expiry"),

        CVC("cvc"),

        PAYMENT_REQUEST_BUTTON("paymentRequestButton"),

        CARD("card"),

        WECHAT("wechat"),

        QRCODE("qrcode"),

        REDIRECT("redirect"),

        DROP_IN("dropIn"),

        FULL_FEATURED_CARD("fullFeaturedCard"),

        HPP("hpp"),

        REDIRECT_PAGE("redirectPage");
    }

    @Parcelize
    enum class TrackerCode(val value: String) : Parcelable {
        ON_READY("onReady"),

        ON_SUBMIT("onSubmit"),

        ON_DYNAMIC_CURRENCY_CONVERSION("onDynamicCurrencyConversion"),

        ON_SUCCESS("onSuccess"),

        ON_ERROR("onError"),

        ON_CANCEL("onCancel"),

        ON_FOCUS("onFocus"),

        ON_BLUR("onBlur"),

        ON_CHANGE("onChange"),

        ON_CLICK("onClick"),

        ON_RESIZE("onResize"),

        ON_REDIRECT("onRedirect"),

        ON_COMPLETE("onComplete"),

        ON_CHALLENGE("onChallenge"),

        ON_CHALLENGE_SUCCESS("onChallengeSuccess"),

        ON_CHALLENGE_ERROR("onChallengeError"),

        ON_INTENT_RETRIEVED("onIntentRetrieved"),

        ON_INTENT_RETRIEVED_ERROR("onIntentRetrievedError"),

        ON_PAYMENT_METHOD_CREATED("onPaymentMethodCreated"),

        ON_PAYMENT_METHOD_CREATED_ERROR("onPaymentMethodCreatedError"),

        ON_LOG_REQUEST("onLogRequest"),

        ON_LOG_RESPONSE("onLogResponse"),

        ON_SWITCH_METHOD("onSwitchMethod"),

        ON_CLICK_CONFIRM_BUTTON("onClickConfirmButton");
    }
}
