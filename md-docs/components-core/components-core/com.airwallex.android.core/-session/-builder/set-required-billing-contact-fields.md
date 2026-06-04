//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[Session](../index.md)/[Builder](index.md)/[setRequiredBillingContactFields](set-required-billing-contact-fields.md)

# setRequiredBillingContactFields

[androidJvm]\
fun [setRequiredBillingContactFields](set-required-billing-contact-fields.md)(fields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../../-required-billing-contact-field/index.md)&gt;?): [Session.Builder](index.md)

Configure which billing fields the new-card UI should collect and the headless checkout should validate. Pass `null` (the default) to derive from the legacy [setRequireBillingInformation](set-require-billing-information.md) / [setRequireEmail](set-require-email.md) flags. An empty set hides the entire billing section.

**Card payments only.** Google Pay billing is configured separately via [GooglePayOptions](../../-google-pay-options/index.md) (`billingAddressRequired`, `emailRequired`); changes here do not propagate to the Google Pay sheet.
