#!/usr/bin/env bash
#
# Ensure the airwallex sample app's "Environment" SharedPreferences value
# matches $TARGET before any Maestro test runs. Bypasses the Settings UI
# entirely, so this works regardless of driver state, at fresh install, and
# without triggering the in-app Process.killProcess that the env-change UI
# flow runs (PR #313).
#
# Usage:
#   .maestro/scripts/ensure-env.sh <TARGET_ENV> [DEVICE_ID]
#
# Required:
#   TARGET_ENV   One of DEMO, PREVIEW, STAGING, PRODUCTION. No default — caller
#                must be explicit so CI and local don't drift silently.
#
# Optional:
#   DEVICE_ID    adb device id (e.g. emulator-5554). If omitted, adb picks the
#                only-attached device, or errors if multiple are attached.
#
# Branches:
#   - prefs file missing (fresh install) → bootstrap-launch, write fresh XML
#   - Environment key missing            → insert before </map>
#   - Environment present, wrong value   → sed-swap
#   - Environment already matches        → no-op, returns immediately
#
# Exits non-zero on hard failures (non-debuggable build, adb missing).

set -euo pipefail

if [ "${1:-}" = "" ]; then
  cat >&2 <<EOF
ensure-env.sh — set Airwallex sample app SharedPreferences "Environment".

usage:   $0 <TARGET_ENV> [DEVICE_ID]
example: $0 DEMO emulator-5554
         $0 PREVIEW

TARGET_ENV must be one of: DEMO PREVIEW STAGING PRODUCTION
EOF
  exit 2
fi

TARGET="$1"
DEVICE="${2:-}"
PKG=com.airwallex.paymentacceptance
PREFS=/data/data/$PKG/shared_prefs/${PKG}_preferences.xml

case "$TARGET" in
  DEMO|PREVIEW|STAGING|PRODUCTION) ;;
  *)
    echo "[ensure-env] ERROR: unknown TARGET '$TARGET' (expected DEMO|PREVIEW|STAGING|PRODUCTION)" >&2
    exit 2
    ;;
esac

adb_d() {
  if [ -n "$DEVICE" ]; then adb -s "$DEVICE" "$@"; else adb "$@"; fi
}

# Sanity: run-as requires a debuggable build
if ! adb_d shell "run-as $PKG id" >/dev/null 2>&1; then
  echo "[ensure-env] ERROR: $PKG is not debuggable on this device." >&2
  echo "[ensure-env] CI must install the debug APK, not release." >&2
  exit 1
fi

CONTENT=$(adb_d shell "run-as $PKG cat $PREFS" 2>/dev/null || true)

wait_for_main() {
  until adb_d shell dumpsys window 2>/dev/null | grep -q 'mCurrentFocus.*MainActivity'; do
    sleep 0.3
  done
}

write_fresh() {
  printf "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n    <string name=\"Environment\">%s</string>\n</map>\n" "$TARGET" \
    | adb_d shell "run-as $PKG sh -c 'cat > $PREFS'"
}

if [ -z "$CONTENT" ]; then
  # Fresh install: shared_prefs/ may not exist. Launch once to let Android
  # create the dir, then write our XML.
  echo "[ensure-env] fresh install — bootstrapping & writing $TARGET"
  adb_d shell am start -n $PKG/.ui.MainActivity >/dev/null
  wait_for_main
  adb_d shell am force-stop $PKG
  write_fresh

elif echo "$CONTENT" | grep -q '<string name="Environment">'; then
  CURRENT=$(echo "$CONTENT" | sed -n 's|.*<string name="Environment">\([^<]*\)</string>.*|\1|p')
  if [ "$CURRENT" = "$TARGET" ]; then
    echo "[ensure-env] already $TARGET — no change"
    # Still ensure the app is on MainActivity for whatever runs next
    adb_d shell am start -n $PKG/.ui.MainActivity >/dev/null
    wait_for_main
    exit 0
  fi
  adb_d shell am force-stop $PKG
  echo "$CONTENT" \
    | sed "s|<string name=\"Environment\">[^<]*</string>|<string name=\"Environment\">$TARGET</string>|" \
    | adb_d shell "run-as $PKG sh -c 'cat > $PREFS'"
  echo "[ensure-env] swapped $CURRENT → $TARGET"

else
  echo "[ensure-env] inserting Environment=$TARGET (key was absent)"
  adb_d shell am force-stop $PKG
  echo "$CONTENT" \
    | sed "s|</map>|    <string name=\"Environment\">$TARGET</string>\n</map>|" \
    | adb_d shell "run-as $PKG sh -c 'cat > $PREFS'"
fi

# Cold-restart so Application#onCreate consumes the new value
adb_d shell am start -n $PKG/.ui.MainActivity >/dev/null
wait_for_main
echo "[ensure-env] done — app on MainActivity, Environment=$TARGET"
