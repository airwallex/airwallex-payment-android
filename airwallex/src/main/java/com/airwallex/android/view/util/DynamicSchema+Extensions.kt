package com.airwallex.android.view.util

import com.airwallex.android.core.model.DynamicSchemaFieldUIType
import com.airwallex.android.core.model.DynamicSchemaFieldValidation

fun String.isValidDynamicSchemaField(validations: DynamicSchemaFieldValidation?, uiType: DynamicSchemaFieldUIType?): Boolean {
    return if (validations == null) {
        when (uiType) {
            DynamicSchemaFieldUIType.EMAIL -> isNotBlank() && isValidEmail()
            else -> isNotBlank()
        }
    } else {
        (isNotBlank() && (!(validations.regex?.let { Regex(it).matches(this) } == true || validations.max?.let { this.length > it } == true)))
    }
}