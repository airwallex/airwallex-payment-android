package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicSchemaField(

    /**
     * Name of schema field
     */
    val name: String,

    /**
     * Display name of schema field
     */
    val displayName: String,

    /**
     * UI type of schema field, include text, email, phone, list, logo_list
     */
    val uiType: DynamicSchemaFieldUIType?,

    /**
     * The type of schema field
     */
    val type: DynamicSchemaFieldType?,

    /**
     * If need to hide
     */
    val hidden: Boolean,

    /**
     * Validations of schema field
     */
    val candidates: List<DynamicSchemaFieldCandidate>?,

    /**
     * Validations of schema field
     */
    val validations: DynamicSchemaFieldValidation?
) : AirwallexModel, Parcelable
