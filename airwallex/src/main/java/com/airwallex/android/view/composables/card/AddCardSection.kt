package com.airwallex.android.view.composables.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airwallex.android.R
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.ScreenView
import com.airwallex.android.ui.composables.StandardCheckBox
import com.airwallex.android.ui.composables.StandardSolidButton
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.composables.common.CountrySelectRow
import com.airwallex.android.view.composables.common.PaymentTextField
import com.airwallex.android.view.composables.common.WarningBanner
import com.airwallex.android.view.util.CountryUtils
import com.airwallex.risk.AirwallexRisk

@Suppress("ComplexMethod", "LongMethod")
@Composable
internal fun AddCardSection(
    viewModel: AddPaymentMethodViewModel,
    cardSchemes: List<CardScheme>,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val expiryFocusRequester = remember { FocusRequester() }
    val cvvFocusRequester = remember { FocusRequester() }
    val nameFocusRequest = remember { FocusRequester() }

    // Use ViewModel state flows for data retention across configuration changes
    val brand by viewModel.cardBrand.collectAsState()
    val isSaveCardChecked by viewModel.isSaveCardChecked.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()
    val expiryDate by viewModel.expiryDate.collectAsState()
    val cvv by viewModel.cvv.collectAsState()
    val cardHolderName by viewModel.cardHolderNameState.collectAsState()
    val email by viewModel.email.collectAsState()
    var cardNumberErrorMessage by remember { mutableStateOf<Int?>(null) }
    var expiryDateErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cvvErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cardHolderNameErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cardHolderEmailErrorMessage by remember { mutableStateOf<Int?>(null) }

    // Billing info section - Use ViewModel state flows
    val isSameAddressChecked by viewModel.isSameAddressChecked.collectAsState()
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()
    val street by viewModel.street.collectAsState()
    val state by viewModel.state.collectAsState()
    val city by viewModel.city.collectAsState()
    val zipCode by viewModel.zipCode.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    var streetErrorMessage by remember { mutableStateOf<Int?>(null) }
    var stateErrorMessage by remember { mutableStateOf<Int?>(null) }
    var cityErrorMessage by remember { mutableStateOf<Int?>(null) }

    ScreenView {
        viewModel.trackScreenViewed(
            PaymentMethodType.CARD.value,
            mapOf("subtype" to "card")
        )
    }

    LaunchedEffect(Unit) {
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
    }

    Column(
        modifier = Modifier
            .focusTarget()
            .clickable(
                interactionSource = null,
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
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
            cardSchemes = cardSchemes,
            initialValue = cardNumber,
            onValueChange = { value, cardBrand ->
                viewModel.updateCardNumber(value.text, cardBrand)
                cardNumberErrorMessage = null
            },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .zIndex(1f),
            onComplete = { input ->
                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(input)
                expiryFocusRequester.requestFocus()
            },
            onFocusLost = { input ->
                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(input)
            },
            isError = cardNumberErrorMessage != null,
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp,
            ),
        )

        Row {
            CardExpiryTextField(
                initialValue = expiryDate,
                onTextChanged = { value ->
                    viewModel.updateExpiryDate(value.text)
                    expiryDateErrorMessage = null
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
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 8.dp,
                ),
            )
            CardCvcTextField(
                cardBrand = brand,
                initialValue = cvv,
                onTextChanged = { value ->
                    viewModel.updateCvv(value.text)
                    cvvErrorMessage = null
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
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomEnd = 8.dp,
                    bottomStart = 0.dp,
                ),
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

        PaymentTextField(
            text = cardHolderName,
            onTextChanged = { value ->
                viewModel.updateCardHolderName(value)
                cardHolderNameErrorMessage = null
            },
            onComplete = { input ->
                cardHolderNameErrorMessage = viewModel.getCardHolderNameValidationMessage(input)
                if (!isSameAddressChecked || viewModel.isEmailRequired) {
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    focusManager.clearFocus()
                }
            },
            onFocusLost = { input ->
                cardHolderNameErrorMessage = viewModel.getCardHolderNameValidationMessage(input)
            },
            errorText = cardHolderNameErrorMessage?.let { stringResource(id = it) },
            modifier = Modifier
                .focusRequester(nameFocusRequest)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clickable(
                    onClick = {
                        AirwallexRisk.log(
                            event = "input_card_holder_name",
                            screen = "page_create_card"
                        )
                    },
                ),
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

            PaymentTextField(
                text = email,
                onTextChanged = { value ->
                    viewModel.updateEmail(value)
                    cardHolderEmailErrorMessage = null
                },
                onComplete = { input ->
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(input)
                    focusManager.clearFocus()
                },
                onFocusLost = { input ->
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(input)
                },
                errorText = cardHolderEmailErrorMessage?.let { stringResource(id = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .contentType(ContentType.EmailAddress)
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
                    onCheckedChange = {
                        AnalyticsLogger.logAction("toggle_billing_address")
                        viewModel.updateSameAddressChecked(it)
                        if (it) {
                            viewModel.updateSelectedCountryCode(viewModel.countryCode)
                            viewModel.updateStreet(viewModel.shipping?.address?.street.orEmpty())
                            viewModel.updateState(viewModel.shipping?.address?.state.orEmpty())
                            viewModel.updateCity(viewModel.shipping?.address?.city.orEmpty())
                            viewModel.updateZipCode(viewModel.shipping?.address?.postcode.orEmpty())
                            viewModel.updatePhoneNumber(viewModel.shipping?.phoneNumber.orEmpty())
                        }
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                CountrySelectRow(
                    options = CountryUtils.countryList.map { it.name to it.code },
                    default = selectedCountryCode,
                    onOptionSelected = {
                        viewModel.updateSelectedCountryCode(it.second)
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                    enabled = !isSameAddressChecked,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                    ),
                )

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_shipping_street_hint),
                    text = street,
                    onTextChanged = {
                        viewModel.updateStreet(it)
                        streetErrorMessage = null
                    },
                    onComplete = { input ->
                        streetErrorMessage = viewModel.getBillingValidationMessage(
                            input,
                            AddPaymentMethodViewModel.BillingFieldType.STREET
                        )
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    onFocusLost = { input ->
                        streetErrorMessage = viewModel.getBillingValidationMessage(
                            input,
                            AddPaymentMethodViewModel.BillingFieldType.STREET
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .zIndex(1f)
                        .contentType(ContentType.AddressStreet),
                    enabled = !isSameAddressChecked,
                    isError = streetErrorMessage != null,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                    ),
                )

                Row {
                    BillingTextField(
                        hint = stringResource(id = R.string.airwallex_shipping_state_name_hint),
                        text = state,
                        onTextChanged = {
                            viewModel.updateState(it)
                            stateErrorMessage = null
                        },
                        onComplete = { input ->
                            stateErrorMessage = viewModel.getBillingValidationMessage(
                                input,
                                AddPaymentMethodViewModel.BillingFieldType.STATE
                            )
                            focusManager.moveFocus(FocusDirection.Right)
                        },
                        onFocusLost = { input ->
                            stateErrorMessage = viewModel.getBillingValidationMessage(
                                input,
                                AddPaymentMethodViewModel.BillingFieldType.STATE
                            )
                        },
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .weight(1f)
                            .contentType(ContentType.AddressRegion),
                        enabled = !isSameAddressChecked,
                        isError = stateErrorMessage != null,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp,
                        ),
                    )
                    BillingTextField(
                        hint = stringResource(id = R.string.airwallex_shipping_city_name_hint),
                        text = city,
                        onTextChanged = {
                            viewModel.updateCity(it)
                            cityErrorMessage = null
                        },
                        onComplete = { input ->
                            cityErrorMessage = viewModel.getBillingValidationMessage(
                                input,
                                AddPaymentMethodViewModel.BillingFieldType.CITY
                            )
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        onFocusLost = { input ->
                            cityErrorMessage = viewModel.getBillingValidationMessage(
                                input,
                                AddPaymentMethodViewModel.BillingFieldType.CITY
                            )
                        },
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .weight(1f)
                            .contentType(ContentType.AddressLocality),
                        enabled = !isSameAddressChecked,
                        isError = cityErrorMessage != null,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp,
                        ),
                    )
                }

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_shipping_zip_code_hint),
                    text = zipCode,
                    onTextChanged = {
                        viewModel.updateZipCode(it)
                    },
                    onComplete = { input ->
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .contentType(ContentType.PostalCode),
                    enabled = !isSameAddressChecked,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp,
                    ),
                )

                BillingTextField(
                    hint = stringResource(id = R.string.airwallex_contact_phone_number_hint),
                    text = phoneNumber,
                    onTextChanged = {
                        viewModel.updatePhoneNumber(it)
                    },
                    onComplete = { input ->
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .contentType(ContentType.PhoneNumber),
                    enabled = !isSameAddressChecked,
                    options = StandardTextFieldOptions(
                        inputType = StandardTextFieldOptions.InputType.PHONE,
                        returnType = StandardTextFieldOptions.ReturnType.DONE,
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 8.dp,
                    ),
                )

                val billingErrorMessage =
                    streetErrorMessage ?: stateErrorMessage ?: cityErrorMessage
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
                onCheckedChange = {
                    viewModel.updateSaveCardChecked(it)
                    if (it) {
                        AnalyticsLogger.logAction("save_card")
                    }
                },
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(if (isSaveCardChecked) "card-saving-toggle-checked" else "card-saving-toggle-unchecked")
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
            text = stringResource(viewModel.ctaRes),
            onClick = {
                focusManager.clearFocus()

                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(cardNumber)
                expiryDateErrorMessage = viewModel.getExpiryValidationMessage(expiryDate)
                cvvErrorMessage = viewModel.getCvvValidationMessage(cvv, brand)
                cardHolderNameErrorMessage =
                    viewModel.getCardHolderNameValidationMessage(cardHolderName)
                if (viewModel.isEmailRequired) {
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(email)
                }
                if (viewModel.isBillingRequired) {
                    streetErrorMessage = viewModel.getBillingValidationMessage(
                        street,
                        AddPaymentMethodViewModel.BillingFieldType.STREET
                    )
                    stateErrorMessage = viewModel.getBillingValidationMessage(
                        state,
                        AddPaymentMethodViewModel.BillingFieldType.STATE
                    )
                    cityErrorMessage = viewModel.getBillingValidationMessage(
                        city,
                        AddPaymentMethodViewModel.BillingFieldType.CITY
                    )
                }

                val allValidated = listOfNotNull(
                    cardNumberErrorMessage,
                    expiryDateErrorMessage,
                    cvvErrorMessage,
                    cardHolderNameErrorMessage,
                    cardHolderEmailErrorMessage,
                    streetErrorMessage,
                    stateErrorMessage,
                    cityErrorMessage
                ).isEmpty()
                if (allValidated) {
                    // All fields are valid, so proceed to confirm payment.
                    val card = viewModel.createCard(cardNumber, cardHolderName, expiryDate, cvv)
                        ?: return@StandardSolidButton
                    val billing = viewModel.createBilling(
                        countryCode = selectedCountryCode,
                        state = state,
                        city = city,
                        street = street,
                        postcode = zipCode,
                        phoneNumber = phoneNumber,
                        email = email,
                    )
                    onOperationStart(PaymentOperation.AddCard)
                    viewModel.confirmPayment(
                        card = card,
                        saveCard = isSaveCardChecked,
                        billing = billing,
                        onResult = { status ->
                            onOperationDone(PaymentOperationResult.AddCard(status))
                        }
                    )
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