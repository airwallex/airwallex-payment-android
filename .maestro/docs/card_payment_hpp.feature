Feature: Card Payment - HPP Integration
  As a merchant using Airwallex HPP (Hosted Payment Page / Native UI)
  I want customers to complete payments through Airwallex-controlled UI
  So that I can process card payments securely

  # ═══════════════════════════════════════════════════════════════════════════
  # INTEGRATION: HPP (Hosted Payment Page / Native UI)
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # HPP = Airwallex-controlled payment UI
  # Also known as: Native UI, Payment List, "Launch payment list"
  #
  # Coverage: 16 test scenarios
  # - Guest one-off: 7 tests
  # - Auth one-off: 5 tests
  # - Auth recurring: 4 tests (2 customer + 2 merchant-triggered)
  #
  # ═══════════════════════════════════════════════════════════════════════════

  Background:
    Given the Airwallex Payment SDK is initialized
    And the environment is set to "DEMO"
    And I use the HPP integration

  # ═══════════════════════════════════════════════════════════════════════════
  # HPP MAIN SCENARIO 1/3: ONE-OFF PAYMENT
  # ═══════════════════════════════════════════════════════════════════════════

  @hpp @one-off @guest @covered
  Scenario Outline: Guest user one-off payment via HPP
    Given I am a guest user
    And the checkout mode is "one-off"
    When I navigate to the payment flow
    And I enter valid card details
    And I submit the payment
    And the 3DS outcome is "<3ds_outcome>"
    Then the payment result is "<payment_result>"

    Examples: 3DS Variations
      | 3ds_outcome | payment_result | maestro_test                      | notes                              |
      | success     | success        | test_guest_3ds_none               | No 3DS required                    |
      | success     | success        | test_guest_3ds_combined           | 3DS combined flow (tab layout)     |
      | success     | success        | test_guest_3ds_combined_accordion | 3DS combined flow (accordion)      |
      | success     | success        | test_guest_3ds_challenge          | 3DS challenge flow                 |
      | success     | success        | test_guest_3ds_data_collection    | 3DS data collection flow           |
      | cancelled   | cancelled      | test_guest_payment_cancel_3ds     | User cancels 3DS challenge         |

    # NOTE: We have 4 tests for 3DS success (combined, challenge, data collection, none)
    # but they all test the same SDK code path. Different 3DS types are server responses.
    # CONSOLIDATION: Could reduce to 2 tests (1 success + 1 cancel)

  @hpp @one-off @guest @cancellation @covered
  Scenario: Guest user cancels payment at method selection
    Given I am a guest user
    And the checkout mode is "one-off"
    When I navigate to the payment flow
    And I view the payment methods
    And I navigate back without selecting
    Then the payment is cancelled
    And I receive a cancellation confirmation

    # Maestro: test_guest_payment_cancel

  @hpp @one-off @auth @card-saving @covered
  Scenario Outline: Authenticated user one-off payment with card saving
    Given I am an authenticated user
    And the checkout mode is "one-off"
    When I navigate to the payment flow
    Then I see the "Save this card for future payments" option

    When I enter card details
    And I <save_action>
    And I submit the payment
    Then the payment is successful

    When I navigate to the payment flow again
    Then <card_status>

    Examples: Card Saving Behavior
      | save_action          | card_status               | maestro_test                  | layout    |
      | enable card saving   | I see my saved card       | test_user_card_saving_tab     | Tab       |
      | enable card saving   | I see my saved card       | test_user_card_saving_accordion | Accordion |
      | do not enable saving | I see card entry form     | test_user_oneoff_payment      | Tab       |

    # NOTE: Layout (tab vs accordion) is just UI rendering - both test same business logic

  @hpp @one-off @auth @saved-card @covered
  Scenario: Authenticated user pays with saved card
    Given I am an authenticated user
    And the checkout mode is "one-off"
    And I have a saved card on file
    When I navigate to the payment flow
    Then I see my saved cards listed

    When I select the first saved card
    And I enter CVV for verification
    And I submit the payment
    Then the payment is successful

    # Maestro: flow_pay_with_consent, flow_user_card_saving_consent_payment

  @hpp @one-off @auth @delete-cards @covered
  Scenario: Authenticated user removes all saved cards
    Given I am an authenticated user
    And I have saved cards on file
    When I navigate to the payment flow
    And I view my saved cards
    And I delete all saved cards
    And I navigate back
    Then the payment is cancelled
    And no saved cards remain

    # Maestro: flow_remove_all_consents, flow_user_card_saving_consent_payment

  # ═══════════════════════════════════════════════════════════════════════════
  # HPP MAIN SCENARIO 2/3: RECURRING CONSENT
  # ═══════════════════════════════════════════════════════════════════════════

  @hpp @recurring @auth @customer-triggered @covered
  Scenario: Customer-triggered recurring consent (no initial payment)
    Given I am an authenticated user
    And the checkout mode is "recurring"
    And the next action trigger is "customer"

    When I navigate to the payment flow
    Then I do not see the "Save this card" option

    When I enter card details
    And I submit the consent
    Then no payment is processed
    And a recurring consent is created

    When I attempt to create a consent with the same card again
    Then the duplicate consent is rejected

    When I switch to one-off mode
    And I navigate to the payment flow
    Then I see my saved consent
    And I can delete the consent

    # Maestro: test_user_recurring_trigger_by_customer (WITH_PAYMENT: false, TRIGGER_BY_CUSTOMER: true)
    # Location: .maestro/Card/

  @hpp @recurring @auth @merchant-triggered @covered
  Scenario: Merchant-triggered recurring consent (no initial payment)
    Given I am an authenticated user
    And the checkout mode is "recurring"
    And the next action trigger is "merchant"

    When I navigate to the payment flow
    Then I do not see the "Save this card" option

    When I enter card details
    And I submit the consent
    Then no payment is processed
    And a recurring consent is created

    When I attempt to create a consent with the same card again
    Then the duplicate is deduplicated
    And only one consent is shown

    When I switch to one-off mode
    And I navigate to the payment flow
    Then I see my saved consent
    But I cannot delete the merchant-managed consent
    And I see "Cannot remove card" when attempting deletion

    # Maestro: test_user_recurring_trigger_by_merchant (WITH_PAYMENT: false, TRIGGER_BY_CUSTOMER: false)
    # Location: .maestro/MerchantTriggered/Card/
    # Assertion: flow_assert_merchant_consent.yaml verifies deletion protection

  # ═══════════════════════════════════════════════════════════════════════════
  # HPP MAIN SCENARIO 3/3: RECURRING + PAYMENT
  # ═══════════════════════════════════════════════════════════════════════════

  @hpp @recurring-with-payment @auth @customer-triggered @covered
  Scenario: Customer-triggered recurring consent with initial payment
    Given I am an authenticated user
    And the checkout mode is "recurring and payment"
    And the next action trigger is "customer"

    When I navigate to the payment flow
    Then I do not see the "Save this card" option

    When I enter card details
    And I submit the consent
    Then the payment is successful
    And a recurring consent is created

    When I attempt to create a consent with the same card again
    Then the duplicate consent is allowed

    When I switch to one-off mode
    And I navigate to the payment flow
    Then I see my saved consent
    And I can delete the consent

    # Maestro: test_user_recurring_with_payment_trigger_by_customer (WITH_PAYMENT: true, TRIGGER_BY_CUSTOMER: true)
    # Location: .maestro/Card/

  @hpp @recurring-with-payment @auth @merchant-triggered @covered
  Scenario: Merchant-triggered recurring consent with initial payment
    Given I am an authenticated user
    And the checkout mode is "recurring and payment"
    And the next action trigger is "merchant"

    When I navigate to the payment flow
    Then I do not see the "Save this card" option

    When I enter card details
    And I submit the consent
    Then the payment is successful
    And a recurring consent is created

    When I attempt to create a consent with the same card again
    Then the duplicate is deduplicated
    And only one consent is shown

    When I switch to one-off mode
    And I navigate to the payment flow
    Then I see my saved consent
    But I cannot delete the merchant-managed consent
    And I see "Cannot remove card" when attempting deletion

    # Maestro: test_user_recurring_with_payment_trigger_by_merchant (WITH_PAYMENT: true, TRIGGER_BY_CUSTOMER: false)
    # Location: .maestro/MerchantTriggered/Card/
    # Assertion: flow_assert_merchant_consent.yaml verifies deletion protection

  # ═══════════════════════════════════════════════════════════════════════════
  # HPP MAIN SCENARIO 4/4: BILLING FIELDS CONFIGURATION (UI-only, no payment)
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Regression coverage for APAM-536 / PR #329 — `setRequiredBillingContactFields`
  # replaces the legacy `setRequireBillingInformation`/`setRequireEmail` booleans
  # with a per-field set ({NAME, EMAIL, PHONE, ADDRESS, COUNTRY_CODE}). The new-card
  # composable (`AddCardSection`) must render only the requested fields. No payment
  # is made — the test simply opens the form, asserts visibility, navigates back,
  # reconfigures via Settings, and repeats.
  #
  @hpp @one-off @guest @billing-config @ui-only @covered
  Scenario Outline: New-card form renders only the merchant-configured billing fields
    Given I am a guest
    And the checkout mode is "one-off"
    And the merchant has set `requiredBillingContactFields` to <config>

    When I open the new-card form
    Then I see the cardholder-name field is <name>
    And I see the email field is <email>
    And I see the phone field is <phone>
    And I see the billing-info section is <billing>
    And I see the street row is <street>

    Examples:
      | config                  | name    | email   | phone   | billing | street  |
      | unset (legacy default)  | visible | hidden  | visible | visible | visible |
      | empty set               | hidden  | hidden  | hidden  | hidden  | hidden  |
      | { NAME }                | visible | hidden  | hidden  | hidden  | hidden  |
      | { EMAIL }               | hidden  | visible | hidden  | hidden  | hidden  |
      | { PHONE }               | hidden  | hidden  | visible | hidden  | hidden  |
      | { ADDRESS }             | hidden  | hidden  | hidden  | visible | visible |
      | { COUNTRY_CODE }        | hidden  | hidden  | hidden  | visible | hidden  |
      | { ADDRESS, COUNTRY_CODE } | hidden  | hidden  | hidden  | visible | visible |
    # Note on last row: ADDRESS and COUNTRY_CODE are mutually exclusive in the UI;
    # ADDRESS wins and renders the full address block (street + state + city + postcode).
    # Maestro: test_hpp_billing_fields_visibility.yaml

  # ═══════════════════════════════════════════════════════════════════════════
  # COVERAGE SUMMARY - HPP
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Total Scenarios: 11 (10 payment + 1 UI-only regression)
  # Total Tests: 17
  #
  # By Checkout Mode:
  # - One-off: 12 tests (7 guest + 5 auth)
  # - Recurring: 2 tests (1 customer + 1 merchant)
  # - Recurring+Payment: 2 tests (1 customer + 1 merchant)
  #
  # By User Type:
  # - Guest: 7 tests (one-off only)
  # - Authenticated: 9 tests (one-off + recurring)
  #
  # By Payment Trigger:
  # - Customer: 2 tests (.maestro/Card/)
  # - Merchant: 2 tests (.maestro/MerchantTriggered/Card/)
  #
  # ═══════════════════════════════════════════════════════════════════════════
