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
| Google Pay button **tap** + checkout-ui handler fires without crashing WebView | ✅ Covered | Same (tap-level smoke) |
| Google Pay / Apple Pay **native sheet** appears | ⚠️ Possible, requires setup | Needs `system-images;android-34;google_apis_playstore` AVD + Google Wallet + tokenized test card; see "Digital wallet sheet coverage" below |
| Google Pay / Apple Pay sheet → confirm → success callback | ⚠️ Possible, fragile | Sheet internals change across Android / iOS versions; covered today by FE Playwright |
| Actual card-number / expiry / CVC submission | ✅ Covered | `test_webview_card_3ds_success.sh` (adb-driven; see "Iframe limitation" below for why this is a shell script and not a Maestro yaml) |
| 3DS challenge OTP entry | ✅ Covered | Same script (OTP = `1234` for test card `4012000300000088`) |
| Native success view assertion (`Thanks for your order!`) | ✅ Covered | Same script |
| Card save & consent reuse | ❌ Not covered | Out of scope for ACE-597 P0 |

The bits we don't cover here are fully covered by the FE Playwright suite running in
a normal browser (`paymentacceptance-fe-automation-test`). The webview-specific risk
we own is `does checkout-ui mount + initialize in a WebView with the right runtime
config?` — that's exactly what this P0 catches.

## Files

```
flow_open_h5_webview.yaml             # reusable flow: cold-launch sample app → H5 demo → /shopping-cart
test_webview_checkout_renders.yaml    # P0: assert checkout-ui mounts + Pay button shows
test_webview_card_3ds_success.sh      # P1: full card flow incl. 3DS challenge (adb-driven, see below)
README.md                             # this file
```

The earlier YAML drafts (`flow_webview_pay_with_card.yaml`,
`flow_webview_handle_3ds.yaml`, `flow_webview_assert_result.yaml`,
`test_webview_card_save_and_reuse.yaml`, etc.) were removed once we discovered Maestro's
`inputText` does not trigger React `onChange` events for inputs nested in PCI cross-origin
iframes — see "Iframe limitation" below. The single surviving card flow,
`test_webview_card_3ds_success.sh`, drops down to raw `adb shell input` to work around
this; the iOS equivalent is the pure-Maestro
`airwallex-payment-ios/.maestro/WebViewCheckout/test_webview_card_3ds_success.yaml`
(WKWebView's `inputText` behaviour differs and works without the workaround).

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

# P0: WebView mount + Pay button (Maestro)
maestro test .maestro/WebViewCheckout/test_webview_checkout_renders.yaml

# P1: Full card flow incl. 3DS challenge (shell + adb)
.maestro/WebViewCheckout/test_webview_card_3ds_success.sh
```

Typical pass time on an M-series Mac with arm64 emulator:

| Test | Time | Stability |
|---|---|---|
| `test_webview_checkout_renders.yaml` (Maestro) | ~25s | 3/3 green |
| `test_webview_card_3ds_success.sh` (shell + adb) | ~210s | 2/2 green (test card `4012000300000088` Y-Y-SUCCESS, OTP `1234`) |

## Digital wallet sheet coverage

The P0 test asserts the Google Pay button is **rendered + tappable + the
checkout-ui click handler runs without crashing the WebView**. It does NOT
assert that the native Google Pay sheet actually appears.

### Why the default AVD can't show the sheet

We exhaustively confirmed this during bring-up by running the test on
**both** image variants:

| System image | `PaymentRequest.canMakePayment()` | GPay button click handler fires | Native sheet opens |
|---|---|---|---|
| `system-images;android-34;google_apis;arm64-v8a` (default in `webview_test` AVD) | ✅ true | ✅ analytics event in `adb logcat` (`button_name=google_pay`) | ❌ no provider available |
| `system-images;android-34;google_apis_playstore;arm64-v8a` (`webview_gpay` AVD) | ✅ true | ✅ same analytics event | ❌ Play Store present but **no Google account signed in + Google Wallet not yet installed** |

So the blocker is one extra environment-setup step, NOT a checkout-ui or
Maestro defect:

1. Use the playstore AVD (`webview_gpay`, created at
   `~/.android/avd/webview_gpay.avd`).
2. Open Play Store, sign in to a Google account that has a
   [Google Pay test merchant card](https://developers.google.com/pay/api/android/guides/test-and-deploy/test-card-suite)
   tokenized.
3. Install **Google Wallet** from Play.
4. Re-run the P0 — the GPay button tap will now trigger the native sheet.

### How to extend the test once the AVD is provisioned

Append after the existing GPay tap step in
`test_webview_checkout_renders.yaml`:

```yaml
- tapOn: "Google Pay"
- extendedWaitUntil:
    visible: "Continue"   # The GPay sheet's confirm button (system UI)
    timeout: 10000
- takeScreenshot: gpay_sheet_visible
- back               # Dismiss sheet without committing — we only assert appearance
```

`back` (Android system back) closes the GPay sheet and returns to
checkout-ui's "Select payment method" view, so the test stays
idempotent without going through the full payment flow.

### Why we left this out of the merged regression

The `google_apis_playstore` AVD requires interactive Google login + Wallet
install + test card setup, none of which are reproducible on shared CI
without baking a custom image with pre-seeded credentials. The failure
mode that "sheet doesn't appear" would catch (Payment Request bridge
broken in WebView) is already caught at the tap level — if the bridge
were broken, the click handler would throw and our
`assertVisible: "Select payment method"` post-condition would fail.

When PA stands up dedicated mobile CI hardware with pre-seeded wallets,
this is the very first follow-up.

## Iframe limitation & the `adb shell input` workaround

Airwallex's `cardElement` mounts each PCI-sensitive input (card number, expiry, CVC)
in its **own nested cross-origin iframe** served from `checkout.airwallex.com`. The
Android `WebView` *does* expose those inner `EditText` nodes to the OS accessibility
tree (`uiautomator dump` shows them), but Maestro's `inputText` action emits a
text-replacement event that gets to the DOM yet **never fires React's `onChange`
event** for those iframe-hosted inputs. The visible value updates but checkout-ui's
controlled-input state stays empty, so validation fails and Pay stays disabled.

What works vs what doesn't on Android WebView:

| Action | Outcome | Why |
|---|---|---|
| `assertVisible: "Card information"` (Maestro) | ✅ | Outer iframe text, fine via a11y |
| `tapOn point` on PAN/Expiry/CVC field (Maestro or adb) | ✅ | Tap focuses the input |
| `inputText: "401200..."` (Maestro) | ❌ | Updates DOM but skips React `onChange` |
| `adb shell input text "401200..."` (raw kernel events) | ✅ | Goes through InputConnection → IME → React |
| `tapOn { id: "cardnumber" }` (Maestro) | ❌ | HTML `name`/`id` not surfaced as accessibility id |

`test_webview_card_3ds_success.sh` codifies this: it uses Maestro for the
prologue (launch app → navigate to `H5DemoActivity` → tap Check out), then drops
to `adb shell input tap` + `adb shell input text` for every iframe-internal
interaction (PAN, Expiry, CVC, 3DS OTP, Submit), re-dumping the hierarchy
between steps so coordinates stay correct as the form reflows around the IME.

The iOS WKWebView equivalent
(`airwallex-payment-ios/.maestro/WebViewCheckout/test_webview_card_3ds_success.yaml`)
does NOT need the adb workaround — WKWebView's accessibility bridge feeds Maestro's
`inputText` events through the JS engine in a way that does trigger React `onChange`.

### Why we didn't pick the alternatives

1. **SDK-internal harness that posts a card token directly** — would test SDK
   bridging logic but not the actual `checkout-ui` field-input → tokenization
   path that real merchants hit.
2. **Custom `WebView` subclass with a JS bridge that exposes iframe internals
   to a11y** — invasive, ships test-only code in production binaries, and the
   FE team would have to keep the bridge contract in sync.
3. **Switch to Playwright Mobile / Detox / CDP** — already done outside this
   suite: the FE Playwright suite (`paymentacceptance-fe-automation-test`)
   covers card / 3DS / digital-wallet behaviour on `mobile-chrome` and
   `mobile-webkit` configurations. This Maestro+adb suite is the source of
   truth for "does checkout-ui mount + drive a real PI through the Android
   *native* `WebView`?".

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
