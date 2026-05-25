# Maestro Test Authoring Rules

> **Source-of-truth for `/maestro-author` skill decisions.**
> Trust this file when it conflicts with anything else in this folder.
>
> Last updated: 2026-05-14

---

## 1. Coverage hierarchy

Three levels organise test scope:

| Level | Dimension | Examples |
|-------|-----------|----------|
| **L1** | Integration (HOW the merchant integrates) | HPP, Embedded, low-level API |
| **L2** | Checkout Mode (WHAT the customer is doing) | One-off, Recurring, Recurring+Payment |
| **L3+** | Details / Modifiers within a scenario | User Type, 3DS, Save Card, Trigger By, Layout, etc. |

When deciding where a new test belongs:
- **New L1 (e.g. GooglePay)** → new `*.feature` file
- **New L2 (e.g. new checkout mode in HPP)** → new `Scenario` in existing feature file
- **New L3 variation** → new row in an existing `Scenario Outline`'s `Examples:` table

### Current surface (card payments)

```
Card Payment Testing
│
├─ HPP (Hosted Payment Page / Native UI)
│  │   Airwallex-controlled UI. Also called: "Launch payment list", "Launch card payment".
│  ├─ One-Off Payment
│  │  ├─ Guest: 3DS success / 3DS cancelled / cancel at method-selection
│  │  └─ Auth: save+saves / save+doesn't / uses saved / deletes saved
│  ├─ Recurring Consent (auth only)
│  │  ├─ Customer-triggered → duplicate rejected
│  │  └─ Merchant-triggered → deduplicated, deletion-protected
│  └─ Recurring + Payment (auth only)
│     ├─ Customer-triggered → duplicate allowed
│     └─ Merchant-triggered → deduplicated, deletion-protected
│
├─ Low-level API ("Integrate with low-level API")
│  │   Merchant calls SDK methods directly from a custom screen.
│  ├─ One-Off (Guest + Customer): pay w/ card (no save / save), pay w/ card + 3DS, redirect (Pay Now)
│  ├─ Recurring Consent (Customer + Merchant): first card → consent (merchant forces 3DS), duplicate Customer→rejected / Merchant→allowed, 3DS card, redirect (Authorize Now, no success popup)
│  └─ Recurring + Payment (Customer + Merchant): first card → consent + payment, duplicate allowed for both, 3DS card, redirect (Authorize Now)
│
├─ Embedded Element
│  │   Merchant-controlled UI embedded in checkout page.
│  ├─ One-Off (auth): Tab no-save, Accordion+save, Accordion+Schema, Save→Use→Delete lifecycle, Express Checkout
│  ├─ Recurring Consent — NOT TESTED (P2 gap)
│  └─ Recurring + Payment — NOT TESTED (P2 gap)
│
└─ Error Scenarios (cross-cutting, apply to both integrations) — 0 tests, P0 gap
   payment FAILED, validation errors, declines (402), network timeout,
   session expiration, offline, multi-saved-card selection, 3DS network interrupt
```

For test counts and Maestro-file mapping per row, see `TEST_PERMUTATIONS.md`.

---

## 2. Variables that **need full permutation coverage** (test all values)

These create different code paths in the SDK or backend. Skip any of them and you leave behavior untested.

| Variable | Values |
|---|---|
| **User Type** | Guest, Authenticated |
| **Checkout Mode** | One-off, Recurring, Recurring+Payment |
| **Session Type** | Legacy (`USE_NEW_SESSION=false`), New (`USE_NEW_SESSION=true`) |
| **Payment Trigger** *(recurring only)* | Customer, Merchant |
| **Card Saving** | Cannot, Can+does, Can+doesn't, Auto-saved, Uses-saved |
| **3DS Outcome** | Success, Cancelled |
| **Payment Result** | Success, Cancelled, Failed |
| **Express Checkout** *(when relevant)* | Yes, No |

**Session Type exception:** skip the New-Session twin when the only difference between two scenarios is UI-only (e.g. tab vs accordion of the same flow). Test both for everything else.

---

## 3. Variables that are **UI/data only** (1–2 examples sufficient)

These don't change code paths — extra coverage just slows the suite.

| Variable | Recommendation |
|---|---|
| **Layout** (Tab / Accordion / Schema) | 1 tab + 1 accordion; Schema once if you want to verify the variant renders |
| **Card numbers** | Pick one per "behavior class" — e.g. one no-3DS card, one 3DS card. Don't write a variant per PAN. |
| **3DS Types** (Combined / Challenge / Data Collection) | Same SDK code path. **One success test + one cancel test is enough.** The existing 4 guest 3DS tests are over-coverage and could be consolidated to 2. |
| **Customer IDs** | The literal value doesn't matter; just needs to exist when `IS_GUEST=false` |

---

## 4. Anti-patterns (don't do these)

- ❌ Don't add tab + accordion variants of the same scenario (UI-only). One of each in the whole suite is fine.
- ❌ Don't add 3 or 4 different 3DS-type variants of the same scenario.
- ❌ Don't write a separate test for each test card number when the only difference is the PAN.
- ❌ Don't put logic in `test_*.yaml`. Tests should be thin parameter-passers; flows hold logic.
- ❌ Don't make a strict assertion tolerant just to make a flaky run pass — that masks real bugs (we did this twice in the 3DS and redirect helpers; both reverted). Only relax when the variability is **by-design and you can state why**.

---

## 5. Flow-vs-Test architecture (preserve this pattern)

```
flow_*.yaml   — reusable building blocks containing the actual logic
test_*.yaml   — thin wrappers that pass parameters to flows
```

Example:

```yaml
# flow_guest_card_payment.yaml — reusable
- runFlow:
    file: flow_pay_with_card.yaml
    env:
      CARD_NUMBER: ${CARD_NUMBER}
      HANDLE_3DS: ${HANDLE_3DS}

# test_guest_3ds_combined.yaml — thin wrapper, sets parameters
- runFlow:
    file: flow_guest_card_payment.yaml
    env:
      CARD_NUMBER: ${output.cards.visa3DS}
      HANDLE_3DS: true
```

When authoring a new test, look for an existing flow you can parameterize before creating a new flow.

---

## 6. Default settings (from `.maestro/Common/flow_update_settings.yaml`)

Tests inherit these unless they override via `env:`:

| Variable | Default |
|---|---|
| `ENVIRONMENT` | **`DEMO`** |
| `NEXT_TRIGGERED_BY` | `Customer` |
| `LAYOUT` | `Tab` |
| `FORCE_3DS` | `false` |
| `EXPRESS_CHECKOUT` | `false` |
| `IS_GUEST` | `true` |
| `GENERATE_NEW_CUSTOMER_ID` | `false` |
| `USE_NEW_SESSION` | `false` |

### How env gets set

Run `.maestro/scripts/ensure-env.sh <TARGET_ENV> [<device-id>]` before any
Maestro test. The script writes the `Environment` SharedPreferences key
directly via `adb run-as` + force-stop + cold-restart, so the new value is
picked up cleanly on next launch. It does NOT go through the in-app Settings
dropdown (which triggers PR #313's `Process.killProcess`). The script is the
single source of truth for env setup — do not duplicate the logic elsewhere
and do not include env-change steps inside a test YAML.

**Tests must not hard-code an environment value.** Use `${ENVIRONMENT}` from
`flow_update_settings.yaml` so the same test runs cleanly against whichever
env the caller pre-flighted to.

> The literal default in `flow_update_settings.yaml` is `DEMO`. When
> documenting a new test row in `TEST_PERMUTATIONS.md`, write `DEMO` unless
> the test author explicitly pre-flighted to something else.

---

## 7. After authoring a new test — required follow-ups

1. **Update the matching `*.feature` file.** Pick the case that fits:
   - Existing `@missing` Scenario → flip `@missing` → `@covered`.
   - Existing `Scenario Outline`, new variation → add a row to its `Examples:` table.
   - No matching Scenario → add a new `@covered` Scenario in the right section. New test = new documented behaviour; both must land together.
   - No matching `*.feature` file → **bootstrap one** with the standard structure (Background → Scenario sections by L2 mode → tagged Scenarios) and add the new test as its first `@covered` entry.
2. **Add a Maestro reference**: append a `# Maestro: <relative-path-to-yaml>` comment under the Scenario block so future readers can jump to the implementation.
3. **Update `TEST_PERMUTATIONS.md`** with one row per new test (the matrix is one-row-per-test). Skip only when the change is a pure refactor of an existing test — same coverage, different file.
4. Don't update other docs in this folder for cross-cutting authoring rules — those belong here.

---

## 8. Quick decision tree

```
Authoring a new test...

  Is this a fundamentally new integration (e.g. a new SDK surface)?
    YES → Create new <area>.feature file (L1)
    NO  → Is it a new checkout mode for an existing integration?
          YES → New Scenario in the existing feature file (L2)
          NO  → Is it a new value of a "needs full coverage" variable
                (User Type, Session, Trigger By, Card Saving, 3DS,
                Payment Result, Express)?
                YES → New row in an existing Scenario Outline (L3+)
                NO  → It's UI-only. Don't add a new test.
                      Reuse / parameterize an existing one.
```

---

## 9. Operational caveats (defer to MEMORY for these)

This document covers **what to test** and **how to structure tests**. For runtime behavior (how to run tests on this app, recover the driver, handle env-specific gaps), see the user's MEMORY notes — those are the source of truth on operational facts. This file deliberately stays out of operational territory because it rots fast.

---

**Maintained by**: contributors writing new Maestro tests.
**Cross-references**: `*.feature` files in this directory hold per-scenario intent; `TEST_PERMUTATIONS.md` is the human-facing test matrix. This file holds the cross-cutting authoring rules.
