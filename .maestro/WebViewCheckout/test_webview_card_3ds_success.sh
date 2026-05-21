#!/usr/bin/env bash
# Full card flow on Android WebView — test card 4012000300000088 (Y-Y-SUCCESS):
#   DDC + 3DS challenge SUCCESS (OTP = 1234)
#
# WHY THIS IS A SHELL SCRIPT, NOT A MAESTRO YAML
# ----------------------------------------------
# Maestro's `inputText` action on Android WebView does NOT trigger React's
# `onChange` event for inputs nested inside Airwallex's PCI cross-origin
# iframes (card number / expiry / CVC are each in their own
# checkout.airwallex.com iframe). The text appears in the DOM but
# checkout-ui's controlled-input state never updates, so validation fails
# and the Pay button stays disabled.
#
# `adb shell input text` (raw kernel-level key events) DOES correctly fire
# native InputConnection commits that the WebView's input dispatcher routes
# through ime-side update chains — which in turn drive React's onChange.
#
# Maestro is still used for the navigation prologue (open H5DemoActivity →
# launch /shopping-cart → tap Check out), then this script drives the
# iframe-internal interaction via adb directly.
#
# Prerequisites:
#   - adb in PATH, emulator booted (the `webview_test` AVD set up per README)
#   - Maestro CLI in PATH (only needed for the prologue flow)
#   - Sample app installed: `:sample:assembleDebug` → adb install
#
# Usage:
#   .maestro/WebViewCheckout/test_webview_card_3ds_success.sh
#
# Exit codes:
#   0 = full E2E success (3DS challenge passed + native success view shown)
#   1 = any step failed (see stderr + screenshots in $SHOT_DIR)
set -euo pipefail

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
REPO_ROOT="$( cd "$SCRIPT_DIR/../.." && pwd )"
SHOT_DIR="${SHOT_DIR:-/tmp/android_card_flow}"
mkdir -p "$SHOT_DIR"

log() { printf '\033[36m[card-flow]\033[0m %s\n' "$*"; }
err() { printf '\033[31m[card-flow ERROR]\033[0m %s\n' "$*" >&2; }

shot() {
  adb shell screencap -p /sdcard/_s.png >/dev/null 2>&1
  adb pull /sdcard/_s.png "$SHOT_DIR/$1.png" >/dev/null 2>&1
  log "  screenshot → $SHOT_DIR/$1.png"
}

# Re-dump UI hierarchy + return center coords of node matching a python filter.
# Usage: find_center "lambda n: n.get('text')=='Pay 100.00 CNY'"
find_center() {
  local filter="$1"
  adb shell uiautomator dump /sdcard/_d.xml >/dev/null 2>&1
  adb pull /sdcard/_d.xml /tmp/_d.xml >/dev/null 2>&1
  python3 - "$filter" <<'PY'
from xml.etree import ElementTree as ET
import sys
filter_src = sys.argv[1]
flt = eval(filter_src)
root = ET.parse('/tmp/_d.xml').getroot()
for n in root.iter('node'):
    if flt(n):
        b = n.get('bounds', '')
        import re
        m = re.match(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', b)
        if m:
            x1,y1,x2,y2 = map(int, m.groups())
            print(f"{(x1+x2)//2} {(y1+y2)//2}")
            sys.exit(0)
sys.exit(2)
PY
}

# ----- 0. Open HPP via Maestro flow ---------------------------------------------
log "0/8 Opening HPP via Maestro prologue"
maestro test "$SCRIPT_DIR/flow_open_h5_webview.yaml" >/dev/null
# After prologue: H5WebViewActivity is on "Shopping cart" page
# Tap Check out via Maestro (which uses content-desc selector reliably)
cat > /tmp/_open_hpp_tail.yaml <<'EOF'
appId: com.airwallex.paymentacceptance
---
- tapOn: "Check out"
- extendedWaitUntil:
    visible: "Card information"
    timeout: 30000
EOF
maestro test /tmp/_open_hpp_tail.yaml >/dev/null
shot 00_hpp_loaded
log "  HPP loaded"

# ----- 1. Fill PAN --------------------------------------------------------------
log "1/8 Tap PAN field"
PAN_XY=$(find_center "lambda n: n.get('class')=='android.widget.EditText' and n.get('text','')=='' and n.get('bounds','').startswith('[154,14')")
read PAN_X PAN_Y <<< "$PAN_XY"
log "  PAN center: ($PAN_X, $PAN_Y)"
adb shell input tap "$PAN_X" "$PAN_Y"
sleep 1
adb shell input text "4012000300000088"
sleep 1
shot 01_pan_typed

# ----- 2. Fill Expiry -----------------------------------------------------------
log "2/8 Tap Expiry + type 1155"
# After PAN typed + keyboard up: first empty EditText is Expiry
EXP_XY=$(find_center "lambda n: n.get('class')=='android.widget.EditText' and n.get('text','')==''")
read EXP_X EXP_Y <<< "$EXP_XY"
log "  Expiry center: ($EXP_X, $EXP_Y)"
adb shell input tap "$EXP_X" "$EXP_Y"
sleep 1
adb shell input text "1155"
sleep 1
shot 02_expiry_typed

# ----- 3. Fill CVC --------------------------------------------------------------
log "3/8 Tap CVC + type 153"
# After Expiry typed: form auto-advances and CVC is the next empty EditText
CVC_XY=$(find_center "lambda n: n.get('class')=='android.widget.EditText' and n.get('text','')==''")
read CVC_X CVC_Y <<< "$CVC_XY"
log "  CVC center: ($CVC_X, $CVC_Y)"
adb shell input tap "$CVC_X" "$CVC_Y"
sleep 1
adb shell input text "153"
sleep 1
shot 03_cvc_typed

# ----- 4. Tap Pay --------------------------------------------------------------
log "4/8 Tap Pay 100.00 CNY (with keyboard still up; tap fires through)"
PAY_XY=$(find_center "lambda n: n.get('text','')=='Pay 100.00 CNY'")
read PAY_X PAY_Y <<< "$PAY_XY"
log "  Pay center: ($PAY_X, $PAY_Y)"
adb shell input tap "$PAY_X" "$PAY_Y"
sleep 3
shot 04_pay_clicked

# ----- 5. Wait for 3DS challenge -----------------------------------------------
log "5/8 Wait for 3DS challenge to load (up to 30s)"
for i in {1..30}; do
  if find_center "lambda n: n.get('text','')=='Submit'" >/dev/null 2>&1; then
    log "  3DS challenge visible after ${i}s"
    break
  fi
  sleep 1
done
shot 05_3ds_loaded

# ----- 6. Type OTP --------------------------------------------------------------
log "6/8 Type OTP 1234 into challenge"
OTP_XY=$(find_center "lambda n: n.get('resource-id','')=='challengeDataEntry'")
read OTP_X OTP_Y <<< "$OTP_XY"
log "  OTP input center: ($OTP_X, $OTP_Y)"
adb shell input tap "$OTP_X" "$OTP_Y"
sleep 1
adb shell input text "1234"
sleep 1
shot 06_otp_typed

# ----- 7. Submit ----------------------------------------------------------------
log "7/8 Tap Submit"
SUB_XY=$(find_center "lambda n: n.get('text','')=='Submit'")
read SUB_X SUB_Y <<< "$SUB_XY"
log "  Submit center: ($SUB_X, $SUB_Y)"
adb shell input tap "$SUB_X" "$SUB_Y"
sleep 5
shot 07_submit_clicked

# ----- 8. Assert native success view -------------------------------------------
log "8/8 Wait for native success view (up to 30s)"
for i in {1..30}; do
  if adb shell uiautomator dump /sdcard/_d.xml >/dev/null 2>&1 \
     && adb pull /sdcard/_d.xml /tmp/_d.xml >/dev/null 2>&1 \
     && grep -q "Thanks for your order" /tmp/_d.xml; then
    log "  Native success view visible after ${i}s"
    shot 08_SUCCESS
    log ""
    log "================ CARD FLOW PASS ================"
    log "  Test card:    4012000300000088"
    log "  3DS OTP:      1234"
    log "  Screenshots:  $SHOT_DIR/"
    log "================================================"
    exit 0
  fi
  sleep 1
done

shot 08_FAILED
err "Did not reach 'Thanks for your order!' within 30s after Submit"
err "See screenshots in $SHOT_DIR/"
exit 1
