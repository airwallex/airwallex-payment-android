---
name: maestro-test
description: End-to-end orchestrator for running Maestro tests on the Airwallex Android online SDK repo. Composes /maestro-run for execution, auto-recovers driver wedges (up to 3 attempts) without spending the test-heal budget, and auto-invokes /maestro-heal once for assertion/state failures with a single retry. Use when the user invokes /maestro-test.
allowed-tools: Bash, Read, AskUserQuestion, Skill, mcp__maestro__list_devices, mcp__maestro__run_flow_files, mcp__maestro__take_screenshot
---

# /maestro-test — Composer for run + auto-heal-once + cleanup

A thin orchestrator that runs a Maestro test, classifies failures, and applies
the right recovery: **driver/connection wedges** get up to 3 reboot-recovery
retries because they're flaky infra (not a test issue), while **assertion/state
failures** get a single heal cycle and then stop.

## When to use

- The user invokes `/maestro-test` (with or without arguments).

## Two retry budgets — keep them separate

| Failure class | How to detect | Budget | Recovery |
|---|---|---|---|
| **Connection wedge** | Run errors with `DEADLINE_EXCEEDED ... waiting_for_connection`, or `mcp__maestro__take_screenshot` returns `UNKNOWN`, or the maestro log shows no new lines for >30s while a test is supposedly running. | **3 retries**, each preceded by the driver-recovery procedure (`/maestro-heal` Branch A). Does NOT consume the heal budget below. | Reboot emulator + re-bootstrap `MaestroDriverService` gRPC server. Env preference survives reboot. |
| **Assertion / state / infra issue** | Anything else — `Assertion is false`, `No visible element found`, popups blocking, leftover consents, etc. | **1 heal cycle per distinct error fingerprint** (auto-invoke `/maestro-heal`, then retry once). See loop-detection rules below. | Whatever `/maestro-heal` does for that branch. |

Driver wedges are common after a long session or a previous test crash — they're an MCP infrastructure tax, not signal about the test under test. Don't make the user re-trigger the run just because the driver hiccupped.

### Budget reset and loop detection (assertion-class only)

A naive "1 heal per invocation" wastes effort when the first fix uncovers a *new* failure — that's progress, not a loop. But "different errors" can still loop (A → B → A → B). Rules to balance both:

**Error fingerprint** = `(last_activity, error_class, anchor)`.
- `last_activity` from `dumpsys window | grep mCurrentFocus`
- `error_class` ∈ {`ASSERTION_VISIBLE`, `ASSERTION_NOT_VISIBLE`, `ELEMENT_NOT_FOUND`, `POPUP_BLOCKING`, `DEADLINE_EXCEEDED`, `UNKNOWN`}
- `anchor` = the element name/id quoted in the error (`"Country / Region"`, `"atlCardCvc"`, etc.), normalized

Two errors with the same triple = same root cause.

**Maintain a per-invocation history** of `[(fingerprint, fix_summary)]` appended after each failed run. After each append, **run loop detection first, budget logic second**:

```
1. Loop check — detect_loop(history):
     For K in 1..floor(len(history) / 2):
       If history[-K:] == history[-2K:-K]:
         return K   # we've completed 2 full periods of a length-K loop
     return None
   If a K is returned → STOP. Surface the cycle to the user. Don't apply
   another heal. (See "Soft loop signal" below for earlier warnings.)

2. Budget logic — assuming no loop detected:
     If history[-1].fingerprint == history[-2].fingerprint:
       Same root cause repeating. The heal-cycle counter for THIS fingerprint
       is already at 1 → STOP and surface (you've used the one heal allowed
       for this error). Treat as "the fix didn't address the root cause."
     Else:
       Different fingerprint = fresh root cause. Reset the heal-cycle
       counter and proceed.
```

**Soft loop signal** (warn before the algorithm fully confirms):
- After `A → B → A` (3 events with the 1st and 3rd matching), state explicitly to the user: "I see fingerprint X recurring after a different fix. If the next failure is fingerprint Y, this is a 2-cycle loop." Then proceed cautiously.
- If any single fingerprint appears 3+ times anywhere in the history (regardless of period), pause and surface — the same root cause is resisting multiple fixes.

**Edge cases**:
- `K=1` immediate repeat (`A → A`): the fix didn't change the observable outcome. Could be "fix didn't apply" or "fix wrong." Pause without waiting for a 2nd repeat.
- Long-period loops (K=4 needs 8 events). Don't actually iterate that far — the soft signals above will pull the user in by event 5 or 6 anyway.
- A confirmed loop on the wedge-recovery path (Branch A failing 3 times) — see Step 3 below; that's "host-side issue, ask user."

## Execution flow

```
1. Invoke /maestro-run with the user's args (or prompt for missing inputs)
   ├─ Success → step 7
   └─ Failure → step 2

2. Classify the failure (see "Two retry budgets" above).

3. If connection wedge:
   a. Apply /maestro-heal Branch A (reboot + re-bootstrap driver). NOT counted
      against heal budget.
   b. Re-run /maestro-run with same args.
   c. If success → step 7.
   d. If failure → still a wedge? loop a→c up to 3 times total.
      If 3 wedges in a row → escalate to user (this is unusual; ask whether
      to reboot host machine / give up).
      If failure mode changes (e.g. now it's an assertion error) → go to
      step 4 with the new error.

4. Take a screenshot so the user can see the failure state.

5. Invoke /maestro-heal with the failure context.
   ├─ Heal concludes "REAL BUG" → stop, report to user with options. DONE.
   ├─ Heal concludes "INFRA / STATE LEAK" and applies a fix → step 6.
   └─ Heal couldn't classify → stop, report, ask user. DONE.

6. Retry /maestro-run ONCE.
   ├─ Success → report success + note that a heal was applied. DONE.
   ├─ Connection wedge on retry → go to step 3 (still have wedge retries
     unspent for THIS invocation? you may; track separately. If already
     exhausted, escalate.)
   └─ Failure (same or different assertion) → STOP. Report failure + heal
     attempt to the user with options via AskUserQuestion. Looping past one
     heal cycle hides intermittent bugs.

7. Final state report. See Step 7 below.
```

## Inputs

Same as `/maestro-run`:
```
/maestro-test test=<path-or-glob> [emulator=<id>] [customer_id=<cus_xxx>] [is_guest=<true|false>] [generate_new_customer=<true|false>] [express_checkout=<true|false>] [is_recording=<true|false>]
```
Composer forwards these to `/maestro-run`.

## Step-by-step

### Step 1 — Run

Invoke the run skill via the `Skill` tool, passing through user args:
```
Skill: maestro-run
args: <user's args, or empty if interactive>
```

Wait for the result. If success → step 7.

### Step 2 — Classify the failure

Before deciding heal vs. recovery, read the error string from `/maestro-run` and probe driver health:

```
mcp__maestro__take_screenshot { device_id: <emulator from run> }
```

- Screenshot returns `UNKNOWN` (fails fast, <2s) → **connection wedge**. Go to step 3.
- Run error string contains `DEADLINE_EXCEEDED`, `waiting_for_connection`, `RST_STREAM`, or `failed to connect` → **connection wedge**. Go to step 3.
- Otherwise (assertion error, element not found, popup blocking, state leak) → **test/infra failure**. Go to step 4.

Don't conflate the two — burning the heal budget on a driver hiccup leaves you no margin for the real failure when the driver recovers.

### Step 3 — Auto-recover connection wedge (up to 3 attempts)

Track a `wedge_retries_used` counter for this `/maestro-test` invocation. Start at 0.

```
Loop while wedge_retries_used < 3:
  1. Invoke /maestro-heal Branch A (reboot + re-bootstrap driver).
     This does NOT count against the heal budget.
  2. After driver is back (`nc -z localhost 7001` succeeds), re-run /maestro-run
     with the SAME args.
  3. Outcome:
     - Success → step 7.
     - Still a connection wedge → wedge_retries_used += 1, continue loop.
     - Different failure (assertion, element not found, etc.) → go to step 4
       with the new error. Connection budget preserved.

If wedge_retries_used == 3 and still wedged:
  Escalate to user via AskUserQuestion: "3 driver wedges in a row is unusual.
  Likely host-side issue. Options: restart MCP host / reboot the Mac / give up
  for now."
```

### Step 4 — Capture failure context (test/infra failure path)

Snapshot the device for the user:
```
mcp__maestro__take_screenshot { device_id: <emulator from run> }
adb -s <emulator> shell dumpsys window | grep mCurrentFocus
```
Show the screenshot briefly.

### Step 5 — Heal

```
Skill: maestro-heal
context: { error: "<error from run>", screenshot: "<reference>", test_path: "<from args>" }
```

Possible outcomes:
- **Real bug** — heal returns saying "do not retry; this is a real bug." → Surface to user via `AskUserQuestion` with options (file an issue / continue investigating / give up). DONE.
- **State leak** — heal cleans up consents and navigates back to MainActivity. Continue to step 6.
- **Infra brittleness with explicit by-design variability** — heal patches the helper. Continue to step 6. Otherwise, see "Real bug" above.
- **Unclear** — heal couldn't classify. Surface to user via `AskUserQuestion`. DONE.

(Heal Branch A "driver wedged" should NOT be reached here — Step 2 already routed that to Step 3.)

### Step 6 — Retry once, then apply loop detection

Invoke `/maestro-run` again with the SAME args. Whatever happens, append `(fingerprint, fix_summary)` to the invocation history.

- **Success** → report success + "heal applied: <summary>" so the user knows test infra changed.
- **Connection wedge on retry** → go to Step 3 with whatever wedge budget remains.
- **Failure** → run the loop check + budget logic from "Budget reset and loop detection" above:
  - Loop detected (period K, 2K full events) → STOP. Report the cycle and ask the user what to do.
  - Same fingerprint as before → STOP. The single allowed heal for this error didn't fix it; surface to user.
  - Different fingerprint, no loop → budget resets. Go back to Step 4 with the new failure as the starting point.
  - Soft-loop signal (3+ occurrences of one fingerprint, or A→B→A pattern forming) → continue but flag the risk to the user in the next message.

When you STOP, the user-facing report should include:
1. Original failure
2. What each heal did (chronologically)
3. The latest failure
4. The detected cycle (if any), or the repeated fingerprint
5. Options via `AskUserQuestion`: "investigate further" / "revert heal changes" / "give up for now"

### Step 7 — Final state report (success path)

After a successful run (with or without heal), surface where the app ended up:
```
adb -s <emulator> shell dumpsys window | grep mCurrentFocus
```
The test usually leaves the app on a non-Main screen due to its own cleanup steps. Don't auto-back unless the user explicitly asked.

If the user passed `cleanup=true`:
```
Skill: maestro-run
args: test=.maestro/temp_cleanup_consents.yaml
```
(Bootstrap that temp file if it doesn't exist; pattern in `/maestro-heal` Branch B4.)

## Boundaries

- **Max one heal attempt per invocation** for test/infra failures. No exceptions.
- **Up to 3 driver-recovery attempts per invocation** for connection wedges. These do NOT consume the heal budget — they're MCP infra hiccups, not test signal.
- **Track the two budgets separately.** A wedge after a successful heal-retry should still be eligible for wedge recovery (provided budget remains).
- **Heal-applied changes to SHARED helpers** (`flow_handle_3ds`, `flow_handle_redirect`, `flow_remove_all_consents`, `flow_update_settings`, `flow_update_checkout_mode`) should be flagged in the success report — those changes affect every test that uses them.
- **Heal-applied changes to NEW flows** (the one being authored / tested for the first time) are usually fine; just note them.

## What NOT to do

- ❌ Don't loop test-heal retries past a single cycle.
- ❌ Don't count a connection-wedge recovery against the heal budget — they're orthogonal failure classes.
- ❌ Don't run a full `mcp__maestro__run_flow_files` just to probe driver health — use `take_screenshot` for that (~1s vs. up to 120s).
- ❌ Don't suppress the failure details if heal couldn't fix it — surface everything.
- ❌ Don't make decisions on the user's behalf for "real bug" outcomes (file an issue, revert, etc.) — always ask.
- ❌ Don't auto-cleanup state on the success path unless explicitly requested — the user may want to inspect the post-run state.

## Useful MEMORY references

- `feedback_test_debugging.md` — anti-looping rule that motivates the once-and-stop behavior here
