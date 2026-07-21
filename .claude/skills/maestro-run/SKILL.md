---
name: maestro-run
description: Run a Maestro UI test on the Airwallex Android online SDK repo via the Maestro MCP server. Handles the launchApp workaround (temporarily comments out launchApp + `am start` MainActivity from host, restores after), validates env preference (must be DEMO), prompts for missing inputs with auto-detected defaults. Use when the user explicitly invokes /maestro-run.
allowed-tools: Bash, Read, Edit, AskUserQuestion, Monitor, mcp__maestro__list_devices, mcp__maestro__run_flow_files, mcp__maestro__take_screenshot
---

# /maestro-run — Procedural test runner for Maestro

Run a single Maestro test (or set of tests) against a connected Android emulator
via the Maestro MCP server. This skill encodes the project's quirks (env
preference, broken `launchApp`, driver-wedge avoidance) so a contributor can
run a test correctly without rediscovering them.

## When to use

- The user invokes `/maestro-run` (with or without arguments).
- Do NOT auto-load on incidental mentions of "maestro" — explicit invocation only.

## Inputs (precedence: args > auto-detected > sensible default > prompt)

Argument syntax:
```
/maestro-run test=<path-or-glob> [emulator=<id>] [customer_id=<cus_xxx>] [is_guest=<true|false>] [generate_new_customer=<true|false>] [express_checkout=<true|false>] [is_recording=<true|false>]
```

For each value:
1. **Use the slash arg** if provided.
2. **Auto-detect from device or repo** where possible (see below).
3. **Apply a sensible default** (e.g. `IS_RECORDING=false`).
4. **Otherwise prompt via `AskUserQuestion`** with the best-effort default pre-filled.

## Pre-flight checklist

Run in order. Stop and report if any step fails.

### 1. Pick the emulator
<!-- GENERIC: Maestro+MCP -->

```bash
adb devices
```

Logic:
- If `emulator=<id>` arg passed → use it.
- Else if exactly **one** emulator is connected → use it.
- Else if multiple emulators connected → prompt the user via `AskUserQuestion` (list the connected ones; do NOT default to a specific port like 5554).
- Else if **zero** emulators connected → ask the user to start one and stop.

### 2. Ensure env preference is DEMO
<!-- REPO-SPECIFIC: Airwallex Android online SDK -->

Call the pre-flight script — it reads the app's SharedPreferences via
`adb shell run-as`, branches on (file missing / key missing / wrong value /
already correct), force-stops + writes + cold-restarts when a swap is needed,
and exits 0 once the app is on MainActivity with the target env set:

```bash
.maestro/scripts/ensure-env.sh DEMO <emulator>
```

- **DEMO** is the canonical env for local / Claude-driven runs. PREVIEW has
  disabled payment methods (e.g. alipayhk recurring) that break tests with
  non-test-bug failures; DEMO has the full set enabled.
- If the user explicitly requests a different env (e.g. `env=PREVIEW` in the
  `/maestro-run` args), pass that to the script instead.
- If `ensure-env.sh` exits non-zero (most often because the installed build
  isn't debuggable so `run-as` fails), surface the script's stderr and stop.
- The script bypasses the in-app env-change handler entirely, so PR #313's
  `Process.killProcess` never fires during the swap. Safe between MCP calls
  and at fresh install. See "Soft rules" below for the historical context.

### 3. Resolve the customer ID
<!-- REPO-SPECIFIC: Airwallex Android online SDK -->

User-type rules (from MEMORY: `reference_payment_flow_rules.md`):

| User type | What to pass |
|---|---|
| Guest (`is_guest=true`) | NOTHING — do not pass `UI_TESTING_CUSTOMER_ID` at all |
| Authenticated, customer-triggered | A fixed customer ID for the env. DEMO canonical: `cus_hkdmgg5nhhhl60k7r7r` (PREVIEW canonical, if explicitly used: `cus_sgpvth9jjhig6lzfpst`) |
| Authenticated, merchant-triggered | Pass `GENERATE_NEW_CUSTOMER_ID=true` instead. NEVER reuse a fixed ID — merchant tests need fresh customers because their consents can't be deleted by automation |

Detect merchant-triggered by the test path: tests under `MerchantTriggered/` or with `NEXT_TRIGGERED_BY=Merchant` env. If unsure, prompt.

### 4. Replace launchApp temporarily for the MCP run
<!-- GENERIC pattern, REPO-SPECIFIC trigger -->

The committed test YAML SHOULD include `- launchApp` (proper Maestro pattern, works in CLI/CI). For MCP runs only, temporarily comment it out and use `adb shell am start` from host.

Workflow:
```bash
# 1. Snapshot original (so we can verify restoration)
grep -n "^- launchApp$" <test-file>

# 2. Temporarily comment it out
sed -i '' 's|^- launchApp$|# - launchApp  # TEMP: MCP run workaround, will be restored|' <test-file>
```

If the test references sub-flows via `runFlow: file:`, check those for `- launchApp` too — comment in any that have it.

**Why:** Maestro's `launchApp` does (1) force-stop, (2) `am start`, (3) wait-for-ready via UiAutomation. Step 3 times out for this app (slow cold-start: App Startup, fingerprinting/TrustDefender, DI graph). `tapOn` / `assertVisible` / etc. only do device-side queries against the current window and are unaffected.

### 5. Bring the app to MainActivity via `am start`
<!-- GENERIC pattern, REPO-SPECIFIC trigger -->

```bash
adb -s <emulator> shell am start -n com.airwallex.paymentacceptance/.ui.MainActivity
```

`am start -n <component>` is explicit and deterministic. Don't use
`monkey -c LAUNCHER` — when the debug build has LeakCanary installed, two
activities resolve to LAUNCHER (MainActivity + LeakCanary's `LeakLauncherActivity`)
and monkey picks non-deterministically.

Then poll for focus rather than blanket-sleeping:
```bash
until adb -s <emulator> shell dumpsys window windows 2>/dev/null \
  | grep -q 'mCurrentFocus.*com.airwallex.paymentacceptance.ui.MainActivity'; do sleep 0.3; done
```

If the app is on a wrong activity (APIIntegrationActivity, UIIntegrationActivity, PaymentMethodsActivity) due to leftover state from a prior failed test:
```bash
for i in 1 2 3 4; do adb -s <emulator> shell input keyevent KEYCODE_BACK; sleep 0.4; done
```
Then dismiss any "Payment cancelled" / "Payment failed" popups via `mcp__maestro__tap_on { text: "OK" }` and re-poll focus.

If consents leftover from a prior failed test cause the test's "no saved cards initially" assertion to fail, hand off to `/maestro-heal` Branch B4 for cleanup before retrying.

## Run the test
<!-- GENERIC pattern -->

```
mcp__maestro__run_flow_files
  device_id: <emulator>
  flow_files: <test-path-relative-to-repo-root>
  env: { UI_TESTING_CUSTOMER_ID: "<resolved or omitted>", IS_RECORDING: "false", GENERATE_NEW_CUSTOMER_ID: "<resolved>", ...other args }
```

**Path note**: pass paths relative to the repo root (e.g. `.maestro/Api/...`), not absolute. The MCP server roots at the repo.

**Sequential only on Mac.** Maestro CLI/MCP both want exclusive ownership of gRPC port 7001 — running multiple instances or using CLI while MCP is alive causes `DEADLINE_EXCEEDED`. Don't try to parallelize.

**Multi-test runs — reset between every test.** When running more than one test in sequence (one `run_flow_files` call per test), force-stop and re-launch MainActivity BEFORE every test, not just the first:

```bash
adb -s <emulator> shell am force-stop com.airwallex.paymentacceptance
adb -s <emulator> shell am start -n com.airwallex.paymentacceptance/.ui.MainActivity
until adb -s <emulator> shell dumpsys window 2>/dev/null \
  | grep -q 'mCurrentFocus.*MainActivity'; do sleep 0.3; done
```

Tests do NOT guarantee they end on MainActivity — many leave the app on `APIIntegrationActivity`, `UIIntegrationActivity`, or mid-3DS / mid-redirect screens. Without an explicit reset, the next test's first `tapOn` runs against the previous test's residue and fails non-deterministically.

- `am force-stop` is required: without it, `am start -n MainActivity` on an already-running app may deliver an intent to the top activity instead of navigating ("Activity not started, intent has been delivered to currently running top-most instance").
- Use `am start -n` (explicit component), NEVER `adb monkey -c LAUNCHER` — see "Hard rules" / step 5 / LeakCanary.
- Do NOT use `pm clear` between tests — wipes the SharedPreferences `Environment` key. The cleanup flows in test YAMLs handle per-customer state; that's enough.
- The first test of a session can rely on `ensure-env.sh` to leave the app on MainActivity. Subsequent tests need this explicit reset.

**Fast driver-health probe before a long run (recommended).** A wedged driver makes `run_flow_files` hang the full ~120s gRPC deadline. A `take_screenshot` call against the same device returns `UNKNOWN` in ~1s when the driver is wedged, so use it as a cheap pre-flight check:

```
mcp__maestro__take_screenshot { device_id: <emulator> }
# Success → driver healthy, proceed with run_flow_files.
# Returns "UNKNOWN" / quick failure → driver is wedged. Run the recovery
# procedure in /maestro-heal Branch A BEFORE the test run, not after.
```

When invoked via `/maestro-test`, the composer also uses this probe to classify failures and route to the connection-wedge recovery loop (separate from the test-heal budget).

**For long-running ops, stream the maestro log live** to surface failures within seconds instead of waiting for the gRPC deadline:
```bash
# In a parallel Monitor:
tail -F ~/.maestro/tests/$(ls -t ~/.maestro/tests | head -1)/maestro.log \
  | grep --line-buffered -iE "refused|disconnect|UiAutomation|error|failed|timeout"
```
(Caveat: maestro splits artifacts across two timestamped dirs per run — log lands in one, the HTML report in another. Don't pin to a single `LATEST_DIR` if polling for completion; scan all recent dirs.)

**Silent-wedge detector (for cases where no error line is emitted).** Some wedges hang the driver before maestro writes any log entries for the current command — the grep above sees no errors because there's no output at all. To detect those, monitor the log file's mtime; no updates for >30 s during what should be an active run means stuck:

```bash
LATEST=~/.maestro/tests/$(ls -t ~/.maestro/tests | head -1)
while true; do
  cur=$(stat -f %m "$LATEST/maestro.log" 2>/dev/null)
  now=$(date +%s)
  if [ -n "$cur" ] && [ $((now - cur)) -gt 30 ]; then
    echo "STUCK: no log activity for >30s — driver likely wedged"
    break
  fi
  sleep 5
done
```

Use this only when you actually need it (debugging a hang); it's noisy as a default.

## Post-run

### Always — restore launchApp
<!-- REPO-SPECIFIC -->

```bash
sed -i '' 's|^# - launchApp.*MCP run workaround.*$|- launchApp|' <test-file>
```

Verify the file is back to its committed state:
```bash
grep -n "launchApp" <test-file>   # should show "- launchApp" not "# - launchApp"
```

### On success
- Report success and the number of commands executed (from MCP result).
- Note where the app ended up (`adb shell dumpsys window | grep mCurrentFocus`) — often left on a non-Main screen due to the test's cleanup steps. Don't auto-back; that's the user's call.

### On failure — classify before retrying

1. **Probe the driver first** with `mcp__maestro__take_screenshot`. If it returns `UNKNOWN` (fails fast, <2 s), the driver is wedged — this is NOT a test failure. Run `/maestro-heal` Branch A (reboot + re-bootstrap driver) and retry the run. **Auto-reboot without prompting, up to 3 reboots per session.** Both `/maestro-test` (the composer) and standalone `/maestro-run` follow the same policy — driver wedges are infrastructure hiccups, not user decisions, and asking wastes the time the reboot would've taken. Only stop and surface to the user once the 3-reboot budget is exhausted.

2. **Otherwise read the error string.** If it contains `DEADLINE_EXCEEDED`, `waiting_for_connection`, `RST_STREAM`, or `failed to connect` — still a connection wedge. Same recovery as above (auto-reboot, no prompt).

3. **Anything else** (`Assertion is false`, `No visible element found`, popups blocking, leftover state) is a real test/infra failure. Take a screenshot for the user. If hand-off to `/maestro-heal` is appropriate, do it **at most once**. Looping retries masks real bugs.

Recap: driver wedges don't count against the heal budget. Real failures do. Track wedge count per session — after 3 reboots, the issue is no longer "a hiccup," so surface to the user.

## Hard rules (don't break these)

- ❌ Never use `launchApp` in the YAML at run-time on this app — temporarily comment out per step 4.
- ❌ Never `pm clear` or `clearState` outside of a controlled "simulate fresh install" experiment — wipes the Environment preference and all per-env caches.
- ❌ Never `pkill maestro`, `pkill UiAutomation`, or `bash .maestro/free-port.sh` — severs the MCP tool channel for the rest of the session. Recovery without killing MCP is in `/maestro-heal` Branch A.
- ❌ Never blanket-sleep — gate on real signals (`adb shell dumpsys`, `nc -z`, `getprop sys.boot_completed`).
- ❌ Never fall back to `maestro test` CLI while MCP is alive — port-conflict.
- ❌ Never run tests in parallel on Mac — port-conflict on gRPC 7001.

## Soft rules (preferences, not hard prohibitions)

- ⚠️ **Prefer the bash pre-flight (`.maestro/scripts/ensure-env.sh`) over UI-driven env switches.** Empirically (this repo, current Maestro + Android Studio emulator) the maestro driver survives the in-app `Process.killProcess(myPid)` that PR #313 fires when the user changes env via the Settings dropdown — the app restarts to MainActivity and the driver reconnects. The historical "never script env switching" rule was based on a now-superseded driver/Android combo. Still, the script is faster (~1s vs ~10s), doesn't depend on the driver being healthy, and works at fresh install, so use it for routine env setup.
- ⚠️ **Do not put env changes inside test YAMLs.** Env is part of the runtime environment, not the test's own state. Run `ensure-env.sh` once before the test suite, then leave env alone for the rest of the session.

## Useful MEMORY references

When in doubt, the operational source-of-truth lives in:
- `feedback_maestro_launchapp_workaround.md` — the recipe + why
- `feedback_maestro_uiautomation_recovery.md` — driver wedge recovery
- `feedback_maestro_mcp_lifecycle.md` — never-kill-MCP rule
- `feedback_maestro_execution.md` — always MCP, never CLI; sequential only on Mac
- `feedback_maestro_realtime_logs.md` — live log streaming
- `reference_test_execution.md` — emulator + customer-ID mapping
- `reference_payment_flow_rules.md` — customer ID rules per user type
