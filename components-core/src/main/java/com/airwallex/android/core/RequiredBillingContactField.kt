package com.airwallex.android.core

/**
 * Billing contact fields that the SDK can collect on the new-card payment screen
 * and validate in the headless checkout path. Mirrors iOS's
 * `AWXRequiredBillingContactFields` option set.
 *
 * Configure via [AirwallexSession.requiredBillingContactFields]. An empty set hides
 * the entire billing section; `null` (the default) derives the set from the legacy
 * [AirwallexSession.isBillingInformationRequired] / [AirwallexSession.isEmailRequired]
 * flags so existing integrations keep their current behavior.
 *
 * `ADDRESS` and `COUNTRY_CODE` are mutually exclusive in the UI: when both are set,
 * `ADDRESS` wins and the standalone country picker is suppressed.
 */
enum class RequiredBillingContactField {
    NAME,
    EMAIL,
    PHONE,
    ADDRESS,
    COUNTRY_CODE,
}
