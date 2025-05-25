package com.airwallex.android.view.util

import com.airwallex.android.core.model.DynamicSchemaFieldValidation

fun String.isValidDynamicSchemaField(validations: DynamicSchemaFieldValidation?) = isNotBlank()
        && (validations == null || !(validations.regex?.let { Regex(it).matches(this) } == true || validations.max?.let { this.length > it } == true))