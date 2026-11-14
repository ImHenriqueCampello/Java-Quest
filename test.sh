#!/usr/bin/env bash
set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

mkdir -p out/test
javac -cp "lib/junit-platform-console-standalone.jar:src" -d out/test $(find src test -name "*.java")
java -cp "out/test:lib/junit-platform-console-standalone.jar:src" test.TestRunner "$@"
