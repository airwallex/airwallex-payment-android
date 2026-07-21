Feature: Card Payment - Error Scenarios & Edge Cases
  As a fintech SDK provider
  I want to handle all payment errors and edge cases gracefully
  So that users receive clear feedback and the system maintains data integrity

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR SCENARIO COVERAGE
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Current Status: 0% covered (CRITICAL GAP)
  #
  # These scenarios are CROSS-CUTTING - they apply to both:
  # - HPP (Hosted Payment Page / Native UI)
  # - Embedded Element
  #
  # Priority:
  # - P0 (Critical): Payment failures, validation, declines, timeout
  # - P1 (High): Session expiration
  # - P2 (Medium): Offline mode, multiple card selection
  #
  # ═══════════════════════════════════════════════════════════════════════════

  Background:
    Given the Airwallex Payment SDK is initialized
    And the environment is set to "DEMO"

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 1: PAYMENT RESULT - FAILED
  # Status: NOT COVERED - P0 CRITICAL
  # ═══════════════════════════════════════════════════════════════════════════

  @error @payment-failed @missing @P0-critical
  Scenario Outline: Payment fails due to processor rejection
    Given I use the "<integration>" integration
    And I am a "<user_type>" user
    And the checkout mode is "one-off"
    When I submit a payment
    And the payment processor rejects the transaction
    Then the payment result is "failed"
    And I see a payment failed error message
    And I can retry the payment
    And I can choose a different payment method

    Examples: Integration & User Type Variations
      | integration | user_type | notes                                  |
      | HPP         | guest     | Guest sees generic retry message       |
      | HPP         | auth      | Auth can retry or use saved cards      |
      | Embedded    | auth      | Embedded element error handling        |

    # MISSING: No test for PAYMENT_RESULT: FAILED
    # Current tests only cover: SUCCESS, CANCELLED
    # Priority: P0 - Untested code path in production
    # Risk: Failed payment flow may not work correctly
    # Estimated effort: 1 day

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 2: VALIDATION ERRORS
  # Status: NOT COVERED - P0 CRITICAL
  # ═══════════════════════════════════════════════════════════════════════════

  @error @validation @missing @P0-critical
  Scenario Outline: Card input validation prevents payment submission
    Given I use the "<integration>" integration
    And I am a guest user
    And the checkout mode is "one-off"
    When I navigate to the payment flow
    And I enter "<field>" as "<invalid_value>"
    And I attempt to submit the payment
    Then I see a validation error message
    And the error indicates "<error_type>"
    And the payment is not submitted
    And I can correct the input and retry

    Examples: Validation Error Cases
      | integration | field       | invalid_value | error_type              | error_message_example              |
      | HPP         | card number | 4111111       | Incomplete card number  | Card number is incomplete          |
      | HPP         | card number | 1234567890    | Invalid card number     | Invalid card number                |
      | HPP         | expiry date | 0123          | Invalid date format     | Invalid expiry date format         |
      | HPP         | expiry date | 01/20         | Card expired            | Card has expired                   |
      | HPP         | CVV         | 12            | CVV too short           | CVV must be 3 digits               |
      | HPP         | CVV         | 12345         | CVV too long            | CVV must be 3 digits               |
      | Embedded    | card number | 4111111       | Incomplete card number  | Card number is incomplete          |
      | Embedded    | expiry date | 01/20         | Card expired            | Card has expired                   |
      | Embedded    | CVV         | 12            | CVV too short           | CVV must be 3 digits               |

    # MISSING: No validation error tests exist
    # Priority: P0 - Users may see cryptic errors or broken validation
    # Risk: Poor user experience, customer frustration
    # Estimated effort: 2 days

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 3: PAYMENT PROCESSOR DECLINES (API 402)
  # Status: NOT COVERED - P0 CRITICAL
  # ═══════════════════════════════════════════════════════════════════════════

  @error @payment-declined @missing @P0-critical
  Scenario Outline: Payment processor declines with specific reason
    Given I use the "<integration>" integration
    And I am a guest user
    And the checkout mode is "one-off"
    When I enter valid card details
    And I submit the payment
    And the payment processor declines with reason "<decline_reason>"
    Then the payment result is "failed"
    And I see the user-friendly error message "<user_message>"
    And the technical error code "<error_code>" is logged for debugging
    And I can retry the payment
    And I can choose a different payment method

    Examples: Payment Decline Reasons
      | integration | decline_reason      | error_code | user_message                                      | frequency |
      | HPP         | insufficient_funds  | 402_001    | Payment declined due to insufficient funds        | Common    |
      | HPP         | card_declined       | 402_002    | Card declined. Please use a different card        | Common    |
      | HPP         | invalid_card_number | 402_003    | Invalid card number. Please check and try again   | Uncommon  |
      | HPP         | card_expired        | 402_004    | Card expired. Please use a different card         | Common    |
      | HPP         | cvc_check_failed    | 402_005    | CVV verification failed. Please check and retry   | Common    |
      | Embedded    | insufficient_funds  | 402_001    | Payment declined due to insufficient funds        | Common    |
      | Embedded    | card_declined       | 402_002    | Card declined. Please use a different card        | Common    |
      | Embedded    | cvc_check_failed    | 402_005    | CVV verification failed. Please check and retry   | Common    |

    # MISSING: No payment decline (402) tests exist
    # Priority: P0 - High support ticket volume without proper error messages
    # Risk: Users see generic errors instead of helpful guidance
    # Estimated effort: 2 days

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 4: NETWORK TIMEOUT
  # Status: NOT COVERED - P0 CRITICAL (FINANCIAL RISK)
  # ═══════════════════════════════════════════════════════════════════════════

  @error @network-timeout @missing @P0-critical
  Scenario Outline: Network timeout during payment with idempotency protection
    Given I use the "<integration>" integration
    And I am a "<user_type>" user
    And the checkout mode is "one-off"
    When I enter valid card details
    And I submit the payment
    And the network request times out after 30 seconds
    Then I see a network timeout error message
    And I see a "Retry Payment" button

    When I tap "Retry Payment"
    Then the payment is resubmitted
    And no duplicate transaction is created
    And the idempotency mechanism prevents double-charging

    Examples: Integration & User Type Variations
      | integration | user_type | notes                                  |
      | HPP         | guest     | Guest retries with same card details   |
      | HPP         | auth      | Auth can retry or use saved cards      |
      | Embedded    | auth      | Embedded timeout handling              |

    # MISSING: No network timeout test
    # Priority: P0 - FINANCIAL RISK: Potential double-charging
    # Risk: Users could be charged twice during network issues
    # Compliance: Payment industry requires idempotency protection
    # Estimated effort: 2 days

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 5: SESSION EXPIRATION
  # Status: NOT COVERED - P1 HIGH
  # ═══════════════════════════════════════════════════════════════════════════

  @error @session-expired @missing @P1-high
  Scenario Outline: Payment session expires and refreshes automatically
    Given I use the "<integration>" integration
    And I am an authenticated user
    And the checkout mode is "one-off"
    And I have initiated a payment flow
    And the payment session timeout is 15 minutes

    When I wait for 16 minutes without activity
    And I attempt to submit the payment
    Then I see a session expired error message
    And the SDK automatically refreshes the session token

    When the session is refreshed
    Then I can complete the payment successfully
    And no duplicate payment intent is created

    Examples: Integration Variations
      | integration | notes                              |
      | HPP         | HPP session refresh                |
      | Embedded    | Embedded element session refresh   |

    # MISSING: No session expiration test
    # Priority: P1 - Cart abandonment risk
    # Risk: Users lose their checkout progress after timeout
    # Estimated effort: 1 day

  # ═══════════════════════════════════════════════════════════════════════════
  # ERROR TYPE 6: OFFLINE MODE
  # Status: NOT COVERED - P2 MEDIUM
  # ═══════════════════════════════════════════════════════════════════════════

  @error @offline-mode @missing @P2-medium
  Scenario Outline: Payment attempt while device is offline
    Given I use the "<integration>" integration
    And I am a guest user
    And the checkout mode is "one-off"
    When I navigate to the payment flow
    And I enter valid card details
    And I lose internet connectivity
    And I attempt to submit the payment
    Then the SDK detects offline mode
    And I see an error message "No internet connection. Please check your network."
    And the payment data is not lost

    When internet connectivity is restored
    And I tap "Retry"
    Then the payment is submitted successfully

    Examples: Integration Variations
      | integration | notes                         |
      | HPP         | HPP offline detection         |
      | Embedded    | Embedded offline detection    |

    # MISSING: No offline mode test
    # Priority: P2 - Graceful error handling
    # Risk: Poor user experience during connectivity issues
    # Estimated effort: 1 day

  # ═══════════════════════════════════════════════════════════════════════════
  # EDGE CASE 1: MULTIPLE SAVED CARDS SELECTION
  # Status: PARTIALLY COVERED - P2 MEDIUM
  # ═══════════════════════════════════════════════════════════════════════════

  @edge-case @multiple-cards @missing @P2-medium
  Scenario Outline: Selecting between multiple saved cards
    Given I use the "<integration>" integration
    And I am an authenticated user
    And I have <card_count> saved cards
    When I navigate to the payment flow
    Then I see all <card_count> cards listed

    When I select the <card_position> card
    And I enter CVV
    And I submit the payment
    Then the payment is successful using the selected card

    Examples: Card Selection
      | integration | card_count | card_position | maestro_coverage            | notes                              |
      | HPP         | 1          | first         | ✓ flow_pay_with_consent     | Currently tested                   |
      | HPP         | 2          | second        | ✗ Not covered               | Need to test selecting 2nd card    |
      | HPP         | 3          | third         | ✗ Not covered               | Need to test selecting 3rd card    |
      | Embedded    | 1          | first         | ✓ flow_embedded_...         | Currently tested                   |
      | Embedded    | 2          | second        | ✗ Not covered               | Need to test selecting 2nd card    |

    # PARTIAL: Only first card selection is currently tested
    # Priority: P2 - Edge case but important for UX
    # Risk: Cannot verify that card selection works correctly for 2nd+ cards
    # Estimated effort: 1 day

  # ═══════════════════════════════════════════════════════════════════════════
  # EDGE CASE 2: 3DS NETWORK INTERRUPTION
  # Status: NOT COVERED - P2 MEDIUM
  # ═══════════════════════════════════════════════════════════════════════════

  @edge-case @3ds-interruption @missing @P2-medium
  Scenario: 3DS authentication network interruption
    Given I use the HPP integration
    And I am a guest user
    And I have initiated a 3DS authentication challenge
    When the network connection is lost during 3DS verification
    Then I see a connection error in the 3DS iframe
    And the SDK gracefully handles the 3DS failure
    And the payment is marked as "pending 3DS verification"
    And I can retry the 3DS challenge or cancel the payment

    # MISSING: No 3DS network failure test
    # Priority: P2 - 3DS is critical flow but interruptions are rare
    # Risk: Users stuck in broken 3DS state
    # Estimated effort: 1 day

  # ═══════════════════════════════════════════════════════════════════════════
  # COVERAGE SUMMARY - ERROR SCENARIOS
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # Total Scenarios: 8
  # Total Tests: 0 (0% coverage)
  #
  # By Priority:
  # - P0 Critical: 4 scenarios (0 tests) ❌
  # - P1 High: 1 scenario (0 tests) ❌
  # - P2 Medium: 3 scenarios (0 tests) ❌
  #
  # By Error Type:
  # - Payment failed: 0 tests
  # - Validation: 0 tests
  # - Declines (402): 0 tests
  # - Network timeout: 0 tests
  # - Session expiration: 0 tests
  # - Offline mode: 0 tests
  # - Multiple cards: 0 tests (1st card tested, 2nd+ not tested)
  # - 3DS interruption: 0 tests
  #
  # ═══════════════════════════════════════════════════════════════════════════
  # IMPLEMENTATION PRIORITY
  # ═══════════════════════════════════════════════════════════════════════════
  #
  # THIS WEEK (P0 - Critical):
  # 1. test_payment_failed.yaml             - Payment result: FAILED (1 day)
  # 2. test_card_validation_errors.yaml     - Validation errors (2 days)
  # 3. test_payment_declined_402.yaml       - Processor declines (2 days)
  # 4. test_network_timeout.yaml            - Timeout + idempotency (2 days)
  #
  # Total: 7 days (1.5 weeks)
  #
  # NEXT WEEK (P1 - High):
  # 5. test_session_expiration.yaml         - Session timeout (1 day)
  #
  # LATER (P2 - Medium):
  # 6. test_offline_mode.yaml               - Offline detection (1 day)
  # 7. test_multiple_card_selection.yaml    - Select 2nd/3rd card (1 day)
  # 8. test_3ds_network_interruption.yaml   - 3DS failure (1 day)
  #
  # ═══════════════════════════════════════════════════════════════════════════
