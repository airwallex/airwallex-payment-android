package com.airwallex.android.view.composables.addcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardCheckBox
import com.airwallex.android.ui.composables.StandardSolidButton
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.composables.common.CountrySelectRow
import com.airwallex.android.view.composables.common.WarningBanner
import com.airwallex.android.view.util.CountryUtils
import com.airwallex.risk.AirwallexRisk

@Suppress("ComplexMethod", "LongMethod")
@Composable
internal fun AddCardSection(
    viewModel: AddPaymentMethodViewModel,
    type: AvailablePaymentMethodType,
    onConfirm: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val expiryFocusRequester = remember { FocusRequester() }
    val cvvFocusRequester = remember { FocusRequester() }
    val nameFocusRequest = remember { FocusRequester() }

    var brand by remember { mutableStateOf(CardBrand.Unknown) }
    var isSaveCardChecked by remember { mutableStateOf(viewModel.canSaveCard) }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf(viewModel.cardHolderName) }
    var email by remember { mutableStateOf(viewModel.shipping?.email.orEmpty()) }
    var cardNumberErrorMessage by remember { mutableStateOf<Int?>(null) }
    var expiryDateErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cvvErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cardHolderNameErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cardHolderEmailErrorMessage by remember { mutableStateOf<Int?>(null) }

    // Billing info section
    var isSameAddressChecked by remember { mutableStateOf(viewModel.shipping != null) }
    var selectedCountryCode by remember { mutableStateOf(viewModel.countryCode) }
    var street by remember { mutableStateOf(viewModel.shipping?.address?.street.orEmpty()) }
    var state by remember { mutableStateOf(viewModel.shipping?.address?.state.orEmpty()) }
    var city by remember { mutableStateOf(viewModel.shipping?.address?.city.orEmpty()) }
    var zipCode by remember { mutableStateOf(viewModel.shipping?.address?.postcode.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(viewModel.shipping?.phoneNumber.orEmpty()) }
    var streetErrorMessage by remember { mutableStateOf<Int?>(null) }
    var stateErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cityErrorMessage by remember { mutableStateOf<Int?>(null) }
    var zipCodeErrorMessage by remember { mutableStateOf<Int?>(null) }
    var phoneNumberErrorMessage by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
    }

    Column {
        StandardText(
            text = stringResource(R.string.airwallex_card_information_title),
            textAlign = TextAlign.Left,
            typography = AirwallexTypography.Body200,
            color = AirwallexColor.TextPrimary,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 12.dp,
            ),
        )

        CardNumberTextField(
            type = type,
            onValueChange = { value, cardBrand ->
                cardNumber = value.text
                brand = cardBrand
            },
            modifier = Modifier
                .padding(horizontal = 24.dp),
            onComplete = { input ->
                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(input)
                expiryFocusRequester.requestFocus()
            },
            onFocusLost = { input ->
                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(input)
            },
            isError = cardNumberErrorMessage != null,
        )

        Row {
            CardExpiryTextField(
                onTextChanged = { value ->
                    expiryDate = value.text
                },
                onComplete = { input ->
                    expiryDateErrorMessage = viewModel.getExpiryValidationMessage(input)
                    cvvFocusRequester.requestFocus()
                },
                modifier = Modifier
                    .focusRequester(expiryFocusRequester)
                    .padding(start = 24.dp)
                    .weight(1f),
                onFocusLost = { input ->
                    expiryDateErrorMessage = viewModel.getExpiryValidationMessage(input)
                },
                isError = expiryDateErrorMessage != null,
            )
            CardCvcTextField(
                cardBrand = brand,
                onTextChanged = { value ->
                    cvv = value.text
                },
                onComplete = { input ->
                    cvvErrorMessage = viewModel.getCvvValidationMessage(input, brand)
                    nameFocusRequest.requestFocus()
                },
                onFocusLost = { input ->
                    cvvErrorMessage = viewModel.getCvvValidationMessage(input, brand)
                },
                modifier = Modifier
                    .focusRequester(cvvFocusRequester)
                    .padding(end = 24.dp)
                    .weight(1f),
                isError = cvvErrorMessage != null,
            )
        }

        val errorMessage = cardNumberErrorMessage ?: expiryDateErrorMessage ?: cvvErrorMessage
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))

            StandardText(
                text = stringResource(id = errorMessage),
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Caption100,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 40.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StandardText(
            text = stringResource(R.string.airwallex_card_name_hint),
            textAlign = TextAlign.Left,
            typography = AirwallexTypography.Body200,
            color = AirwallexColor.TextPrimary,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 12.dp,
            ),
        )

        AddCardTextField(
            text = cardHolderName,
            onTextChanged = { value ->
                cardHolderName = value.text
            },
            onComplete = { input ->
                cardHolderNameErrorMessage = viewModel.getCardHolderNameValidationMessage(input)
                if (!isSameAddressChecked || viewModel.isEmailRequired) {
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    focusManager.clearFocus()
                }
            },
            errorText = cardHolderNameErrorMessage?.let { stringResource(id = it) },
            modifier = Modifier
                .focusRequester(nameFocusRequest)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )

        if (viewModel.isEmailRequired) {
            Spacer(modifier = Modifier.height(12.dp))

            StandardText(
                text = stringResource(R.string.airwallex_email_hint),
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Body200,
                color = AirwallexColor.TextPrimary,
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 12.dp,
                ),
            )

            AddCardTextField(
                text = email,
                onTextChanged = { value ->
                    email = value.text
                },
                onComplete = { input ->
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(input)
                    focusManager.clearFocus()
                },
                errorText = cardHolderEmailErrorMessage?.let { stringResource(id = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
        }

        if (viewModel.isBillingRequired) {
            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Spacer(modifier = Modifier.height(12.dp))

                StandardText(
                    text = stringResource(R.string.airwallex_billing_info),
                    textAlign = TextAlign.Left,
                    typography = AirwallexTypography.Body200,
                    color = AirwallexColor.TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                StandardCheckBox(
                    checked = isSameAddressChecked,
                    text = stringResource(id = R.string.airwallex_same_as_shipping),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    onCheckedChange = {
                        AnalyticsLogger.logAction("toggle_billing_address")
                        isSameAddressChecked = it
                        selectedCountryCode = viewModel.countryCode
                        street = viewModel.shipping?.address?.street.orEmpty()
                        state = viewModel.shipping?.address?.state.orEmpty()
                        city = viewModel.shipping?.address?.city.orEmpty()
                        zipCode = viewModel.shipping?.address?.postcode.orEmpty()
                        phoneNumber = viewModel.shipping?.phoneNumber.orEmpty()
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                CountrySelectRow(
                    options = CountryUtils.countryList.map { it.name to it.code },
                    label = stringResource(id = R.string.airwallex_shipping_country_name_hint),
                    default = selectedCountryCode,
                    onOptionSelected = {
                        selectedCountryCode = it.second
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                    enabled = !isSameAddressChecked,
                )

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_shipping_street_hint),
                    text = street,
                    onTextChanged = {
                        street = it.text
                    },
                    onComplete = { input ->
                        streetErrorMessage = viewModel.getBillingValidationMessage(input, AddPaymentMethodViewModel.BillingFieldType.STREET)
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                    enabled = !isSameAddressChecked,
                    isError = streetErrorMessage != null,
                )

                Row {
                    BillingTextField(
                        hint = stringResource(id = R.string.airwallex_shipping_state_name_hint),
                        text = state,
                        onTextChanged = {
                            state = it.text
                        },
                        onComplete = { input ->
                            stateErrorMessage = viewModel.getBillingValidationMessage(input, AddPaymentMethodViewModel.BillingFieldType.STATE)
                            focusManager.moveFocus(FocusDirection.Right)
                        },
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .weight(1f),
                        enabled = !isSameAddressChecked,
                        isError = stateErrorMessage != null,
                    )
                    BillingTextField(
                        hint = stringResource(id = R.string.airwallex_shipping_city_name_hint),
                        text = city,
                        onTextChanged = {
                            city = it.text
                        },
                        onComplete = { input ->
                            cityErrorMessage = viewModel.getBillingValidationMessage(input, AddPaymentMethodViewModel.BillingFieldType.CITY)
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .weight(1f),
                        enabled = !isSameAddressChecked,
                        isError = cityErrorMessage != null,
                    )
                }

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_shipping_zip_code_hint),
                    text = zipCode,
                    onTextChanged = {
                        zipCode = it.text
                    },
                    onComplete = { input ->
                        zipCodeErrorMessage = viewModel.getBillingValidationMessage(input, AddPaymentMethodViewModel.BillingFieldType.POSTAL_CODE)
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                    enabled = !isSameAddressChecked,
                    isError = zipCodeErrorMessage != null,
                )

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_contact_phone_number_hint),
                    text = phoneNumber,
                    onTextChanged = {
                        phoneNumber = it.text
                    },
                    onComplete = { input ->
                        phoneNumberErrorMessage = viewModel.getBillingValidationMessage(input, AddPaymentMethodViewModel.BillingFieldType.PONE_NUMBER)
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                    enabled = !isSameAddressChecked,
                    isError = phoneNumberErrorMessage != null,
                )

                val billingErrorMessage = streetErrorMessage ?: stateErrorMessage ?: cityErrorMessage ?: zipCodeErrorMessage ?: phoneNumberErrorMessage
                if (billingErrorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    StandardText(
                        text = stringResource(id = billingErrorMessage),
                        textAlign = TextAlign.Left,
                        typography = AirwallexTypography.Caption100,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 40.dp),
                    )
                }
            }
        }

        if (viewModel.canSaveCard) {
            Spacer(modifier = Modifier.height(8.dp))

            StandardCheckBox(
                checked = isSaveCardChecked,
                text = stringResource(id = R.string.airwallex_save_card),
                modifier = Modifier.padding(horizontal = 24.dp),
                onCheckedChange = {
                    isSaveCardChecked = it
                    if (isSaveCardChecked) {
                        AnalyticsLogger.logAction("save_card")
                    }
                },
            )

            AnimatedVisibility(
                visible = isSaveCardChecked && brand == CardBrand.UnionPay,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                WarningBanner(message = stringResource(id = R.string.airwallex_save_union_pay_card))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        StandardSolidButton(
            text = viewModel.ctaTitle,
            onClick = {
                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(cardNumber)
                expiryDateErrorMessage = viewModel.getExpiryValidationMessage(expiryDate)
                cvvErrorMessage = viewModel.getCvvValidationMessage(cvv, brand)
                cardHolderNameErrorMessage = viewModel.getCardHolderNameValidationMessage(cardHolderName)
                if (viewModel.isEmailRequired) {
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(email)
                }
                if (viewModel.isBillingRequired) {
                    streetErrorMessage = viewModel.getBillingValidationMessage(street, AddPaymentMethodViewModel.BillingFieldType.STREET)
                    stateErrorMessage = viewModel.getBillingValidationMessage(state, AddPaymentMethodViewModel.BillingFieldType.STATE)
                    cityErrorMessage = viewModel.getBillingValidationMessage(city, AddPaymentMethodViewModel.BillingFieldType.CITY)
                    zipCodeErrorMessage = viewModel.getBillingValidationMessage(zipCode, AddPaymentMethodViewModel.BillingFieldType.POSTAL_CODE)
                    phoneNumberErrorMessage = viewModel.getBillingValidationMessage(phoneNumber, AddPaymentMethodViewModel.BillingFieldType.PONE_NUMBER)
                }

                val allValidated = listOfNotNull(
                    cardNumberErrorMessage,
                    expiryDateErrorMessage,
                    cvvErrorMessage,
                    cardHolderNameErrorMessage,
                    cardHolderEmailErrorMessage,
                    streetErrorMessage,
                    stateErrorMessage,
                    cityErrorMessage,
                    zipCodeErrorMessage,
                    phoneNumberErrorMessage,
                ).isEmpty()
                if (allValidated) {
                    // All fields are valid, so proceed to confirm payment.
                    val card = viewModel.createCard(cardNumber, cardHolderName, expiryDate, cvv) ?: return@StandardSolidButton
                    viewModel.confirmPayment(
                        card = card,
                        saveCard = isSaveCardChecked,
                        billing = viewModel.createBillingWithShipping(
                            countryCode = selectedCountryCode,
                            state = state,
                            city = city,
                            street = street,
                            postcode = zipCode,
                            phoneNumber = phoneNumber,
                            email = email,
                        ),
                    )
                    onConfirm()
                }
                // Otherwise do nothing
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}