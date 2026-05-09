#!/bin/sh
# Lightweight mvnw shim: delegate to system mvn if available, otherwise instruct user.
if command -v mvn >/dev/null 2>&1; then
  mvn "$@"
else
  echo "Maven not found. Please install Maven or add the official Maven Wrapper (mvnw)."
  echo "You can also run: curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw && chmod +x mvnw"
  exit 1
fi
