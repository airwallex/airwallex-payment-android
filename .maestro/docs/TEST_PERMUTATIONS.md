# Card Payment - Test Permutation Matrix

**Purpose**: At-a-glance view of all tested combinations (what we actually test, not what we don't test)

**Last Updated**: 2026-06-19

---

## ⚙️ **Default Values**

Tests use these defaults from `flow_update_settings.yaml` unless explicitly overridden:

| Variable | Default Value | Override Required For |
|----------|---------------|----------------------|
| `NEXT_TRIGGERED_BY` | `Customer` | Merchant-triggered recurring tests |
| `EXPRESS_CHECKOUT` | `false` | Express checkout enabled tests |
| `LAYOUT` | `Tab` | Accordion/Schema layout tests |
| `FORCE_3DS` | `false` | 3DS required tests |
| `IS_GUEST` | `true` | Authenticated user tests |
| `ENVIRONMENT` | `DEMO` | Production/Staging tests |
| `GENERATE_NEW_CUSTOMER_ID` | `false` | New customer generation |
| `USE_NEW_SESSION` | `false` | Session-based tests |

---

## 🔄 **Session Type: Legacy vs New Session**

**What is Session Type?**
- **Infrastructure setup** that affects backend session management
- **Legacy Session** (`USE_NEW_SESSION = false`, default): Original session implementation
- **New Session** (`USE_NEW_SESSION = true`): Modern session implementation

**Behavioral Differences**:
| Behavior | Legacy Session | New Session |
|----------|----------------|-------------|
| **Recurring mode UI** | Shows card entry only | Shows list of existing payment consents |
| **Duplicate consent (customer-triggered)** | Shows error (duplicate rejected) | Allows re-adding same card |
| **Everything else** | Same | Same |

**Coverage Rule**:
- ✅ **Must test both** Legacy AND New Session for all meaningful scenarios
- ❌ **Skip New Session** for UI-only variations:
  - Layout duplicates (Tab vs Accordion)
  - 3DS type variations (Combined vs Challenge vs Data Collection)

**In the table below:**
- **Legacy Test** column = Default session (`USE_NEW_SESSION = false`)
- **New Session Test** column = New session (`USE_NEW_SESSION = true`)
- `-` means not needed for New Session (UI-only or pending implementation)
- Most tests use default values (see table above) unless explicitly noted
- `N/A` means not applicable for that particular test scenario

---

## 📊 **Complete Test Permutation Table**

**Session Type Coverage**: Most tests have both Legacy and New Session versions. New Session is skipped only for UI-only variations (layout duplicates, 3DS type variations).

| Integration | User Type | Checkout Mode | 3DS Required | Save Card | Trigger By | Express Checkout | Layout | Scenario | Legacy Test | New Session Test |
|-------------|-----------|---------------|--------------|-----------|------------|------------------|--------|----------|--------------|------------------|
| HPP | Guest | one-off | No | N/A | Customer | No | Tab | Guest pays without 3DS | test_guest_3ds_none | - |
| HPP | Guest | one-off | Yes | N/A | Customer | No | Tab | Guest pays with 3DS combined | test_guest_3ds_combined | - |
| HPP | Guest | one-off | Yes | N/A | Customer | No | Accordion | Guest pays with 3DS accordion layout | test_guest_3ds_combined_accordion | - (UI only) |
| HPP | Guest | one-off | Yes | N/A | Customer | No | Tab | Guest pays with 3DS challenge | test_guest_3ds_challenge | - (3DS type) |
| HPP | Guest | one-off | Yes | N/A | Customer | No | Tab | Guest pays with 3DS data collection | test_guest_3ds_data_collection | - (3DS type) |
| HPP | Guest | one-off | Yes | N/A | Customer | No | Tab | Guest cancels 3DS challenge | test_guest_payment_cancel_3ds | - |
| HPP | Guest | one-off | N/A | N/A | Customer | No | Tab | Guest cancels at payment method selection | test_guest_payment_cancel | - |
| HPP | Auth | one-off | Yes | Enabled (saves) | Customer | No | Tab | User saves card during payment | test_user_card_saving_tab | - |
| HPP | Auth | one-off | Yes | Enabled (saves) | Customer | No | Accordion | User saves card during payment (accordion) | test_user_card_saving_accordion | - (UI only) |
| HPP | Auth | one-off | Yes | Enabled (doesn't save) | Customer | No | Tab | User can save but doesn't | test_user_oneoff_payment | - |
| HPP | Auth | one-off | Yes | Uses saved card | Customer | No | Tab | User pays with saved card | flow_pay_with_consent | - |
| HPP | Auth | one-off | N/A | Deletes all cards | Customer | No | Tab | User deletes all saved cards | flow_remove_all_consents | - |
| HPP | Auth | recurring | No | Auto-saved | Customer | No | Tab | Customer-triggered recurring consent (no payment) | test_user_recurring_trigger_by_customer | Session/test_user_recurring_trigger_by_customer |
| HPP | Auth | recurring+payment | No | Auto-saved | Customer | No | Tab | Customer-triggered recurring + initial payment | test_user_recurring_with_payment_trigger_by_customer | Session/test_user_recurring_with_payment_trigger_by_customer |
| HPP | Auth | recurring | No | Auto-saved | Merchant | No | Tab | Merchant-triggered recurring consent (no payment) | test_user_recurring_trigger_by_merchant | Session/test_user_recurring_trigger_by_merchant |
| HPP | Auth | recurring+payment | No | Auto-saved | Merchant | No | Tab | Merchant-triggered recurring + initial payment | test_user_recurring_with_payment_trigger_by_merchant | Session/test_user_recurring_with_payment_trigger_by_merchant |
| HPP | Guest | one-off | Yes | N/A | Customer | Yes | Tab | Guest express checkout with 3DS | test_guest_express_checkout | - |
| HPP | Auth | one-off | Yes | Enabled (saves) | Customer | Yes | Tab | Auth express checkout saves card | test_user_express_checkout_oneoff | - |
| HPP | Auth | recurring+payment | Yes | Auto-saved | Customer | Yes | Tab | Auth express recurring+payment customer | test_user_express_checkout_recurring_with_payment_customer | Session/test_user_express_checkout_recurring_with_payment_customer |
| HPP | Auth | recurring+payment | Yes | Auto-saved | Merchant | Yes | Tab | Auth express recurring+payment merchant | test_user_express_checkout_recurring_with_payment_merchant | Session/test_user_express_checkout_recurring_with_payment_merchant |
| HPP | Guest | one-off | N/A | N/A | Customer | No | Tab | New-card form renders only merchant-configured billing fields (UI-only, no payment; 8 cycles cover unset/empty/each-field-alone/ADDRESS+COUNTRY_CODE mutex) | test_hpp_billing_fields_visibility | - (UI only) |
| HPP | Guest | one-off | N/A | N/A | Customer | No | Tab | AddressSpec country switch rewires billing block (UI-only, no payment; 3 cycles: US dropdown + ZIP, UK Town + state hidden, AO postcode hidden). Anchor on US state list top entry "Alabama". | test_hpp_country_billing_fields | - (UI only) |
| Embedded | Auth | one-off | No | Disabled | Customer | No | Tab | Embedded tab single payment | test_embedded_element_tab_card | - |
| Embedded | Auth | one-off | No | Enabled (saves) | Customer | No | Accordion | Embedded accordion single payment | test_embedded_element_accordion_card | Session/test_embedded_element_accordion_card |
| Embedded | Auth | one-off | No | Enabled (saves) | Customer | No | Accordion + Schema | Embedded accordion schema variant | test_embedded_element_accordion_schema | Session/test_embedded_element_accordion_schema |
| Embedded | Auth | one-off | No | Lifecycle (save → use → delete) | Customer | No | Accordion | Embedded complete lifecycle | flow_embedded_element_accordion_payment | - |
| Embedded | Auth | one-off | No | N/A | Customer | Yes | N/A | Embedded express checkout | test_embedded_element_express_checkout | Session/test_embedded_element_express_checkout |
| API | Guest | one-off | Yes | N/A | Customer | Yes | N/A | Guest API express checkout with 3DS | test_guest_express_checkout_api_integration | - |
| API | Guest | one-off | No | N/A | Customer | No | N/A | Guest pays with card (no save) | Api/test_api_guest_oneoff | - |
| API | Guest | one-off | Yes | N/A | Customer | No | N/A | Guest pays with 3DS card (no save) | Api/test_api_guest_oneoff | - |
| API | Guest | one-off | No | N/A | Customer | No | N/A | Guest redirect payment (Pay Now) | Api/test_api_guest_oneoff | - |
| API | Auth | one-off | No | No | Customer | No | N/A | Customer pays with card, does not save | Api/test_api_customer_oneoff | Api/test_api_customer_oneoff |
| API | Auth | one-off | No | Yes (saves) | Customer | No | N/A | Customer pays with card and saves | Api/test_api_customer_oneoff | Api/test_api_customer_oneoff |
| API | Auth | one-off | Yes | Yes (saves) | Customer | No | N/A | Customer pays with 3DS card (auto-saved) | Api/test_api_customer_oneoff | Api/test_api_customer_oneoff |
| API | Auth | one-off | No | N/A | Customer | No | N/A | Customer redirect payment (Pay Now) | Api/test_api_customer_oneoff | Api/test_api_customer_oneoff |
| API | Auth | recurring | No | Auto | Customer | No | N/A | Customer creates consent (Visa 0008) | Api/test_api_customer_recurring | Api/test_api_customer_recurring |
| API | Auth | recurring | No | Auto | Customer | No | N/A | Customer duplicate consent rejected | Api/test_api_customer_recurring | Api/test_api_customer_recurring |
| API | Auth | recurring | Yes | Auto | Customer | No | N/A | Customer creates 3DS consent (MC 4518) | Api/test_api_customer_recurring | Api/test_api_customer_recurring |
| API | Auth | recurring | No | N/A | Customer | No | N/A | Customer redirect payment (Authorize Now) | Api/test_api_customer_recurring | Api/test_api_customer_recurring |
| API | Auth | recurring+payment | No | Auto | Customer | No | N/A | Customer consent+payment (Visa 1003) | Api/test_api_customer_recurring_with_payment | Api/test_api_customer_recurring_with_payment |
| API | Auth | recurring+payment | No | Auto | Customer | No | N/A | Customer duplicate consent allowed | Api/test_api_customer_recurring_with_payment | Api/test_api_customer_recurring_with_payment |
| API | Auth | recurring+payment | Yes | Auto | Customer | No | N/A | Customer 3DS consent+payment (Visa 0088) | Api/test_api_customer_recurring_with_payment | Api/test_api_customer_recurring_with_payment |
| API | Auth | recurring+payment | No | N/A | Customer | No | N/A | Customer redirect payment (Authorize Now) | Api/test_api_customer_recurring_with_payment | Api/test_api_customer_recurring_with_payment |
| API | Auth | recurring | No | Auto | Merchant | No | N/A | Merchant creates consent (Visa 0008) | Api/test_api_merchant_recurring | Api/test_api_merchant_recurring |
| API | Auth | recurring | No | Auto | Merchant | No | N/A | Merchant duplicate consent allowed | Api/test_api_merchant_recurring | Api/test_api_merchant_recurring |
| API | Auth | recurring | Yes | Auto | Merchant | No | N/A | Merchant creates 3DS consent (MC 4518) | Api/test_api_merchant_recurring | Api/test_api_merchant_recurring |
| API | Auth | recurring | No | N/A | Merchant | No | N/A | Merchant redirect payment (Authorize Now) | Api/test_api_merchant_recurring | Api/test_api_merchant_recurring |
| API | Auth | recurring+payment | No | Auto | Merchant | No | N/A | Merchant consent+payment (Visa 1003) | Api/test_api_merchant_recurring_with_payment | Api/test_api_merchant_recurring_with_payment |
| API | Auth | recurring+payment | No | Auto | Merchant | No | N/A | Merchant duplicate consent allowed | Api/test_api_merchant_recurring_with_payment | Api/test_api_merchant_recurring_with_payment |
| API | Auth | recurring+payment | Yes | Auto | Merchant | No | N/A | Merchant 3DS consent+payment (Visa 0088) | Api/test_api_merchant_recurring_with_payment | Api/test_api_merchant_recurring_with_payment |
| API | Auth | recurring+payment | No | N/A | Merchant | No | N/A | Merchant redirect payment (Authorize Now) | Api/test_api_merchant_recurring_with_payment | Api/test_api_merchant_recurring_with_payment |

**Total Test Permutations: 51** (HPP: 22, Embedded: 5, API: 24)  
**Total Test Files: 48** (34 legacy + 14 new session)  
**Note**: API test files contain multiple scenarios each (3-4 per file)

---

## 📊 **Permutation Summary by Hierarchy**

### **Level 1: Integration Type**

| Integration | Test Count | % of Total |
|-------------|------------|------------|
| HPP (Native UI) | 20 tests | 63% |
| Embedded Element | 5 tests | 16% |
| API (Low-level) | 7 tests | 22% |

**Note**: Express Checkout (`EXPRESS_CHECKOUT = Yes`) is tested within HPP (4 tests), Embedded (1 test), and API (1 test).

---

### **Level 2: Checkout Mode**

| Integration | One-off | Recurring | Recurring+Payment |
|-------------|---------|-----------|-------------------|
| HPP | 14 tests | 2 tests | 4 tests |
| Embedded | 5 tests | 0 tests | 0 tests |
| API | 8 scenarios | 8 scenarios | 8 scenarios |

**Note**: Embedded recurring flows are not yet implemented. API scenarios are grouped within 7 test files (each file covers 3-4 scenarios).

---

### **Level 3: User Type**

| Integration | Guest | Authenticated |
|-------------|-------|---------------|
| HPP | 8 tests | 12 tests |
| Embedded | 0 tests | 5 tests |
| API | 3 scenarios | 21 scenarios |

**Note**: No guest embedded element tests (embedded typically used by authenticated users).

---

### **Level 4: Details/Modifiers**

#### **3DS Coverage**

| 3DS Type | Test Count | Integration |
|----------|------------|-------------|
| No 3DS | 3 tests | HPP |
| Combined 3DS | 2 tests | HPP |
| Challenge 3DS | 1 test | HPP |
| Data Collection 3DS | 1 test | HPP |
| 3DS Cancelled | 1 test | HPP |
| Embedded (no 3DS tests) | 0 tests | Embedded |

**Consolidation Opportunity**: 4 different 3DS types test the same code path. Could reduce to 2 tests (success + cancel).

---

#### **Card Saving Coverage**

| Behavior | Test Count | Integration |
|----------|------------|-------------|
| Cannot save (guest) | 7 tests | HPP |
| Can save + does | 4 tests | HPP + Embedded |
| Can save + doesn't | 1 test | HPP |
| Uses saved card | 1 test | HPP |
| Deletes cards | 1 test | HPP |
| Lifecycle (save→use→delete) | 1 test | Embedded |
| Auto-saved (recurring) | 4 tests | HPP |

---

#### **Payment Trigger Coverage (Recurring Only)**

| Trigger Type | Recurring | Recurring+Payment | Integration |
|--------------|-----------|-------------------|-------------|
| Customer | 1 test | 1 test | HPP |
| Merchant | 1 test | 1 test | HPP |

---

#### **Layout Coverage**

| Layout | HPP Tests | Embedded Tests |
|--------|-----------|----------------|
| Tab | 10 tests | 1 test |
| Accordion | 2 tests | 3 tests |
| Schema | 0 tests | 1 test |

**Note**: Layout is just UI rendering - don't need all permutations.

---

#### **Express Checkout Coverage**

| Express Checkout | Test Count | Details |
|------------------|------------|---------|
| No | 20 tests | HPP (16) + Embedded (4) |
| Yes | 6 tests | HPP (4) + Embedded (1) + API (1) |

**Express Checkout Breakdown**:
- **HPP**: 4 tests (guest one-off, auth one-off, auth recurring+payment customer, auth recurring+payment merchant)
- **Embedded**: 1 test (auth one-off)
- **API**: 1 test (guest one-off with 3DS)

---

## 🎯 **Test Distribution Heatmap**

### **HPP Integration (16 tests)**

| Checkout Mode | Guest | Auth + Save | Auth + Saved Card | Auth + Recurring |
|---------------|-------|-------------|-------------------|------------------|
| One-off | 7 tests | 3 tests | 2 tests | - |
| Recurring | - | - | - | 2 tests |
| Recurring+Payment | - | - | - | 2 tests |

### **Embedded Integration (5 tests)**

| Checkout Mode | Single Payment | Lifecycle | Express |
|---------------|----------------|-----------|---------|
| One-off | 3 tests | 1 test | 1 test |
| Recurring | 0 tests | 0 tests | 0 tests |
| Recurring+Payment | 0 tests | 0 tests | 0 tests |

---

## 🔍 **Coverage Insights**

### **Well-Covered Combinations** ✅

1. **HPP × One-off × Guest** → 7 tests (all 3DS variations + cancellations)
2. **HPP × One-off × Auth × Save card** → 3 tests (tab, accordion, enable/disable)
3. **HPP × Recurring × Auth** → 4 tests (customer/merchant × recurring/recurring+payment)
4. **Embedded × One-off × Auth** → 5 tests (layouts, lifecycle, express)

---

### **Under-Covered Combinations** ⚠️

1. **Embedded × Recurring** → 0 tests (gap: no embedded recurring flows)
2. **Embedded × Guest** → 0 tests (by design: embedded typically for auth users)
3. **HPP × One-off × Auth × Multiple saved cards** → Only 1st card selection tested
4. **Embedded × 3DS** → 0 tests (gap: no 3DS in embedded element tests)

---

### **Over-Tested Combinations** (Consolidation Opportunities) 🔄

1. **Guest × 3DS types** → 4 tests for same code path (combined, challenge, data collection, none)
   - **Recommendation**: Keep 2 tests (3DS success + 3DS cancel)
   - **Save**: 2 tests

2. **Layout variations** → Tab tested 11 times, accordion 5 times
   - **Recommendation**: 1 tab + 1 accordion per main scenario
   - **Save**: ~6 tests

---

## 📋 **Variable Hierarchy Reference**

```
Level 1: Integration Type
├─ HPP (Hosted Payment Page / Native UI)
└─ Embedded Element

Level 2: Checkout Mode
├─ One-off payment
├─ Recurring consent
└─ Recurring + payment

Level 3: User Type
├─ Guest (one-off only)
└─ Authenticated (all modes)

Level 4: Details/Modifiers (don't change core flow)
├─ Session Type: Legacy/New (test both systematically)
├─ 3DS Required: Yes/No
├─ Save Card: Enabled/Disabled/Auto-saved/Uses saved
├─ Trigger By: Customer/Merchant (recurring only, test both)
├─ Express Checkout: Yes/No
├─ Layout: Tab/Accordion/Schema (UI rendering, 1 of each sufficient)
└─ Payment Result: Success/Cancelled/Failed
```

---

## 🚨 **Missing Permutations (P0 - Critical)**

These are **untested but important** combinations:

| Integration | User Type | Checkout Mode | Missing Aspect | Priority | Impact |
|-------------|-----------|---------------|----------------|----------|--------|
| HPP | Guest | one-off | Payment result: FAILED | P0 | Untested code path |
| HPP | Auth | one-off | Payment result: FAILED | P0 | Untested code path |
| HPP | Guest | one-off | Card validation errors | P0 | Poor UX |
| HPP | Guest | one-off | Payment decline (402) | P0 | High support volume |
| HPP | Guest | one-off | Network timeout | P0 | Double-charging risk |
| HPP | Auth | one-off | Session expiration | P1 | Cart abandonment |
| HPP | Auth | one-off | Select 2nd/3rd saved card | P2 | Edge case |
| Embedded | Auth | recurring | Any test | P2 | Feature gap |
| Embedded | Auth | recurring+payment | Any test | P2 | Feature gap |

---

## 📊 **Export-Friendly Summary Table**

| # | Integration | User | Mode | 3DS | Save | Trigger | Express | Layout | Result | Scenario | File |
|---|-------------|------|------|-----|------|---------|---------|--------|--------|----------|------|
| 1 | HPP | Guest | one-off | No | - | Customer | No | Tab | ✓ | Guest no 3DS | test_guest_3ds_none |
| 2 | HPP | Guest | one-off | Yes | - | Customer | No | Tab | ✓ | Guest 3DS combined | test_guest_3ds_combined |
| 3 | HPP | Guest | one-off | Yes | - | Customer | No | Accordion | ✓ | Guest 3DS accordion | test_guest_3ds_combined_accordion |
| 4 | HPP | Guest | one-off | Yes | - | Customer | No | Tab | Cancelled | Guest cancels 3DS | test_guest_payment_cancel_3ds |
| 5 | HPP | Guest | one-off | - | - | Customer | No | Tab | Cancelled | Guest cancels payment | test_guest_payment_cancel |
| 6 | HPP | Auth | one-off | Yes | Yes | Customer | No | Tab | ✓ | User saves card | test_user_card_saving_tab |
| 7 | HPP | Auth | one-off | Yes | Yes | Customer | No | Accordion | ✓ | User saves card | test_user_card_saving_accordion |
| 8 | HPP | Auth | one-off | Yes | No | Customer | No | Tab | ✓ | User doesn't save | test_user_oneoff_payment |
| 9 | HPP | Auth | one-off | Yes | Saved | Customer | No | Tab | ✓ | User pays with saved | flow_pay_with_consent |
| 10 | HPP | Auth | one-off | - | Delete | Customer | No | Tab | Cancelled | User deletes cards | flow_remove_all_consents |
| 11 | HPP | Auth | recurring | No | Auto | Customer | No | Tab | ✓ | Customer recurring | test_user_recurring_trigger_by_customer |
| 12 | HPP | Auth | recurring+pay | No | Auto | Customer | No | Tab | ✓ | Customer recurring+pay | test_user_recurring_with_payment_trigger_by_customer |
| 13 | HPP | Auth | recurring | No | Auto | Merchant | No | Tab | ✓ | Merchant recurring | test_user_recurring_trigger_by_merchant |
| 14 | HPP | Auth | recurring+pay | No | Auto | Merchant | No | Tab | ✓ | Merchant recurring+pay | test_user_recurring_with_payment_trigger_by_merchant |
| 15 | HPP | Guest | one-off | Yes | - | Customer | Yes | Tab | ✓ | Guest express 3DS | test_guest_express_checkout |
| 16 | HPP | Auth | one-off | Yes | Yes | Customer | Yes | Tab | ✓ | Auth express saves | test_user_express_checkout_oneoff |
| 17 | HPP | Auth | recurring+pay | Yes | Auto | Customer | Yes | Tab | ✓ | Auth express recur customer | test_user_express_checkout_recurring_with_payment_customer |
| 18 | HPP | Auth | recurring+pay | Yes | Auto | Merchant | Yes | Tab | ✓ | Auth express recur merchant | test_user_express_checkout_recurring_with_payment_merchant |
| 19 | Embedded | Auth | one-off | No | No | Customer | No | Tab | ✓ | Embedded tab | test_embedded_element_tab_card |
| 20 | Embedded | Auth | one-off | No | Yes | Customer | No | Accordion | ✓ | Embedded accordion | test_embedded_element_accordion_card |
| 21 | Embedded | Auth | one-off | No | Yes | Customer | No | Accordion | ✓ | Embedded schema | test_embedded_element_accordion_schema |
| 22 | Embedded | Auth | one-off | No | Lifecycle | Customer | No | Accordion | ✓ | Embedded lifecycle | flow_embedded_element_accordion_payment |
| 23 | Embedded | Auth | one-off | No | - | Customer | Yes | - | ✓ | Embedded express | test_embedded_element_express_checkout |
| 24 | API | Guest | one-off | Yes | - | Customer | Yes | N/A | ✓ | Guest API express 3DS | test_guest_express_checkout_api_integration |
| 25 | API | Guest | one-off | No | - | Customer | No | N/A | ✓ | Guest API card payment | Api/test_api_guest_oneoff |
| 26 | API | Guest | one-off | Yes | - | Customer | No | N/A | ✓ | Guest API 3DS payment | Api/test_api_guest_oneoff |
| 27 | API | Guest | one-off | No | - | Customer | No | N/A | ✓ | Guest API redirect (Pay Now) | Api/test_api_guest_oneoff |
| 28 | API | Auth | one-off | No | No | Customer | No | N/A | ✓ | Customer API card no save | Api/test_api_customer_oneoff |
| 29 | API | Auth | one-off | No | Yes | Customer | No | N/A | ✓ | Customer API card + save | Api/test_api_customer_oneoff |
| 30 | API | Auth | one-off | Yes | Yes | Customer | No | N/A | ✓ | Customer API 3DS (auto-saved) | Api/test_api_customer_oneoff |
| 31 | API | Auth | one-off | No | - | Customer | No | N/A | ✓ | Customer API redirect (Pay Now) | Api/test_api_customer_oneoff |
| 32 | API | Auth | recurring | No | Auto | Customer | No | N/A | ✓ | Customer API consent created | Api/test_api_customer_recurring |
| 33 | API | Auth | recurring | No | Auto | Customer | No | N/A | ✗ | Customer API dup consent rejected | Api/test_api_customer_recurring |
| 34 | API | Auth | recurring | Yes | Auto | Customer | No | N/A | ✓ | Customer API 3DS consent | Api/test_api_customer_recurring |
| 35 | API | Auth | recurring | No | - | Customer | No | N/A | ✓ | Customer API redirect (Authorize Now) | Api/test_api_customer_recurring |
| 36 | API | Auth | recurring+pay | No | Auto | Customer | No | N/A | ✓ | Customer API consent+payment | Api/test_api_customer_recurring_with_payment |
| 37 | API | Auth | recurring+pay | No | Auto | Customer | No | N/A | ✓ | Customer API dup consent allowed | Api/test_api_customer_recurring_with_payment |
| 38 | API | Auth | recurring+pay | Yes | Auto | Customer | No | N/A | ✓ | Customer API 3DS consent+payment | Api/test_api_customer_recurring_with_payment |
| 39 | API | Auth | recurring+pay | No | - | Customer | No | N/A | ✓ | Customer API redirect (Authorize Now) | Api/test_api_customer_recurring_with_payment |
| 40 | API | Auth | recurring | No | Auto | Merchant | No | N/A | ✓ | Merchant API consent created | Api/test_api_merchant_recurring |
| 41 | API | Auth | recurring | No | Auto | Merchant | No | N/A | ✓ | Merchant API dup consent allowed | Api/test_api_merchant_recurring |
| 42 | API | Auth | recurring | Yes | Auto | Merchant | No | N/A | ✓ | Merchant API 3DS consent | Api/test_api_merchant_recurring |
| 43 | API | Auth | recurring | No | - | Merchant | No | N/A | ✓ | Merchant API redirect (Authorize Now) | Api/test_api_merchant_recurring |
| 44 | API | Auth | recurring+pay | No | Auto | Merchant | No | N/A | ✓ | Merchant API consent+payment | Api/test_api_merchant_recurring_with_payment |
| 45 | API | Auth | recurring+pay | No | Auto | Merchant | No | N/A | ✓ | Merchant API dup consent allowed | Api/test_api_merchant_recurring_with_payment |
| 46 | API | Auth | recurring+pay | Yes | Auto | Merchant | No | N/A | ✓ | Merchant API 3DS consent+payment | Api/test_api_merchant_recurring_with_payment |
| 47 | API | Auth | recurring+pay | No | - | Merchant | No | N/A | ✓ | Merchant API redirect (Authorize Now) | Api/test_api_merchant_recurring_with_payment |

**Total: 47 unique test scenarios** (32 legacy test files + 14 new session test files)  
**Note**: API test files contain multiple scenarios each (3-4 per file)

**Notes**:
- This table shows **Legacy Session** tests only (default `USE_NEW_SESSION = false`)
- See main table above for **New Session** test coverage
- Most tests use default values from `flow_update_settings.yaml` (see table at top)
- `-` = Not applicable for this test scenario
- API integration tests use "Integrate with low-level API" (custom merchant UI)
- API rows with same file = scenarios tested within a single test flow

---

## 🎯 **How to Use This Table**

### **For Planning**:
- See all tested permutations at a glance
- Identify gaps (e.g., no Embedded recurring tests)
- Spot over-testing (e.g., 4 guest 3DS tests for same code path)

### **For Test Execution**:
- Filter by Integration (`HPP` or `Embedded`)
- Filter by User Type (`Guest` or `Auth`)
- Filter by Checkout Mode (`one-off`, `recurring`, `recurring+payment`)

### **For Reporting**:
- Export to Excel/CSV for stakeholder review
- Show coverage heatmap to identify priorities
- Track progress on missing P0 scenarios

---

**Last Updated**: 2026-06-19  
**Next Review**: After implementing P0 missing scenarios  
**Maintained By**: QA Architecture Team
