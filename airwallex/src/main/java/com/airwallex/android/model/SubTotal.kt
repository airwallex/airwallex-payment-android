package com.airwallex.android.model

import android.os.Parcelable
import android.util.Log
import com.airwallex.android.model.parser.ShippingParser
import com.airwallex.android.model.parser.SubTotalParser
import kotlinx.parcelize.Parcelize

/**
 * SubTotal information
 */
@Parcelize
data class SubTotal internal constructor(

    /**
     * First Price
     */
    val firstPrice: String? = null,

    /**
     * Second Price
     */
    val secondPrice: String? = null,

    /**
     * First Count
     */
    val firstCount:String?=null,

    /**
     * Second Count
     */
    val secondCount:String?=null,

    val total: Double?=0.00,


) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                firstPrice?.let {
                    mapOf(SubTotalParser.FIELD_FIRST_PRICE to it)
                }.orEmpty()
            )
            .plus(
                secondPrice?.let {
                    mapOf(SubTotalParser.FIELD_SECOND_PRICE to it)
                }.orEmpty()
            )
            .plus(
                firstCount?.let {
                    mapOf(SubTotalParser.FIELD_FIRST_COUNT to it)
                }.orEmpty()
            )
            .plus(
                secondCount?.let {
                    mapOf(SubTotalParser.FIELD_SECOND_PRICE to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<SubTotal> {
        private var firstPrice: String? = null
        private var secondPrice: String? = null
        private var firstCount: String? = null
        private var secondCount: String? = null

        fun setFirstPrice(firstPrice: String?): Builder = apply {
            this.firstPrice = firstPrice
        }

        fun setSecondPrice(secondPrice: String?): Builder = apply {
            this.secondPrice = secondPrice
        }

        fun setFirstCount(firstCount: String?): Builder = apply {
            this.firstCount = firstCount
        }

        fun setSecondCount(secondCount: String?): Builder = apply {
            this.secondCount = secondCount
        }


        override fun build(): SubTotal {
            return SubTotal(
                firstPrice = firstPrice,
                secondPrice = secondPrice,
                firstCount = firstCount,
                secondCount = secondCount,
            )
        }

    }

    fun calculate(): Int? {
        return (secondCount?.toInt())?.times((secondPrice?.toInt()!!))?.let {
            (firstCount?.toInt())?.times((firstPrice?.toInt()!!))?.plus(
                it
            )
        }
    }

}
