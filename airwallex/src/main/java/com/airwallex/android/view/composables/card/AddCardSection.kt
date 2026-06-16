package com.airwallex.android.view.composables.card

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
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
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
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
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.common.CountrySelectRow
import com.airwallex.android.view.composables.common.PaymentTextField
import com.airwallex.android.view.composables.common.StateSelectRow
import com.airwallex.android.view.composables.common.WarningBanner
import com.airwallex.android.core.util.AddressSpec
import com.airwallex.android.view.util.AnalyticsConstants.CLICK_PAY_BUTTON
import com.airwallex.android.view.util.AnalyticsConstants.PAGE_CREATE_CARD
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import com.airwallex.android.view.util.AnalyticsConstants.TAP_PAY_BUTTON
import com.airwallex.android.view.util.BillingAddressLabels
import com.airwallex.android.view.util.CountryUtils
import com.airwallex.risk.AirwallexRisk

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun AddCardSection(
    viewModel: AddPaymentMethodViewModel,
    paymentFlowViewModel: PaymentFlowViewModel,
    cardSchemes: List<CardScheme>,
    paymentFlowListener: PaymentFlowListener,
    activity: ComponentActivity,
    checkoutButtonTitle: String? = null,
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
    var postcodeErrorMessage by remember { mutableStateOf<Int?>(null) }
    var phoneErrorMessage by remember { mutableStateOf<Int?>(null) }
    var countryCodeErrorMessage by remember { mutableStateOf<Int?>(null) }

    ScreenView {
        viewModel.trackScreenViewed(
            PaymentMethodType.CARD.value,
            mapOf(Field.SUBTYPE to "card")
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
            color = AirwallexColor.textPrimary,
            modifier = Modifier.padding(
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
                color = AirwallexColor.textError,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        if (viewModel.showName) {
            Spacer(modifier = Modifier.height(12.dp))

            StandardText(
                text = stringResource(R.string.airwallex_card_name_hint),
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Body200,
                color = AirwallexColor.textPrimary,
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            PaymentTextField(
                text = cardHolderName,
                onTextChanged = { value ->
                    viewModel.updateCardHolderName(value)
                    cardHolderNameErrorMessage = null
                },
                onComplete = { _ ->
                    cardHolderNameErrorMessage =
                        viewModel.getCardHolderNameValidationMessage(cardHolderName)
                    // Move to the next visible section: email → phone → billing.
                    if (viewModel.showEmail ||
                        viewModel.showPhone ||
                        viewModel.showBillingSection
                    ) {
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        focusManager.clearFocus()
                    }
                },
                onFocusLost = { input ->
                    cardHolderNameErrorMessage =
                        viewModel.getCardHolderNameValidationMessage(input)
                },
                errorText = cardHolderNameErrorMessage?.let { stringResource(id = it) },
                modifier = Modifier
                    .focusRequester(nameFocusRequest)
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            AirwallexRisk.log(
                                event = "input_card_holder_name",
                                screen = "page_create_card"
                            )
                        },
                    ),
            )
        }

        if (viewModel.showEmail) {
            Spacer(modifier = Modifier.height(12.dp))

            StandardText(
                text = stringResource(R.string.airwallex_email_hint),
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Body200,
                color = AirwallexColor.textPrimary,
                modifier = Modifier.padding(
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
                    if (viewModel.showPhone || viewModel.showBillingSection) {
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        focusManager.clearFocus()
                    }
                },
                onFocusLost = { input ->
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(input)
                },
                errorText = cardHolderEmailErrorMessage?.let { stringResource(id = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .contentType(ContentType.EmailAddress)
            )
        }

        if (viewModel.showPhone) {
            Spacer(modifier = Modifier.height(12.dp))

            StandardText(
                text = stringResource(R.string.airwallex_phone_number_hint),
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Body200,
                color = AirwallexColor.textPrimary,
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            BillingTextField(
                hint = stringResource(id = R.string.airwallex_phone_number_hint),
                text = phoneNumber,
                onTextChanged = {
                    viewModel.updatePhoneNumber(it)
                    phoneErrorMessage = null
                },
                onComplete = { input ->
                    phoneErrorMessage = viewModel.getBillingValidationMessage(
                        input,
                        AddPaymentMethodViewModel.BillingFieldType.PHONE
                    )
                    if (viewModel.showBillingSection) {
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        focusManager.clearFocus()
                    }
                },
                onFocusLost = { input ->
                    phoneErrorMessage = viewModel.getBillingValidationMessage(
                        input,
                        AddPaymentMethodViewModel.BillingFieldType.PHONE
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .contentType(ContentType.PhoneNumber),
                isError = phoneErrorMessage != null,
                options = StandardTextFieldOptions(
                    inputType = StandardTextFieldOptions.InputType.PHONE,
                    returnType = StandardTextFieldOptions.ReturnType.DONE,
                ),
            )

            phoneErrorMessage?.let { errorRes ->
                Spacer(modifier = Modifier.height(4.dp))
                StandardText(
                    text = stringResource(id = errorRes),
                    textAlign = TextAlign.Left,
                    typography = AirwallexTypography.Caption100,
                    color = AirwallexColor.textError,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }

        if (viewModel.showBillingSection) {
            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Spacer(modifier = Modifier.height(12.dp))

                StandardText(
                    text = stringResource(
                        if (viewModel.showCountryCodeOnly) {
                            R.string.airwallex_billing_country_or_region
                        } else {
                            R.string.airwallex_billing_info
                        }
                    ),
                    textAlign = TextAlign.Left,
                    typography = AirwallexTypography.Body200,
                    color = AirwallexColor.textPrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (viewModel.showSameAsShippingToggle) {
                    StandardCheckBox(
                        checked = isSameAddressChecked,
                        text = stringResource(id = R.string.airwallex_same_as_shipping),
                        onCheckedChange = {
                            AnalyticsLogger.logAction("toggle_billing_address")
                            viewModel.updateSameAddressChecked(it)
                            if (it) {
                                val shippingCountryCode = viewModel.shipping?.address?.countryCode ?: viewModel.countryCode
                                viewModel.updateSelectedCountryCode(shippingCountryCode)
                                viewModel.updateStreet(viewModel.shipping?.address?.street.orEmpty())
                                viewModel.updateState(
                                    AddressSpec.mapState(
                                        shippingCountryCode,
                                        viewModel.shipping?.address?.state.orEmpty(),
                                    )
                                )
                                viewModel.updateCity(viewModel.shipping?.address?.city.orEmpty())
                                viewModel.updateZipCode(viewModel.shipping?.address?.postcode.orEmpty())
                                viewModel.updatePhoneNumber(viewModel.shipping?.phoneNumber.orEmpty())
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ADDRESS wins over COUNTRY_CODE (mutually exclusive in UI).
                val addressBottomShape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                val squareShape = RoundedCornerShape(0.dp)

                if (viewModel.showAddress) {
                    // Hide a field when the country's spec doesn't collect it, so the payload
                    // and UI match what's actually expected.
                    val showState = AddressSpec.hasState(selectedCountryCode)
                    val showCity = AddressSpec.hasCity(selectedCountryCode)
                    val showPostcode = AddressSpec.hasPostcode(selectedCountryCode)
                    val stateOptions = AddressSpec.stateList(selectedCountryCode)

                    val stateHint = stringResource(id = BillingAddressLabels.stateLabel(selectedCountryCode))

                    // Whichever field is visually last gets the bottom-rounded corners.
                    val streetIsLast = !showState && !showCity && !showPostcode
                    val stateIsLast = showState && !showCity && !showPostcode
                    val cityIsLast = showCity && !showState && !showPostcode
                    val isStateCityRow = showState && showCity
                    // When exactly one of state/city is hidden, share its row with postcode
                    // so the form stays compact instead of leaving postcode on its own line.
                    val isStatePostcodeRow = showState && !showCity && showPostcode
                    val isCityPostcodeRow = !showState && showCity && showPostcode

                    CountrySelectRow(
                        options = CountryUtils.countryList.map { it.name to it.code },
                        default = selectedCountryCode,
                        onOptionSelected = {
                            viewModel.updateSelectedCountryCode(it.second)
                            countryCodeErrorMessage = null
                            // Clear every address field on country change — fields that
                            // disappear must not silently keep their previous value in the
                            // payload, and fields that remain may carry a stale value
                            // (state codes, postcode shape) that doesn't fit the new country.
                            viewModel.updateStreet("")
                            streetErrorMessage = null
                            viewModel.updateState("")
                            stateErrorMessage = null
                            viewModel.updateCity("")
                            cityErrorMessage = null
                            viewModel.updateZipCode("")
                            postcodeErrorMessage = null
                        },
                        enabled = !isSameAddressChecked,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
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
                                AddPaymentMethodViewModel.BillingFieldType.STREET,
                            )
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        onFocusLost = { input ->
                            streetErrorMessage = viewModel.getBillingValidationMessage(
                                input,
                                AddPaymentMethodViewModel.BillingFieldType.STREET,
                            )
                        },
                        modifier = Modifier
                            .zIndex(1f)
                            .contentType(ContentType.AddressStreet),
                        enabled = !isSameAddressChecked,
                        isError = streetErrorMessage != null,
                        shape = if (streetIsLast) addressBottomShape else squareShape,
                    )

                    val cityField: @Composable (modifier: Modifier, shape: Shape) -> Unit =
                        { cityModifier, cityShape ->
                            BillingTextField(
                                hint = stringResource(id = BillingAddressLabels.cityLabel(selectedCountryCode)),
                                text = city,
                                onTextChanged = {
                                    viewModel.updateCity(it)
                                    cityErrorMessage = null
                                },
                                onComplete = { input ->
                                    cityErrorMessage = viewModel.getBillingValidationMessage(
                                        input,
                                        AddPaymentMethodViewModel.BillingFieldType.CITY,
                                    )
                                    focusManager.moveFocus(
                                        if (isCityPostcodeRow) FocusDirection.Right else FocusDirection.Down,
                                    )
                                },
                                onFocusLost = { input ->
                                    cityErrorMessage = viewModel.getBillingValidationMessage(
                                        input,
                                        AddPaymentMethodViewModel.BillingFieldType.CITY,
                                    )
                                },
                                modifier = cityModifier
                                    .contentType(ContentType.AddressLocality),
                                enabled = !isSameAddressChecked,
                                isError = cityErrorMessage != null,
                                shape = cityShape,
                            )
                        }

                    val stateField: @Composable (modifier: Modifier, shape: Shape) -> Unit =
                        { stateModifier, stateShape ->
                            if (stateOptions != null) {
                                StateSelectRow(
                                    hint = stateHint,
                                    options = stateOptions,
                                    default = state.ifEmpty { null },
                                    onOptionSelected = {
                                        viewModel.updateState(it.first)
                                        stateErrorMessage = null
                                    },
                                    enabled = !isSameAddressChecked,
                                    isError = stateErrorMessage != null,
                                    shape = stateShape,
                                    modifier = stateModifier,
                                )
                            } else {
                                BillingTextField(
                                    hint = stateHint,
                                    text = state,
                                    onTextChanged = {
                                        viewModel.updateState(it)
                                        stateErrorMessage = null
                                    },
                                    onComplete = { input ->
                                        stateErrorMessage = viewModel.getBillingValidationMessage(
                                            input,
                                            AddPaymentMethodViewModel.BillingFieldType.STATE,
                                        )
                                        focusManager.moveFocus(
                                            if (isStateCityRow || isStatePostcodeRow) {
                                                FocusDirection.Right
                                            } else {
                                                FocusDirection.Down
                                            },
                                        )
                                    },
                                    onFocusLost = { input ->
                                        stateErrorMessage = viewModel.getBillingValidationMessage(
                                            input,
                                            AddPaymentMethodViewModel.BillingFieldType.STATE,
                                        )
                                    },
                                    modifier = stateModifier
                                        .contentType(ContentType.AddressRegion),
                                    enabled = !isSameAddressChecked,
                                    isError = stateErrorMessage != null,
                                    shape = stateShape,
                                )
                            }
                        }

                    val postcodeField: @Composable (modifier: Modifier, shape: Shape) -> Unit =
                        { postcodeModifier, postcodeShape ->
                            BillingTextField(
                                hint = stringResource(
                                    id = BillingAddressLabels.postcodeLabel(selectedCountryCode),
                                ),
                                text = zipCode,
                                onTextChanged = {
                                    viewModel.updateZipCode(it)
                                    postcodeErrorMessage = null
                                },
                                onComplete = { input ->
                                    postcodeErrorMessage = viewModel.getPostcodeValidationMessage(
                                        input,
                                        selectedCountryCode,
                                    )
                                    focusManager.moveFocus(FocusDirection.Down)
                                },
                                onFocusLost = { input ->
                                    postcodeErrorMessage = viewModel.getPostcodeValidationMessage(
                                        input,
                                        selectedCountryCode,
                                    )
                                },
                                modifier = postcodeModifier
                                    .contentType(ContentType.PostalCode),
                                enabled = !isSameAddressChecked,
                                isError = postcodeErrorMessage != null,
                                shape = postcodeShape,
                            )
                        }

                    when {
                        isStateCityRow -> {
                            // When the row is last, split the bottom-rounded corners across the two halves.
                            val rowIsLast = !showPostcode
                            Row {
                                stateField(
                                    Modifier.weight(1f),
                                    if (rowIsLast) RoundedCornerShape(bottomStart = 8.dp) else squareShape,
                                )
                                cityField(
                                    Modifier.weight(1f),
                                    if (rowIsLast) RoundedCornerShape(bottomEnd = 8.dp) else squareShape,
                                )
                            }
                        }
                        isStatePostcodeRow -> {
                            Row {
                                stateField(
                                    Modifier.weight(1f),
                                    RoundedCornerShape(bottomStart = 8.dp),
                                )
                                postcodeField(
                                    Modifier.weight(1f),
                                    RoundedCornerShape(bottomEnd = 8.dp),
                                )
                            }
                        }
                        isCityPostcodeRow -> {
                            Row {
                                cityField(
                                    Modifier.weight(1f),
                                    RoundedCornerShape(bottomStart = 8.dp),
                                )
                                postcodeField(
                                    Modifier.weight(1f),
                                    RoundedCornerShape(bottomEnd = 8.dp),
                                )
                            }
                        }
                        showState -> {
                            stateField(
                                Modifier.fillMaxWidth(),
                                if (stateIsLast) addressBottomShape else squareShape,
                            )
                        }
                        showCity -> {
                            cityField(
                                Modifier.fillMaxWidth(),
                                if (cityIsLast) addressBottomShape else squareShape,
                            )
                        }
                    }

                    // Render postcode standalone only when it doesn't share a row above —
                    // i.e. either both state and city are visible, or both are hidden.
                    if (showPostcode && !isStatePostcodeRow && !isCityPostcodeRow) {
                        postcodeField(Modifier.fillMaxWidth(), addressBottomShape)
                    }
                } else if (viewModel.showCountryCodeOnly) {
                    CountrySelectRow(
                        options = CountryUtils.countryList.map { it.name to it.code },
                        default = selectedCountryCode,
                        onOptionSelected = {
                            viewModel.updateSelectedCountryCode(it.second)
                            countryCodeErrorMessage = null
                        },
                        enabled = true,
                        shape = RoundedCornerShape(8.dp),
                    )
                }

                val billingErrorMessage: String? = (
                    streetErrorMessage
                        ?: stateErrorMessage
                        ?: cityErrorMessage
                        ?: postcodeErrorMessage
                        ?: countryCodeErrorMessage
                    )?.let { stringResource(id = it) }
                if (billingErrorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    StandardText(
                        text = billingErrorMessage,
                        textAlign = TextAlign.Left,
                        typography = AirwallexTypography.Caption100,
                        color = AirwallexColor.textError,
                        modifier = Modifier.padding(start = 16.dp),
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
            text = checkoutButtonTitle ?: stringResource(viewModel.ctaRes),
            onClick = {
                focusManager.clearFocus()

                cardNumberErrorMessage = viewModel.getCardNumberValidationMessage(cardNumber)
                expiryDateErrorMessage = viewModel.getExpiryValidationMessage(expiryDate)
                cvvErrorMessage = viewModel.getCvvValidationMessage(cvv, brand)
                if (viewModel.showName) {
                    cardHolderNameErrorMessage =
                        viewModel.getCardHolderNameValidationMessage(cardHolderName)
                }
                if (viewModel.showEmail) {
                    cardHolderEmailErrorMessage = viewModel.getEmailValidationMessage(email)
                }
                if (viewModel.showAddress) {
                    // Anything visible is required — every visible address field gets a
                    // blank check, regardless of any per-country "declared required" hint.
                    streetErrorMessage = viewModel.getBillingValidationMessage(
                        street,
                        AddPaymentMethodViewModel.BillingFieldType.STREET,
                    )
                    stateErrorMessage = if (AddressSpec.hasState(selectedCountryCode)) {
                        viewModel.getBillingValidationMessage(
                            state,
                            AddPaymentMethodViewModel.BillingFieldType.STATE,
                        )
                    } else {
                        null
                    }
                    cityErrorMessage = if (AddressSpec.hasCity(selectedCountryCode)) {
                        viewModel.getBillingValidationMessage(
                            city,
                            AddPaymentMethodViewModel.BillingFieldType.CITY,
                        )
                    } else {
                        null
                    }
                    postcodeErrorMessage = if (AddressSpec.hasPostcode(selectedCountryCode)) {
                        viewModel.getPostcodeValidationMessage(zipCode, selectedCountryCode)
                    } else {
                        null
                    }
                }
                if (viewModel.showPhone) {
                    phoneErrorMessage = viewModel.getBillingValidationMessage(
                        phoneNumber,
                        AddPaymentMethodViewModel.BillingFieldType.PHONE
                    )
                }
                if (viewModel.showAddress || viewModel.showCountryCodeOnly) {
                    countryCodeErrorMessage = viewModel.getBillingValidationMessage(
                        selectedCountryCode,
                        AddPaymentMethodViewModel.BillingFieldType.COUNTRY_CODE
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
                    cityErrorMessage,
                    postcodeErrorMessage,
                    phoneErrorMessage,
                    countryCodeErrorMessage,
                ).isEmpty()
                if (allValidated) {
                    // All fields are valid, so proceed to confirm payment.
                    val card = viewModel.createCard(cardNumber, cardHolderName, expiryDate, cvv)
                        ?: return@StandardSolidButton
                    val billing = viewModel.createBilling(
                        name = cardHolderName,
                        email = email,
                        phoneNumber = phoneNumber,
                        countryCode = selectedCountryCode,
                        state = state,
                        city = city,
                        street = street,
                        postcode = zipCode,
                    )
                    paymentFlowListener.onLoadingStateChanged(true, activity)
                    AnalyticsLogger.logAction(
                        TAP_PAY_BUTTON,
                        mapOf(PAYMENT_METHOD to PaymentMethodType.CARD.value)
                    )
                    AirwallexRisk.log(event = CLICK_PAY_BUTTON, screen = PAGE_CREATE_CARD)
                    paymentFlowViewModel.checkoutWithNewCard(
                        card = card,
                        saveCard = isSaveCardChecked,
                        billing = billing,
                    )
                }
                // Otherwise do nothing
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}