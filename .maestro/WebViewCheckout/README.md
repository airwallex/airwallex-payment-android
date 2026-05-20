# Webview Checkout Regression (Android)

Maestro suite that proves the Airwallex **checkout-ui** mounts correctly inside the
Android SDK sample app's `H5WebViewActivity` (`android.webkit.WebView`).

JIRA: [ACE-597](https://airwallex.atlassian.net/browse/ACE-597).
Mirror: `airwallex-payment-ios/.maestro/WebViewCheckout/`.

## What this suite does — and what it intentionally doesn't

| Capability | Status | Why |
|---|---|---|
| Open H5 demo → load `/shopping-cart` in WebView | ✅ Covered | `flow_open_h5_webview.yaml` |
| Submit cart → render checkout-ui HPP in same WebView | ✅ Covered | `test_webview_checkout_renders.yaml` |
| Assert Card form section title, Pay button with currency-formatted amount | ✅ Covered | Same |
| Visual evidence via screenshot | ✅ Covered | `takeScreenshot` step |
| Actual card-number / expiry / CVC submission | ❌ Not covered | See "iframe limitation" below |
| 3DS challenge OTP entry | ❌ Not covered | Same |
| Google Pay / Apple Pay button **tap** (full sheet) | ❌ Not covered | Same |
| Card save & consent reuse | ❌ Not covered | Same |

The bits we don't cover here are fully covered by the FE Playwright suite running in
a normal browser (`paymentacceptance-fe-automation-test`). The webview-specific risk
we own is `does checkout-ui mount + initialize in a WebView with the right runtime
config?` — that's exactly what this P0 catches.

## Files

```
flow_open_h5_webview.yaml          # reusable flow: cold-launch sample app → H5 demo → /shopping-cart
test_webview_checkout_renders.yaml # P0: assert checkout-ui mounts + Pay button shows
README.md                          # this file
```

The earlier draft also included `flow_webview_pay_with_card.yaml`,
`flow_webview_handle_3ds.yaml`, `flow_webview_assert_result.yaml`,
`test_webview_card_3ds_*.yaml` and `test_webview_card_save_and_reuse.yaml`. These were
removed once we discovered Maestro cannot traverse into the card iframe(s) on either
platform — see "Iframe limitation" below.

## Run

```bash
# Pre-reqs:
#   - JDK 17 (`brew install openjdk@17`)
#   - Android SDK 34 + emulator + system-images;android-34;google_apis;arm64-v8a
#   - Maestro CLI (`curl -sLfO https://get.maestro.mobile.dev && bash maestro`)
#   - Sample app installed:  `./gradlew :sample:assembleDebug && adb install -r sample/build/outputs/apk/debug/*.apk`

export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$HOME/.maestro/bin:$PATH"

# Boot a Pixel 6 / Android 34 AVD called `webview_test`
emulator -avd webview_test -no-snapshot -no-boot-anim &
adb wait-for-device

# Run the whole suite (single test for now)
maestro test .maestro/WebViewCheckout/

# Or just the P0
maestro test .maestro/WebViewCheckout/test_webview_checkout_renders.yaml
```

Typical pass time on an M-series Mac with arm64 emulator: **~25 seconds** per run.
Stability: **3/3 green** in initial bring-up.

## Iframe limitation (the reason the suite is intentionally small)

Airwallex's `cardElement` mounts each PCI-sensitive input (card number, expiry, CVC)
in its **own nested cross-origin iframe** served from `checkout.airwallex.com`. The
Android `WebView` exposes only the FIRST iframe level to the OS accessibility tree,
so Maestro's `id:`, `text:` and even `point:` selectors cannot reliably target inputs
inside those nested frames.

What that means in practice:

- `assertVisible: "Card information"` ✅ works (section header, OUTER iframe)
- `assertVisible: "Pay 100.00 CNY"` ✅ works (Pay button, OUTER iframe)
- `assertVisible: "Google Pay"` ✅ works (button label, OUTER iframe)
- `tapOn: { id: "cardnumber" }` ❌ fails (HTML `name`/`id` is not exposed)
- `tapOn: { text: "1234 1234 1234 1234" }` ❌ fails (placeholder is inside an inner
  iframe and not surfaced)
- Coordinate taps `point: 50%,55%` ⚠️ work for a single device but break on rotation,
  density change, or any vertical layout change in the demo store

To actually drive card submission via Maestro you'd need either:

1. An exported app entry point that bypasses the iframe (e.g. an SDK-internal test
   harness) and posts a card token directly, OR
2. A custom WKWebView/`WebView` subclass in the sample app that injects a JS bridge
   exposing iframe internals to a11y, OR
3. Switch from Maestro to a tool that can drive cross-origin iframes natively
   (Playwright Mobile, Detox, or a Chrome DevTools Protocol pipeline). The FE
   automation suite already covers this path on `mobile-webkit` and
   `mobile-chrome` configurations.

We picked option (3) implicitly: the FE Playwright suite is the source of truth
for card / 3DS / digital-wallet **behaviour**, and this Maestro suite is the source
of truth for **WebView integration** (does checkout-ui mount + initialize at all?).

## Known issues found during bring-up

1. **MainActivity entry text drifted from the strings.xml resource.** The button is
   hard-coded as `"Launch HTML5 Demo"` (no space) in `activity_main.xml` while
   `R.string.h5demo` is `"H5DEMO"`. The flow uses the literal button text so it stays
   tied to the visible UI.

2. **Maestro `eraseText` defaults to 50 chars.** The H5DemoActivity URL field prefill
   is 56 chars (`https://demo-pacheckoutdemo.airwallex.com/shopping-cart`), so a bare
   `- eraseText` leaves 6-character residue which corrupts any subsequent
   `inputText`. Workaround documented in the flow yaml.

3. **Maestro `inputText` corrupts long URL strings via Gboard autocorrect.** Even
   with a clean field, typing `https://staging-pacheckoutdemo.airwallex.com/...`
   through the emulator's default Gboard produces a different string than what was
   asked for — the WebView then loads the wrong route. We worked around this by
   keeping the prefilled default demo URL. To run against staging, install
   AdbKeyBoard (`adb install AdbKeyBoard.apk; adb shell ime enable
   com.android.adbkeyboard/.AdbIME; adb shell ime set
   com.android.adbkeyboard/.AdbIME`) and re-enable the disabled inputText steps in
   `flow_open_h5_webview.yaml`.

4. **WebView sample app ANRs under repeated cold-launch.** Running the suite back to
   back occasionally triggers an `ANR` dialog ("Airwallex Sample isn't responding")
   on the H5DemoActivity tap. Rebooting the emulator clears it. Suspected cause is a
   leaked `WebView` instance from the previous run.

5. **WebView a11y tree refresh lag.** Chaining multiple `assertVisible` calls right
   after a checkout-ui mount caused flaky failures (text X visible in one snapshot,
   gone in the next while the page is mid-hydrate). The test now uses a small,
   stable set of anchors (`Card information` + `Pay .*`) plus a screenshot.

## Future work / not blocking ACE-597

- Hook this suite into CI (`manual_first` per ticket scope; full pipeline is
  follow-up).
- Coordinate-based extension that submits a real card via `pointAndDrag` on the
  iframe pixel position; only useful if the demo store layout stabilizes.
- Add a `clearState: true` variant that proves first-time-user mount path (e.g. no
  prefilled localStorage / consent state).
