#!/usr/bin/env bash
#
# Pre-test setup: writes the sample app's `Environment` SharedPref directly via
# `adb run-as`, bypassing the in-app restart flow added in PR #313.
#
# Why: PR #313 makes env switching call `Process.killProcess`, which leaves the
# Maestro on-device driver wedged for the rest of the session. Pre-seeding the
# pref avoids that path entirely — the SDK reads the new env at app init.
#
# Usage:
#   .maestro/setup-env.sh [ENVIRONMENT] [DEVICE_ID]
#   .maestro/setup-env.sh DEMO
#   .maestro/setup-env.sh STAGING emulator-5554
#
# Requires a debug build of the sample app installed on the device.

set -euo pipefail

NEW_ENV="${1:-DEMO}"
DEVICE="${2:-$("$ANDROID_HOME/platform-tools/adb" devices | awk 'NR==2 && $2=="device" {print $1}')}"
PKG="com.airwallex.paymentacceptance"
PREFS="shared_prefs/${PKG}_preferences.xml"
ADB="$ANDROID_HOME/platform-tools/adb -s $DEVICE"

if [[ -z "$DEVICE" ]]; then
    echo "ERROR: no connected device found" >&2
    exit 1
fi

$ADB shell am force-stop "$PKG"

if $ADB shell "run-as $PKG test -f $PREFS" 2>/dev/null; then
    $ADB shell "run-as $PKG sh -c 'sed -i \"s|<string name=\\\"Environment\\\">[^<]*</string>|<string name=\\\"Environment\\\">$NEW_ENV</string>|\" $PREFS'"
else
    $ADB shell "run-as $PKG sh -c 'mkdir -p shared_prefs && cat > $PREFS'" <<EOF
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="Environment">$NEW_ENV</string>
</map>
EOF
fi

CURRENT=$($ADB shell "run-as $PKG cat $PREFS" | grep -oE 'name="Environment">[^<]+' | cut -d'>' -f2)
echo "Set Environment=$CURRENT for $PKG on $DEVICE"
