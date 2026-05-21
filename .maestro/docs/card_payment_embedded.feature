Feature: Card Payment - Embedded Element Integration
  As a merchant using Airwallex Embedded Elements
  I want to embed payment UI directly in my checkout flow
  So that customers experience a seamless, branded payment experience

  # ═══════════════════════════════════════════════════════════════════════════
  # INTEGRATION: EMBEDDED ELEMENT
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Embedded Element = Merchant-controlled payment UI
  # Elements are embedded directly in merchant's checkout page
  #
  # Coverage: 5 test scenarios
  # - One-off: 5 tests (single payment + lifecycle + express)
  # - Recurring: 0 tests (gap - not yet tested)
  # - Recurring+Payment: 0 tests (gap - not yet tested)
  #
  # ═══════════════════════════════════════════════════════════════════════════

  Background:
    Given the Airwallex Payment SDK is initialized
    And the environment is set to "DEMO"
    And I use the Embedded Element integration
    And I am an authenticated user

  # ═══════════════════════════════════════════════════════════════════════════
  # EMBEDDED MAIN SCENARIO 1/3: ONE-OFF PAYMENT
  # ═══════════════════════════════════════════════════════════════════════════

  @embedded @one-off @lifecycle @covered
  Scenario: Complete payment lifecycle via Embedded Element
    Given the checkout mode is "one-off"

    # First payment - save the card
    When I navigate to the embedded checkout
    Then I see the order details
    And I see the Google Pay option

    When I enter card details
    And I enable card saving
    And I submit the payment
    Then the payment is successful
    And the card is saved

    # Second payment - use saved card
    When I navigate to the embedded checkout again
    Then I see my saved cards

    When I select the saved card
    And I enter CVV
    And I submit the payment
    Then the payment is successful

    # Cleanup - delete saved card
    When I navigate to the embedded checkout again
    And I delete the saved card
    Then the card entry form is displayed
    And no saved cards remain

    # Maestro: flow_embedded_element_accordion_payment
    # Layout: Accordion
    # This tests the complete save → use → delete lifecycle

  @embedded @one-off @single-payment @covered
  Scenario Outline: Single payment via Embedded Element
    Given the checkout mode is "one-off"
    And the UI layout is "<layout>"
    When I navigate to the embedded checkout
    And I complete a payment <save_action>
    Then the payment is successful

    Examples: Layout & Card Saving Variations
      | layout    | save_action        | maestro_test                          | notes                       |
      | tab       | without saving     | test_embedded_element_tab_card        | Tab layout, no card saving  |
      | accordion | with card saving   | test_embedded_element_accordion_card  | Accordion layout, save card |
      | accordion | with card saving   | test_embedded_element_accordion_schema | Accordion + schema variant |

    # NOTE: Layout (tab vs accordion vs schema) is just UI rendering
    # Schema is a layout variant, not a different business flow

  @embedded @one-off @express-checkout @covered
  Scenario: Express checkout via Embedded Element
    Given the checkout mode is "one-off"
    And express checkout is enabled
    When I navigate to the embedded checkout
    Then I see the express checkout option

    When I use the express checkout flow
    Then the payment is successful

    # Maestro: test_embedded_element_express_checkout
    # Express checkout accelerates the payment flow for returning users

  # ═══════════════════════════════════════════════════════════════════════════
  # EMBEDDED MAIN SCENARIO 2/3: RECURRING CONSENT
  # Status: NOT COVERED - GAP
  # ═══════════════════════════════════════════════════════════════════════════

  @embedded @recurring @auth @missing @P2-medium
  Scenario Outline: Recurring consent via Embedded Element
    Given the checkout mode is "recurring"
    And the next action trigger is "<trigger_by>"
    When I navigate to the embedded checkout
    And I create a recurring consent
    Then the consent is created
    And duplicate handling follows "<trigger_by>" rules

    Examples: Trigger Type Variations
      | trigger_by | duplicate_handling  | priority | notes                              |
      | customer   | recurring: rejected | P2       | No tests exist for embedded recurring |
      | merchant   | deduplicated        | P2       | No tests exist for embedded recurring |

    # MISSING: No embedded element recurring tests
    # Priority: P2 - Feature gap but lower priority than error scenarios
    # Risk: Cannot verify embedded recurring flows work correctly

  # ═══════════════════════════════════════════════════════════════════════════
  # EMBEDDED MAIN SCENARIO 3/3: RECURRING + PAYMENT
  # Status: NOT COVERED - GAP
  # ═══════════════════════════════════════════════════════════════════════════

  @embedded @recurring-with-payment @auth @missing @P2-medium
  Scenario Outline: Recurring consent with payment via Embedded Element
    Given the checkout mode is "recurring and payment"
    And the next action trigger is "<trigger_by>"
    When I navigate to the embedded checkout
    And I create a recurring consent with initial payment
    Then the payment is successful
    And the consent is created

    Examples: Trigger Type Variations
      | trigger_by | priority | notes                                        |
      | customer   | P2       | No tests for embedded recurring+payment      |
      | merchant   | P2       | No tests for embedded recurring+payment      |

    # MISSING: No embedded element recurring+payment tests
    # Priority: P2 - Feature gap but lower priority than error scenarios
    # Risk: Cannot verify embedded recurring+payment flows work

  # ═══════════════════════════════════════════════════════════════════════════
  # EMBEDDED ELEMENT SPECIFIC FEATURES
  # ═══════════════════════════════════════════════════════════════════════════

  @embedded @ui-components @covered
  Scenario: Embedded Element UI components render correctly
    Given the checkout mode is "one-off"
    When I navigate to the embedded checkout
    Then I see the order details section
    And I see the Google Pay option with "Or pay with" divider
    And I see the card information section
    And I see the billing info section
    And I see "Same as shipping address" option
    And I see "Save this card for future payments" option
    And I see the Pay/Confirm button

    # This scenario verifies all UI components are present
    # Tested implicitly in: flow_embedded_element_accordion_payment

  @embedded @navigation @covered
  Scenario: User navigates away from Embedded Element
    Given the checkout mode is "one-off"
    When I navigate to the embedded checkout
    And I view the order details
    And I navigate back without completing payment
    Then I return to the main integration screen
    And no payment is processed

    # Maestro: flow_embedded_element_accordion_payment (back navigation at end)

  # ═══════════════════════════════════════════════════════════════════════════
  # COVERAGE SUMMARY - EMBEDDED ELEMENT
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Total Scenarios: 7 (3 covered, 4 missing)
  # Total Tests: 5
  #
  # By Checkout Mode:
  # - One-off: 5 tests ✅
  # - Recurring: 0 tests ❌ (gap)
  # - Recurring+Payment: 0 tests ❌ (gap)
  #
  # By User Type:
  # - Guest: 0 tests (by design - embedded typically for auth users)
  # - Authenticated: 5 tests
  #
  # By Layout:
  # - Tab: 1 test
  # - Accordion: 3 tests
  # - Schema: 1 test (variant of accordion)
  #
  # By Features:
  # - Card saving lifecycle: 1 test ✅
  # - Express checkout: 1 test ✅
  # - Google Pay integration: Verified in lifecycle test ✅
  # - Recurring: 0 tests ❌
  #
  # ═══════════════════════════════════════════════════════════════════════════
  # GAPS & RECOMMENDATIONS
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Priority: P2 - Medium
  # - Add embedded recurring consent tests (customer + merchant trigger)
  # - Add embedded recurring+payment tests (customer + merchant trigger)
  #
  # Why P2 (not P0)?
  # - HPP recurring flows are fully tested (same backend logic)
  # - Embedded is primarily used for one-off payments in practice
  # - Higher priority: Error scenarios (P0) affect all integrations
  #
  # ═══════════════════════════════════════════════════════════════════════════
