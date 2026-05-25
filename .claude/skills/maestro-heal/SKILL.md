---
name: maestro-heal
description: Triage a failing Maestro test on the Airwallex Android online SDK repo and decide whether to fix the test infra, recover device state, or surface a real bug. Default posture is STRICT — only relax assertions when by-design variability is explicitly stated. Use after a /maestro-run failure or when the user invokes /maestro-heal.
allowed-tools: Bash, Read, Edit, Glob, Grep, AskUserQuestion, Monitor, mcp__maestro__take_screenshot, mcp__maestro__inspect_view_hierarchy, mcp__maestro__back, mcp__maestro__tap_on, mcp__maestro__run_flow_files
---

# /maestro-heal — Failure triage for Maestro

When a Maestro run fails, decide what kind of failure it is and respond
appropriately. Encodes the central judgment we got wrong twice in prior
sessions: **default to strict; do not paper over a failed assertion just
to make a run pass.**

## When to use

- The user invokes `/maestro-heal` after seeing a failure.
- Auto-invoked by `/maestro-test` composer after the first failure (max once).

## The cardinal rule (read before doing anything else)

> **A failed assertion in a test that explicitly intends the behavior is a REAL BUG.** Do not patch the helper / shared flow / strict assertion to make it tolerant. Only relax when the user has explicitly stated the behavior is by-design variable.
>
> Example of a real bug we masked once: the test button literally named "Pay with card and trigger 3DS" → the 3DS Purchase Authentication challenge MUST appear. When PREVIEW skipped it, that was the bug — not test-infra brittleness. Reverted the helper-tolerant change after the user pushed back.
>
> Example of true optionality (acceptable to make tolerant): the saved-consent re-charge flow's CVC prompt — the SDK genuinely may or may not ask for CVC. The user explicitly said "if it asks, enter; if not, skip." That's by-design variability.

When in doubt, **fail loudly and ask the user** rather than silently relax an assertion.

## Anti-looping (Important)

- **Max 3 retries on the same fix approach.** If the same fix attempt fails 3 times, stop and surface to the user — there's a deeper issue (test logic problem or app bug).
- **The composer caps you at 1 retry per distinct error fingerprint for ASSERTION/STATE failures (Branches B-onward).** A genuinely new failure after a fix is progress and gets a fresh heal cycle; the same fingerprint repeating is a sign the fix missed.
- **Loop detection runs before budget reset.** Repeating cycles (A→B→A→B, or A→B→C→A→B→C, …) waste budget regardless of how "different" each event looks. See `/maestro-test`'s "Budget reset and loop detection" section for the period-K algorithm.
- **Branch A (connection-wedge recovery) has its own budget: up to 3 reboot+rebootstrap retries per `/maestro-test` invocation, separate from the assertion-heal cycle.** Driver wedges are MCP infra hiccups, not test signal. Don't burn the heal budget on them.
- **Never add cleanup to the start of a test.** Cleanup must happen separately (manually or via the temp wrapper in Branch B4). Tests should assume clean state — adding cleanup to test start masks real issues and makes tests fragile.

### Computing the error fingerprint

When heal hands back to the composer, also return a fingerprint so the composer can run loop detection. Compute it from the failed run:

```
fingerprint = (
  last_activity,     # `adb -s <emulator> shell dumpsys window | grep mCurrentFocus`
                     # → e.g. "PaymentMethodsActivity"
  error_class,       # one of:
                     #   ASSERTION_VISIBLE      — error matches /Assertion is false.*visible/
                     #   ASSERTION_NOT_VISIBLE  — /Assertion is false.*notVisible/
                     #   ELEMENT_NOT_FOUND      — /No visible element found/
                     #   POPUP_BLOCKING         — screenshot shows a dialog we didn't dismiss
                     #   DEADLINE_EXCEEDED      — /DEADLINE_EXCEEDED|waiting_for_connection/
                     #   UNKNOWN                — anything else
  anchor             # the element text/id quoted in the error message,
                     # normalized (trim whitespace, lowercase if helpful).
                     # e.g. for `Assertion is false: "Country / Region" is visible`
                     #      → "Country / Region"
)
```

The composer compares fingerprints by tuple equality. Two errors that share `(activity, class, anchor)` are the same root cause for loop-detection purposes, even if their full error strings differ in stack traces or timing details.

## Triage tree

### Step 0 — Always start here
<!-- GENERIC -->

Take a screenshot AND inspect current focus before deciding anything:
```
mcp__maestro__take_screenshot { device_id: <emulator> }
adb -s <emulator> shell dumpsys window | grep mCurrentFocus
```
The error message alone is rarely enough — the screen state tells you whether it's a state leak, a real assertion failure, or a driver wedge.

For long-running heals, run a parallel `Monitor` tailing the log so you see live signals:
```bash
tail -F ~/.maestro/tests/$(ls -t ~/.maestro/tests | head -1)/maestro.log \
  | grep --line-buffered -iE "refused|disconnect|UiAutomation|error|failed|timeout"
```

Then walk the tree below in order. Stop at the first match.

---

### Branch A — Driver wedged (connection-class failure)
<!-- GENERIC: any Maestro+MCP setup -->

Symptoms (any one is enough to classify):
- Run error contains `DEADLINE_EXCEEDED` with `waiting_for_connection`
- Run error contains `UiAutomation not connected`, `UNAVAILABLE: Network closed`, `RST_STREAM`, or `failed to connect`
- A follow-up `mcp__maestro__take_screenshot` returns `UNKNOWN` (fast-fails in <2 s)
- `tail -F` on the latest `~/.maestro/tests/<ts>/maestro.log` shows no new lines for >30 s during what should be an active run

**Budget rule (important).** Branch A recoveries are **infra hiccups, not test failures**. When invoked via `/maestro-test`, they get their own budget of up to 3 attempts and do NOT consume the single test-heal cycle. The composer tracks the two counters separately — see `/maestro-test`'s "Two retry budgets" table.

**Immediate stop on MCP retries WITHOUT recovery.** Every blind retry hangs the full 120 s gRPC deadline — two = 4 minutes wasted. Run the recovery procedure below first, THEN retry.

**Fast probe first** to confirm the driver is dead before committing to a reboot:
```bash
nc -z localhost 7001    # the gRPC server's TCP port
```
If `nc` succeeds but a `take_screenshot` MCP call still returns `UNKNOWN`, the on-device `MaestroDriverService` is alive but UiAutomation underneath it is wedged — still need the full reboot. If `nc` fails, the port forward itself is gone — go straight to recovery.

Recovery procedure (gate on real signals at every step):

```bash
adb -s <emulator> reboot
until [ "$(adb -s <emulator> shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 2; done
until adb -s <emulator> shell pm path android >/dev/null 2>&1; do sleep 0.5; done
adb -s <emulator> forward tcp:7001 tcp:7001
# Start driver in background; it stays alive serving gRPC
adb -s <emulator> shell am instrument -w -e debug false \
  -e class 'dev.mobile.maestro.MaestroDriverService#grpcServer' \
  dev.mobile.maestro.test/androidx.test.runner.AndroidJUnitRunner
# Wait for the gRPC port to actually bind (real readiness signal)
until nc -z localhost 7001 2>/dev/null; do sleep 0.5; done
```

Env preference survives reboot (it's in SharedPreferences). Re-run pre-flight from `/maestro-run` and retry the test.

**NEVER `pkill maestro`** — the Claude Code harness owns the maestro MCP subprocess; killing it severs the MCP tool channel for the rest of the session and the user has to type `/mcp` to recover. If recovery via reboot fails, ask the user to type `/mcp` rather than killing anything.

---

### Branch B — Element not found / "Assertion is false"

The most common failure class. Diagnose before fixing.

#### B1: Is there a popup blocking the next step?
<!-- REPO-SPECIFIC popups -->

Common popups that block the next step on this app:
- "Payment cancelled" — back navigation triggered cancellation; tap OK
- "Payment failed" — payment terminated unexpectedly; tap OK, investigate why
- "Customer Dialog" — saved-cards dialog still open from a previous step
- "Create PaymentIntent Failed" — typically wrong customer ID for current env (e.g. PREVIEW ID used on DEMO)

Fix: dismiss the popup (tap "OK" or back), confirm focus, then decide whether to retry or report.

#### B2: Did a previous step overshoot?

Symptoms:
- Test fails on `tapOn` for an element that should exist on screen X
- Screenshot shows we're on screen Y (e.g. MainActivity instead of APIIntegrationActivity)
- Often happens when a `- back` in the test was meant to dismiss a loading overlay but no overlay was present

Fix options (in order of preference):
1. **If the new flow being authored can self-recover**, add a defensive `runFlow when: visible: <main-screen-marker>` block at its start. Example for navigating back to API integration:
   ```yaml
   - runFlow:
       when:
         visible: "Launch HTML5 Demo"   # MainActivity-only marker
       commands:
         - tapOn: "Integrate with low-level API"
   ```
2. **If overshoot is one-time after a failed run**, recover state manually (back / `am start -n <pkg>/.ui.MainActivity`), re-run, flag to the user.
3. **Don't** modify the test to skip the assertion — hides the real cause.

#### B3: Is the test asserting something that genuinely should be true?

This is the cardinal rule check. Before relaxing any assertion:

1. **Find the failing test path** (e.g. `test_api_customer_recurring.yaml`).
2. **Grep `.maestro/docs/*.feature`** for the matching scenario:
   ```bash
   grep -l "<test filename or related tag>" .maestro/docs/*.feature
   ```
   Read the matching Scenario block — its `Then` clauses are the source of truth for intended behavior.
3. **If the failed assertion matches a `Then` clause** OR the test button name implies the behavior (e.g. "Pay with card and trigger 3DS" → 3DS MUST appear): **STOP. This is a real bug.** Report to the user.
4. **If the .feature has no scenario for what you observed**: assume real bug or env mismatch. Fail loudly. Don't patch.
5. **Only if** the user has explicitly stated this step is by-design optional: patch using the pattern in `flow_pay_with_saved_consent.yaml` (`extendedWaitUntil` + `runFlow when: visible:`).

#### B4: Is state leaking from a prior failed run?

Symptoms:
- Test asserts "no saved cards initially" but cards exist
- Test asserts a popup but a different popup is on screen

Fix: navigate back to MainActivity, run cleanup, re-run. **Never add cleanup to the start of a test** — it must remain separate.

Temporary cleanup wrapper (create as `.maestro/temp_cleanup_consents.yaml`, run via MCP, delete after):
```yaml
appId: com.airwallex.paymentacceptance
---
- tapOn: "Integrate with Airwallex UI"
- tapOn: "Launch payment list"
- runFlow:
    file: Card/flow_remove_all_consents.yaml
- back
- runFlow:
    when:
      visible: "Payment cancelled"
    commands:
      - tapOn: "OK"
- back
```

Caveat: **merchant-triggered consents cannot be deleted** by the customer-side cleanup flow ("Cannot remove card" message). If cleanup leaves merchant consents behind, that's expected — for merchant-triggered tests, use a fresh customer ID (`GENERATE_NEW_CUSTOMER_ID=true`) instead of trying to clean.

#### B5: Wrong customer ID for current env
<!-- REPO-SPECIFIC -->

Symptoms:
- Test fails early with "Create PaymentIntent Failed" / "Customer with ID cus_xxx cannot be found"
- The customer ID in env was set for a different env (PREVIEW ID used on DEMO, or vice versa)

Fix: pass the correct customer ID for the current env. DEMO canonical: `cus_hkdmgg5nhhhl60k7r7r`. PREVIEW canonical (if explicitly used): `cus_sgpvth9jjhig6lzfpst`. Customer IDs are env-bound and won't work cross-env.

---

### Branch C — Wrong scrollUntilVisible direction
<!-- GENERIC -->

Symptoms: "No visible element found" on `scrollUntilVisible` but the element exists when you scroll manually.

Fix: check the `direction:` value.
- **No direction (default DOWN)**: finds elements BELOW current view (swipe up)
- **`direction: UP`**: finds elements ABOVE current view (swipe down)

Match neighboring `scrollUntilVisible` calls in the same file when in doubt.

---

### Branch D — Optional screen race
<!-- GENERIC -->

Symptoms: an assertion fires before an asynchronously-loaded screen appears (WebView, 3DS challenge after a network call).

Fix: replace the immediate `runFlow when: visible: X` with `extendedWaitUntil: visible: "X|Y|terminal"` first, THEN branch with `runFlow when: visible: X`. The `when:` predicate is an instant check, not a poll, so a wait is needed before it for any async screen. Use this only when the screen is genuinely optional (Branch B3 cardinal rule still applies).

---

### Branch E — Stale shared infra known issues
<!-- REPO-SPECIFIC -->

Some infra is known-broken in specific scenarios. Don't try to fix these in heal — surface them to the user:

- **`flow_update_checkout_mode.yaml` regex bug**: `notVisible: ${CHECKOUT_MODE}` skips the mode change when transitioning between modes whose names contain each other ("Recurring and payment" → "Recurring"). Symptom: test starts in wrong mode, wrong cards, wrong 3DS behavior. Workaround: run the affected test in isolation after relaunch/state-clear.
- **`flow_handle_redirect.yaml` PREVIEW alipayhk gap**: alipayhk recurring is disabled on PREVIEW and returns "Payment failed - method is not enabled". The committed flow does not handle this; if a test fails here on PREVIEW, it's known-stale.
- **Merchant-triggered 3DS requirement**: every `Pay with card → PAY` in merchant-triggered recurring/recurring+payment triggers 3DS, even with regular non-3DS PANs. If a merchant test fails on `assertVisible "Payment successful"` because Purchase Authentication is on screen, the test is missing a `runFlow flow_handle_3ds.yaml` step. (Customer-triggered does NOT trigger 3DS with regular cards.)

---

## When to STOP and ask the user

Stop and surface the situation rather than continuing if:
- You've recovered from a wedge or state leak and the test still fails the same way (real bug suspected).
- The failure looks like a real bug per Branch B3 — never silently mask it.
- You've made 2+ retries (or 1 if invoked by composer) — looping retries hide intermittent failures, which are themselves bugs.
- The failure is in shared infra (`flow_handle_3ds`, `flow_handle_redirect`, `flow_remove_all_consents`, `flow_update_settings`, `flow_update_checkout_mode`) — modifying these affects every test that uses them; require explicit confirmation before touching.
- The user's stated scope didn't include this kind of fix.

When you stop, give the user:
1. Original error message
2. Screenshot URL or description of what's on screen
3. Triage conclusion (which branch matched)
4. Two or three concrete options via `AskUserQuestion`

## After a successful heal + retry

If healing worked and the test now passes:
- Record what you changed (file edits, state cleanup).
- If the change was to a SHARED helper, **flag it explicitly** — those changes affect every test that uses them.
- If the change was to a NEW flow being authored, that's usually fine; just note it.

## Useful MEMORY references

- `feedback_test_debugging.md` — anti-looping rules, never-cleanup-on-start
- `feedback_maestro_launchapp_workaround.md` — env preference issues
- `feedback_maestro_uiautomation_recovery.md` — driver recovery procedure
- `feedback_maestro_mcp_lifecycle.md` — never-kill-MCP, ask user to /mcp instead
- `feedback_maestro_realtime_logs.md` — live log tail patterns
- `reference_test_issues.md` — known stale-infra issues (regex bug, etc.)
- `reference_payment_flow_rules.md` — session/mode/trigger behavior matrix
