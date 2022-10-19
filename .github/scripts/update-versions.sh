#!/usr/bin/env bash
set -euo pipefail

if [ -z "${VERSION}" ]; then
  echo "Missing version."
  exit 1
fi

echo "Updating version to $VERSION."

find . -type f -name "*.md" | xargs sed -i 's/\(io.github.airwallex:[^:]*:\)[^"'\'']*/\1'"$VERSION"'/g'
sed -i 's/\(version = '\''\)[^'\'']*/\1'"$VERSION"'/g' ./build.gradle