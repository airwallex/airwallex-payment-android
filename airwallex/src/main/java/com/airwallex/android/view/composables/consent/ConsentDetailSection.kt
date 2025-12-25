package com.airwallex.android.view.composables.consent

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.CardBrand
import com.airwallex.android.ui.composables.ScreenView
import com.airwallex.android.ui.composables.StandardSolidButton
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.composables.card.CardCvcTextField

@Suppress("LongParameterList")
@Composable
internal fun ConsentDetailSection(
    viewModel: AddPaymentMethodViewModel,
    isCvcRequired: Boolean,
    cardBrand: CardBrand,
    onCheckoutWithCvc: (String) -> Unit,
    onCheckoutWithoutCvv: () -> Unit,
    onScreenViewed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    var cvv by remember { mutableStateOf("") }
    var cvvErrorMessage by remember { mutableStateOf<Int?>(null) }

    ScreenView { onScreenViewed() }

    if (isCvcRequired) {
        CardCvcTextField(
            cardBrand = cardBrand,
            onTextChanged = { value ->
                cvv = value.text
                cvvErrorMessage = null
            },
            onComplete = { input ->
                cvvErrorMessage = viewModel.getCvvValidationMessage(input, cardBrand)
                focusManager.clearFocus()
            },
            onFocusLost = { input ->
                cvvErrorMessage = viewModel.getCvvValidationMessage(input, cardBrand)
            },
            isError = cvvErrorMessage != null,
            errorMessage = cvvErrorMessage?.let { stringResource(it) },
            modifier = modifier,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    StandardSolidButton(
        text = stringResource(viewModel.ctaRes),
        onClick = {
            if (isCvcRequired) {
                cvvErrorMessage = viewModel.getCvvValidationMessage(cvv, cardBrand)
                if (cvvErrorMessage == null) {
                    onCheckoutWithCvc(cvv)
                }
            } else {
                onCheckoutWithoutCvv()
            }
        },
        modifier = modifier,
    )

    Spacer(modifier = Modifier.height(32.dp))
}