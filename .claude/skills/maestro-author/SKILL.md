---
name: maestro-author
description: Author a new Maestro UI test (or new variation) for the Airwallex Android online SDK repo. Reads .maestro/docs/AUTHORING_RULES.md as canonical source for what to permute vs not, follows the flow-vs-test architectural pattern, picks the right card per checkout mode, and updates the matching .feature file at the end. Use when the user invokes /maestro-author.
allowed-tools: Bash, Read, Write, Edit, Glob, Grep, AskUserQuestion, mcp__maestro__check_flow_syntax
---

# /maestro-author — Authoring new Maestro tests

Help the user author a new Maestro test correctly the first time, by:
1. Consulting the canonical authoring rules so they don't over-test UI-only variations
2. Picking the right test card per checkout mode (the cards differ on purpose)
3. Cross-referencing intent in the BDD scenarios so the test matches a real requirement
4. Following the flow-vs-test architectural pattern
5. Closing the documentation loop by updating the .feature file

## When to use

- The user invokes `/maestro-author` (with or without arguments).

## Inputs

The user typically describes what they want to test in prose. If unclear, ask:
- **What integration?** HPP, Embedded, low-level API, **All** (change touches every integration), or a new one
- **What checkout mode?** One-off, Recurring, Recurring+Payment, **All** (change touches every mode)
- **What user type?** Guest, customer-triggered, merchant-triggered, **All** (change touches every user type)
- **What's the unique behavior?** What about this isn't already covered

**Handling "All" answers**: when the user picks "All" for any dimension, the work fans out — plan one test (or new `Examples:` row) per applicable permutation, then enumerate them back to the user *before* writing, so they can confirm or narrow the scope. Don't silently generate ten files.

## Step 0 — If the user gave a PR reference, fetch context first

If the prose mentions a PR URL, PR number, or branch name (e.g. "test the new billing fields feature from APAM-536", "based on PR #340", or "the changes in feature/foo branch"), pull the PR's metadata and diff before asking the four input questions — the diff often answers them.

```bash
# PR number or full URL — gh handles both
gh pr view <number-or-url> --json title,body,files,headRefName,baseRefName

# Diff of the PR (filtered to Kotlin/UI/resources)
gh pr diff <number-or-url>

# If only branch names are given:
gh api repos/airwallex/airwallex-payment-android/compare/<base>...<head> \
  --jq '{commits: [.commits[].commit.message], files: [.files[] | {filename, additions, deletions}]}'
```

Use the result to:
- Identify the SDK surface(s) touched (card form? consent flow? Google Pay? new setter on `Session.Builder`?).
- Decide which integration(s) need new/updated coverage and whether scope is "All" on any dimension.
- Skim the diff for *user-visible* behavior changes — UI-only restyling doesn't justify a new test (see AUTHORING_RULES.md).
- Pre-fill the four input questions from the PR rather than asking the user to re-state context that's already in the diff.

If the user described the test in prose without referencing a PR, skip this step.

## Step 1 — Read the canonical authoring rules (MANDATORY)
<!-- REPO-SPECIFIC: this project's coverage philosophy -->

```
Read: .maestro/docs/AUTHORING_RULES.md
```

This file (~150 lines) is the single source of truth for:
- L1/L2/L3+ coverage hierarchy
- Which variables need full permutation coverage (test all values)
- Which variables are UI/data only (1–2 examples sufficient)
- Anti-patterns
- Flow-vs-test pattern
- Default settings

If `.maestro/docs/AUTHORING_RULES.md` does not exist (e.g. forked / new project), bootstrap it with the standard structure (Coverage hierarchy, "needs full coverage" vs "UI-only" variables, anti-patterns, flow-vs-test pattern, default settings, decision tree).

## Step 2 — Check intent in `.feature` files

```bash
ls .maestro/docs/*.feature 2>/dev/null
grep -l "<keyword from the scenario>" .maestro/docs/*.feature
```

Possible states:
- **`@covered`** with `# Maestro: <path>` comment → Test exists. Confirm whether they want a variant.
- **`@missing`** with same description → Documented gap; fill it.
- **No matching scenario** → Either add to .feature or don't write the test (rare).

## Step 3 — Decide where the test goes (L1/L2/L3+ hierarchy)

```
Is this a fundamentally new integration (e.g. a new SDK surface)?
  YES → Create new <area>.feature file (L1)
  NO  → Is it a new checkout mode for an existing integration?
        YES → New Scenario in the existing feature file (L2)
        NO  → Is it a new value of a "needs full coverage" variable?
              YES → New row in an existing Scenario Outline (L3+)
              NO  → It's UI-only. Don't add a new test.
                    Reuse / parameterize an existing one.
```

## Step 4 — Find a flow to parameterize before creating a new one

```bash
ls .maestro/Card/flow_*.yaml
ls .maestro/Common/flow_*.yaml
ls .maestro/Api/flow_*.yaml 2>/dev/null
```

Existing reusable flows in this repo:
- `Card/flow_pay_with_card.yaml` — enter card details and pay
- `Card/flow_pay_with_consent.yaml` — pay with saved card
- `Card/flow_user_card_saving_consent_payment.yaml` — save and pay
- `Card/flow_recurring_payment.yaml` — recurring consent (used by both customer- and merchant-triggered with `TRIGGER_BY_CUSTOMER` parameter)
- `Card/flow_remove_all_consents.yaml` — cleanup
- `Card/flow_embedded_element_*.yaml` — embedded variants
- `Common/flow_handle_3ds.yaml` — 3DS challenge handler (strict; asserts the challenge appears)
- `Common/flow_handle_redirect.yaml` — redirect WebView handler
- `Common/flow_update_settings.yaml` — sets test settings (Customer ID, layout, etc.)
- `Common/flow_handle_payment_result.yaml` — dismisses success/failed/cancelled popups based on `PAYMENT_RESULT`
- `Api/flow_pay_with_saved_consent.yaml` — Get saved cards → per-card PAY (handles optional CVC + 3DS)

Only create a new `flow_*.yaml` if no existing flow matches the test's logic shape.

## Step 5 — Pick the right test card for the checkout mode
<!-- REPO-SPECIFIC: from MEMORY reference_payment_flow_rules.md -->

Different checkout modes use **different cards** to avoid interference when reusing the same customer ID across tests.

| Card type | One-off (PAYMENT) | Recurring | Recurring+Payment |
|---|---|---|---|
| **Regular (no 3DS)** | Visa 1003 (`4012000300001003`) | Visa 0008 (`4035501000000008`) | Visa 1003 (`4012000300001003`) |
| **3DS** | Visa 0088 (`4012000300000088`) | Mastercard 4518 (`5307837360544518`) | Visa 0088 (`4012000300000088`) |

The test's `runScript: cards.js` exposes these as `${output.cards.visa}`, `${output.cards.visa3DS}`, `${output.cards.challenge3DS}`, etc. The sample app's `Constant.kt` is the source of truth for which card maps to which mode — if behavior changes, check there first.

## Step 6 — User type rules
<!-- REPO-SPECIFIC -->

| User type | Customer ID handling | Allowed checkout modes |
|---|---|---|
| Guest | NO `UI_TESTING_CUSTOMER_ID` env. NO customer ID at all. | One-off only |
| Authenticated, customer-triggered | Pass a fixed env-bound customer ID (current DEMO: `cus_hkdmgg5nhhhl60k7r7r`) | All modes |
| Authenticated, merchant-triggered | Pass `GENERATE_NEW_CUSTOMER_ID=true` — never reuse a fixed ID. Merchant consents can't be deleted by automation, so each test needs a fresh customer. | Recurring + Recurring+Payment only |

**Merchant-triggered 3DS rule**: every `Pay with card → PAY` in merchant-triggered recurring/recurring+payment triggers 3DS even with regular non-3DS cards. Author MUST add `runFlow: flow_handle_3ds.yaml` between `tapOn "PAY"` and the success assertion. Customer-triggered does NOT trigger 3DS with regular cards.

## Step 7 — Session type behavior (Legacy vs New)
<!-- REPO-SPECIFIC -->

The `USE_NEW_SESSION` env determines session backend behavior. Differences that affect tests:

| Behavior | Legacy (`USE_NEW_SESSION=false`) | New (`USE_NEW_SESSION=true`) |
|---|---|---|
| Recurring mode: PAY-on-saved-consent | DISABLED (button is no-op) | ENABLED |
| Recurring mode: duplicate consent (customer-triggered) | REJECTED ("already exists") | ALLOWED |
| Recurring mode UI | Card entry form only | Shows list of existing consents |
| Everything else | Same | Same |

For tests that depend on session-specific behavior, parameterize via `USE_NEW_SESSION` and write twin tests under `LegacySession/` and `NewSession/` folders. For UI-only variants (layout, 3DS type), test only one session.

## Step 8 — Write the test YAML

Pattern for a thin parameter-passer:
```yaml
appId: com.airwallex.paymentacceptance
tags:
  - <relevant tags from .feature scenario>
platform:
  android:
    disableAnimations: true
---
- runFlow:
    when:
      true: ${IS_RECORDING}
    commands:
      - startRecording: <test-name>
- launchApp                    # ← ALWAYS include. Required for CLI/CI runs.
                               # /maestro-run will temporarily comment it out for MCP-only runs.
- runScript: <relative-path>/cards.js
- runScript: <relative-path>/settings.js
- tapOn: "Integrate with low-level API"     # or appropriate entry tile
- runFlow:
    file: <relative-path>/Common/flow_update_checkout_mode.yaml
    env:
      CHECKOUT_MODE: ${output.settings.checkoutMode.<mode>}
- tapOn: setting
- runFlow:
    file: <relative-path>/Common/flow_update_settings.yaml
    env:
      USE_NEW_SESSION: <true|false>
      EXPRESS_CHECKOUT: <true|false>
      IS_GUEST: <true|false>
      UI_TESTING_CUSTOMER_ID: ${UI_TESTING_CUSTOMER_ID}
# === scenario-specific actions ===
- runFlow:
    file: <reusable-flow>
    env:
      <parameters>
# === assertions ===
- assertVisible: "<expected outcome>"
- tapOn: "OK"
# === cleanup ===
- back
- tapOn: "Integrate with Airwallex UI"
- tapOn: "Launch payment list"
- runFlow:
    file: <relative-path>/Card/flow_remove_all_consents.yaml
- runFlow:
    when:
      true: ${IS_RECORDING}
    commands:
      - stopRecording
```

**Always include `- launchApp`.** It's the proper Maestro pattern and is required for CLI / CI runs. The `/maestro-run` skill handles the MCP-specific workaround (temporarily commenting it out, then restoring) — author should never write `# - launchApp` or omit it as a permanent state.

**Selector hygiene**:
- `scrollUntilVisible` direction: omit (default DOWN = finds elements below). Use `direction: UP` only for elements above current view. Match neighbors when in doubt.
- For optional async-loaded screens (3DS WebView, payment confirmation popup), use `extendedWaitUntil` with a regex covering BOTH the optional screen AND the next terminal screen, then branch with `runFlow when: visible:`.
- For elements that may or may not be present (e.g. CVC re-prompt on saved consents), wrap interaction in `runFlow when: visible: id: <unique-id>`. Match by `id` (View id like `atlCardCvc`) when possible — more stable than text.
- For PaymentCheckoutActivity CVC field: `id: atlCardCvc`. Confirm button: `id: rlPayNow` (text "Pay").

**Don't put logic in `test_*.yaml`.** Tests are thin parameter-passers; flows hold logic.

## Step 9 — Validate the YAML syntax

```
mcp__maestro__check_flow_syntax with the YAML body
```
Fix any syntax issues before proceeding.

## Step 10 — Update docs (REQUIRED)
<!-- REPO-SPECIFIC: closes the BDD documentation loop -->

Per AUTHORING_RULES.md section 7. Both updates below are mandatory whenever they apply — do not skip.

### 10a. Update the matching `.feature` file (always)

1. **Find the matching Scenario** in `.maestro/docs/<area>.feature` (api, hpp, embedded, errors).
2. Decide which case you're in:
   - **Existing `@missing` Scenario** → flip the tag: `@missing` → `@covered`. Leave other tags intact.
   - **Existing `Scenario Outline`, new variation** → add a row to its `Examples:` table rather than a new Scenario.
   - **No matching Scenario at all** → add a new `@covered` Scenario in the right section. The test now describes a flow that wasn't documented before; documenting it is part of the work.
   - **No matching `.feature` file at all** → bootstrap one (Step 11).
3. **Add a Maestro reference comment** under the Scenario block:
   ```gherkin
   @api @one-off @auth @covered
   Scenario: Authenticated customer pays with card
     ...
     # Maestro: .maestro/Api/NewSession/CustomerTriggered/test_api_customer_oneoff.yaml
   ```

### 10b. Update `TEST_PERMUTATIONS.md` (when the test adds a row)

The matrix is one row per test. Add a row whenever the new test is a new permutation a human would scan for — i.e. anything that flipped a `@missing` to `@covered`, added a new `Examples:` row, or introduced a new Scenario. Match the existing column order (Integration, User Type, Checkout Mode, 3DS Required, Save Card, Trigger By, Express Checkout, Layout, Scenario, Legacy Test, New Session Test).

Skip the matrix update only when the new test is a pure refactor of an existing test (same coverage, different file).

> The default env is `DEMO` (matches both `TEST_PERMUTATIONS.md` and `flow_update_settings.yaml`). If you must mention environment in a row's notes, write `DEMO`.

## Step 11 — Bootstrap a `.feature` file if none exists

If you're authoring in a brand-new area (no `<area>.feature` file yet), create one with this skeleton:

```gherkin
Feature: <Area Name>
  As a <user>
  I want <capability>
  So that <reason>

  Background:
    Given the Airwallex Payment SDK is initialized
    And the environment is set to "DEMO"
    And <other entry conditions>

  # ═══════════════════════════════════════════════════════════════════════════
  # MAIN SCENARIO 1: <name>
  # ═══════════════════════════════════════════════════════════════════════════

  @<area> @<mode> @<user-type> @covered
  Scenario: <one-line description>
    When <action>
    Then <outcome>
    # Maestro: <relative path to the new yaml>
```

## Step 12 — Smoke-run before declaring done

If a device is connected, suggest invoking `/maestro-run test=<the-new-yaml>` to verify the test actually passes. Don't author and walk away — a test that doesn't run isn't a test.

## What NOT to do

- ❌ Don't add tab + accordion variants of the same scenario (UI-only per AUTHORING_RULES.md)
- ❌ Don't write 4 different 3DS-type tests for the same scenario — same SDK code path
- ❌ Don't create a separate test per test card — pick one per behavior class
- ❌ Don't put logic in `test_*.yaml`. Use flows.
- ❌ **Don't add cleanup to test start.** Tests must assume clean state. Cleanup happens manually after a failure.
- ❌ Don't omit or comment out `- launchApp` — it's required for CLI/CI runs. The MCP run-time workaround is handled by `/maestro-run`.
- ❌ Don't relax assertions to make a test pass — see `/maestro-heal` cardinal rule.

## Useful MEMORY references

- `reference_payment_flow_rules.md` — session/mode/trigger behavior, card mappings, customer ID rules
- `reference_test_issues.md` — known stale-infra issues (regex bug, merchant-3DS rule, etc.)
- `reference_test_execution.md` — emulator + customer-ID mapping per env
- `feedback_test_debugging.md` — never-cleanup-on-start, anti-looping
