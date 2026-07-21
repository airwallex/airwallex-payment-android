Feature: Card Payment - Low-level API Integration
  As a merchant using Airwallex's low-level API ("Integrate with low-level API")
  I want to drive payments from my own UI without the Airwallex payment list
  So that I can fully control the checkout UX while still using the SDK for card / 3DS / redirect handling

  # ═══════════════════════════════════════════════════════════════════════════
  # INTEGRATION: LOW-LEVEL API (APIIntegrationActivity)
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # API integration = Merchant calls SDK methods directly from a custom screen
  # Entry point in sample: "Integrate with low-level API"
  # The screen exposes discrete buttons:
  #   - "Get saved cards"               → loads consents (shows Customer Dialog)
  #   - "Pay with card"                 → one-shot card payment (or consent in recurring)
  #   - "Pay with card and save card"   → one-off pay + save card
  #   - "Pay with card and trigger 3DS" → forces 3DS challenge
  #   - "Redirect Payment"              → redirect-based PaymentMethod (e.g. Pay Now / Authorize Now)
  #
  # Coverage: 24 scenarios across 7 yaml files
  # - Legacy Session: 6 files (Guest, Customer ×3, Merchant ×2)
  # - New Session:    5 files (Customer ×3, Merchant ×2)
  #
  # Each yaml file is a sequential script that exercises 4-5 scenarios in one
  # run, sharing the same customer / session. Scenarios are documented here
  # individually so they can be planned and reported on separately.
  #
  # ═══════════════════════════════════════════════════════════════════════════

  Background:
    Given the Airwallex Payment SDK is initialized
    And the environment is set to "DEMO"
    And I select "Integrate with low-level API"
    And the Express Checkout setting is "false"

  # ═══════════════════════════════════════════════════════════════════════════
  # API MAIN SCENARIO 1/3: ONE-OFF PAYMENT — GUEST
  # File: .maestro/Api/LegacySession/Guest/test_api_guest_oneoff.yaml
  # ═══════════════════════════════════════════════════════════════════════════

  @api @one-off @guest @legacy-session @covered
  Scenario: Guest cannot have saved cards
    Given I am a guest user
    And the checkout mode is "one-off"
    When I tap "Get saved cards"
    Then I see the "Customer Dialog"
    And no saved cards are listed (no Visa, no Mastercard, no PAY button)

  @api @one-off @guest @legacy-session @covered
  Scenario: Guest pays with card (no 3DS, no save)
    Given I am a guest user
    And the checkout mode is "one-off"
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment successful"
    And after dismissing, "Get saved cards" still shows no saved cards

  @api @one-off @guest @redirect @legacy-session @covered
  Scenario: Guest pays via redirect payment method
    Given I am a guest user
    And the checkout mode is "one-off"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then the redirect payment succeeds

  # ═══════════════════════════════════════════════════════════════════════════
  # API MAIN SCENARIO 1/3: ONE-OFF PAYMENT — AUTHENTICATED CUSTOMER
  # Files:
  #   .maestro/Api/LegacySession/CustomerTriggered/test_api_customer_oneoff.yaml
  #   .maestro/Api/NewSession/CustomerTriggered/test_api_customer_oneoff.yaml
  # ═══════════════════════════════════════════════════════════════════════════

  @api @one-off @auth @covered
  Scenario Outline: Authenticated customer starts with no saved cards
    Given I am an authenticated user with customer ID "<UI_TESTING_CUSTOMER_ID>"
    And the session type is "<session>"
    And the checkout mode is "one-off"
    When I tap "Get saved cards"
    Then I see the "Customer Dialog"
    And no saved cards are listed

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @one-off @auth @no-save @covered
  Scenario Outline: Authenticated customer pays with card without saving
    Given the session type is "<session>"
    And the checkout mode is "one-off"
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment successful"
    And no card is saved to the customer

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @one-off @auth @save-card @covered
  Scenario Outline: Authenticated customer pays and saves the card
    Given the session type is "<session>"
    And the checkout mode is "one-off"
    When I tap "Pay with card and save card"
    And I tap "PAY"
    Then I see "Payment successful"
    And "Get saved cards" lists "Visa •••• 1003"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @one-off @auth @3ds @covered
  Scenario Outline: Authenticated customer pays a 3DS-required card (auto-saved)
    Given the session type is "<session>"
    And the checkout mode is "one-off"
    When I tap "Pay with card and trigger 3DS"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" additionally lists "Visa •••• 0088"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @one-off @auth @redirect @covered
  Scenario Outline: Authenticated customer pays via redirect payment method
    Given the session type is "<session>"
    And the checkout mode is "one-off"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then the redirect payment succeeds

    Examples:
      | session |
      | legacy  |
      | new     |

  # ═══════════════════════════════════════════════════════════════════════════
  # API MAIN SCENARIO 2/3: RECURRING CONSENT (no initial payment)
  # Files:
  #   .maestro/Api/LegacySession/CustomerTriggered/test_api_customer_recurring.yaml
  #   .maestro/Api/NewSession/CustomerTriggered/test_api_customer_recurring.yaml
  #   .maestro/Api/LegacySession/MerchantTriggered/test_api_merchant_recurring.yaml
  #   .maestro/Api/NewSession/MerchantTriggered/test_api_merchant_recurring.yaml
  # ═══════════════════════════════════════════════════════════════════════════

  @api @recurring @auth @customer-triggered @covered
  Scenario Outline: Customer-triggered recurring consent (no payment) — first card
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "customer"
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment successful"
    And "Get saved cards" lists "Visa •••• 0008"
    And the per-card "PAY" button in the Customer Dialog is disabled

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @customer-triggered @duplicate @covered
  Scenario Outline: Customer-triggered recurring rejects duplicate consent
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "customer"
    And a consent for the card already exists
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment failed"
    And only one consent remains for that card

    Examples:
      | session | extra_assertion                  |
      | legacy  | generic failure                  |
      | new     | error message contains "already exists" |

  @api @recurring @auth @customer-triggered @3ds @covered
  Scenario Outline: Customer-triggered recurring with a 3DS card
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "customer"
    When I tap "Pay with card and trigger 3DS"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" additionally lists "Mastercard •••• 4518"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @customer-triggered @redirect @covered
  Scenario Outline: Customer-triggered recurring via redirect payment method
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "customer"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then a loading overlay appears
    And I press back to dismiss it (no success popup in recurring mode)

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @merchant-triggered @3ds-on-new-card @covered
  Scenario Outline: Merchant-triggered recurring consent (no payment) — first card requires 3DS
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "merchant"
    When I tap "Pay with card"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" lists "Visa •••• 0008"
    And the per-card "PAY" button in the Customer Dialog is disabled

    # NOTE: Merchant-triggered recurring forces 3DS on every new card,
    # even when the basic "Pay with card" button is used.

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @merchant-triggered @duplicate @covered
  Scenario Outline: Merchant-triggered recurring allows duplicate consent (with 3DS)
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "merchant"
    And a consent for the card already exists
    When I tap "Pay with card"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And a second consent is created for the same card

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @merchant-triggered @3ds @covered
  Scenario Outline: Merchant-triggered recurring with an explicit 3DS card
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "merchant"
    When I tap "Pay with card and trigger 3DS"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" additionally lists "Mastercard •••• 4518"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring @auth @merchant-triggered @redirect @covered
  Scenario Outline: Merchant-triggered recurring via redirect payment method
    Given the session type is "<session>"
    And the checkout mode is "recurring"
    And the next action trigger is "merchant"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then a loading overlay appears with no success popup
    And I press back to dismiss it
    And merchant-triggered consents cannot be deleted (no cleanup performed)

    Examples:
      | session |
      | legacy  |
      | new     |

  # ═══════════════════════════════════════════════════════════════════════════
  # API MAIN SCENARIO 3/3: RECURRING + PAYMENT (consent + initial charge)
  # Files:
  #   .maestro/Api/LegacySession/CustomerTriggered/test_api_customer_recurring_with_payment.yaml
  #   .maestro/Api/NewSession/CustomerTriggered/test_api_customer_recurring_with_payment.yaml
  #   .maestro/Api/LegacySession/MerchantTriggered/test_api_merchant_recurring_with_payment.yaml
  #   .maestro/Api/NewSession/MerchantTriggered/test_api_merchant_recurring_with_payment.yaml
  # ═══════════════════════════════════════════════════════════════════════════

  @api @recurring-with-payment @auth @customer-triggered @covered
  Scenario Outline: Customer-triggered recurring + payment — first card
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "customer"
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment successful"
    And "Get saved cards" lists "Visa •••• 1003"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @customer-triggered @duplicate @covered
  Scenario Outline: Customer-triggered recurring + payment allows duplicate consent
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "customer"
    And a consent for the card already exists
    When I tap "Pay with card"
    And I tap "PAY"
    Then I see "Payment successful"
    And a second consent is created for the same card

    # NOTE: Differs from "recurring" (no payment) — duplicates are allowed
    # for customer-triggered recurring+payment in both Legacy and New Session.

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @customer-triggered @3ds @covered
  Scenario Outline: Customer-triggered recurring + payment with a 3DS card
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "customer"
    When I tap "Pay with card and trigger 3DS"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" additionally lists "Visa •••• 0088"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @customer-triggered @redirect @covered
  Scenario Outline: Customer-triggered recurring + payment via redirect
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "customer"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then a loading overlay appears
    And I press back to dismiss it

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @merchant-triggered @3ds-on-new-card @covered
  Scenario Outline: Merchant-triggered recurring + payment — first card requires 3DS
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "merchant"
    When I tap "Pay with card"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" lists "Visa •••• 1003"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @merchant-triggered @duplicate @covered
  Scenario Outline: Merchant-triggered recurring + payment allows duplicate (with 3DS)
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "merchant"
    And a consent for the card already exists
    When I tap "Pay with card"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And a second consent is created for the same card

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @merchant-triggered @3ds @covered
  Scenario Outline: Merchant-triggered recurring + payment with an explicit 3DS card
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "merchant"
    When I tap "Pay with card and trigger 3DS"
    And I tap "PAY"
    And I complete the 3DS challenge
    Then I see "Payment successful"
    And "Get saved cards" additionally lists "Visa •••• 0088"

    Examples:
      | session |
      | legacy  |
      | new     |

  @api @recurring-with-payment @auth @merchant-triggered @redirect @covered
  Scenario Outline: Merchant-triggered recurring + payment via redirect
    Given the session type is "<session>"
    And the checkout mode is "recurring and payment"
    And the next action trigger is "merchant"
    When I tap "Redirect Payment"
    And I complete the redirect flow
    Then the redirect payment is processed
    And merchant-triggered consents cannot be deleted (no cleanup performed)

    Examples:
      | session |
      | legacy  |
      | new     |

  # ═══════════════════════════════════════════════════════════════════════════
  # COVERAGE SUMMARY - LOW-LEVEL API
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Total Scenarios: 24 (each yaml file packs 4-5 scenarios)
  #
  # By Yaml File:
  # - LegacySession/Guest/test_api_guest_oneoff.yaml                    → 3 scenarios
  # - LegacySession/CustomerTriggered/test_api_customer_oneoff.yaml     → 5 scenarios
  # - LegacySession/CustomerTriggered/test_api_customer_recurring.yaml  → 5 scenarios
  # - LegacySession/CustomerTriggered/test_api_customer_recurring_with_payment.yaml → 5 scenarios
  # - LegacySession/MerchantTriggered/test_api_merchant_recurring.yaml  → 5 scenarios
  # - LegacySession/MerchantTriggered/test_api_merchant_recurring_with_payment.yaml → 5 scenarios
  # - NewSession/CustomerTriggered/test_api_customer_oneoff.yaml        → 5 scenarios
  # - NewSession/CustomerTriggered/test_api_customer_recurring.yaml     → 5 scenarios
  # - NewSession/CustomerTriggered/test_api_customer_recurring_with_payment.yaml → 5 scenarios
  # - NewSession/MerchantTriggered/test_api_merchant_recurring.yaml     → 5 scenarios
  # - NewSession/MerchantTriggered/test_api_merchant_recurring_with_payment.yaml → 5 scenarios
  #
  # By Checkout Mode:
  # - One-off:           3 (guest) + 5×2 (customer L/N) = 13 scenarios
  # - Recurring:         5×2 (customer L/N) + 5×2 (merchant L/N) = 20 scenarios
  # - Recurring+Payment: 5×2 (customer L/N) + 5×2 (merchant L/N) = 20 scenarios
  #
  # ═══════════════════════════════════════════════════════════════════════════
  # KEY BEHAVIOURAL NOTES
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Duplicate consent rules (verified against the yaml files):
  #
  # | Trigger  | Mode               | Legacy   | New      |
  # |----------|--------------------|----------|----------|
  # | Customer | recurring          | rejected | rejected (with "already exists" message) |
  # | Customer | recurring+payment  | allowed  | allowed  |
  # | Merchant | recurring          | allowed  | allowed  |
  # | Merchant | recurring+payment  | allowed  | allowed  |
  #
  # Merchant-triggered specific:
  # - Every new card forces 3DS, even via the basic "Pay with card" button
  # - Consents cannot be deleted from the customer-side UI (no cleanup step)
  #
  # In recurring modes:
  # - The Customer Dialog's per-card "PAY" button is disabled
  # - Redirect Payment shows a loading overlay with no success popup;
  #   tester presses back to dismiss it
  #
  # ═══════════════════════════════════════════════════════════════════════════
