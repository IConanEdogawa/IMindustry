#!/usr/bin/env bash
# Gradle wrapper stub. Run `gradle wrapper` to generate full wrapper files locally.
if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
else
  echo "Gradle is not installed. Please install Gradle or generate the wrapper by running 'gradle wrapper' locally." >&2
  exit 1
fi
