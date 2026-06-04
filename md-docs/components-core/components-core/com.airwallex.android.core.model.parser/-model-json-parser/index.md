//[components-core](../../../index.md)/[com.airwallex.android.core.model.parser](../index.md)/[ModelJsonParser](index.md)

# ModelJsonParser

interface [ModelJsonParser](index.md)&lt;[Model](index.md)&gt;

#### Inheritors

| |
|---|
| [AddressParser](../-address-parser/index.md) |
| [AirwallexErrorParser](../-airwallex-error-parser/index.md) |
| [AvailablePaymentMethodTypeParser](../-available-payment-method-type-parser/index.md) |
| [AvailablePaymentMethodTypeResourceParser](../-available-payment-method-type-resource-parser/index.md) |
| [BankParser](../-bank-parser/index.md) |
| [BankResourcesParser](../-bank-resources-parser/index.md) |
| [BankResponseParser](../-bank-response-parser/index.md) |
| [BillingParser](../-billing-parser/index.md) |
| [ClientSecretParser](../-client-secret-parser/index.md) |
| [DynamicSchemaFieldCandidateParser](../-dynamic-schema-field-candidate-parser/index.md) |
| [DynamicSchemaFieldParser](../-dynamic-schema-field-parser/index.md) |
| [DynamicSchemaFieldValidationParser](../-dynamic-schema-field-validation-parser/index.md) |
| [DynamicSchemaParser](../-dynamic-schema-parser/index.md) |
| [LogoResourcesParser](../-logo-resources-parser/index.md) |
| [NextActionParser](../-next-action-parser/index.md) |
| [PageParser](../-page-parser/index.md) |
| [PaymentConsentParser](../-payment-consent-parser/index.md) |
| [PaymentIntentParser](../-payment-intent-parser/index.md) |
| [PaymentMethodOptionsParser](../-payment-method-options-parser/index.md) |
| [PaymentMethodParser](../-payment-method-parser/index.md) |
| [GooglePayParser](../-payment-method-parser/-google-pay-parser/index.md) |
| [CardParser](../-payment-method-parser/-card-parser/index.md) |
| [PaymentMethodReferenceParser](../-payment-method-reference-parser/index.md) |
| [PaymentMethodTypeInfoParser](../-payment-method-type-info-parser/index.md) |
| [PhysicalProductParser](../-physical-product-parser/index.md) |
| [PurchaseOrderParser](../-purchase-order-parser/index.md) |
| [ShippingParser](../-shipping-parser/index.md) |
| [ThreeDSecureParesParser](../-three-d-secure-pares-parser/index.md) |
| [ThreeDSecureParser](../-three-d-secure-parser/index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [dateFormat](date-format.md) | [androidJvm]<br>open val [dateFormat](date-format.md): [SimpleDateFormat](https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.html) |

## Functions

| Name | Summary |
|---|---|
| [parse](parse.md) | [androidJvm]<br>abstract fun [parse](parse.md)(json: [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)): [Model](index.md)? |
