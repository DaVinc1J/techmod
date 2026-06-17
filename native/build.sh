#!/usr/bin/env bash
# Compiles native/src/*.c into a shared library and drops it into
# src/main/resources/natives/<platform>/, where NativeCore.java expects to
# find it as a jar resource.
#
# Requires JAVA_HOME to point at a JDK that has jni.h (any JDK 8+ works for
# the header; doesn't need to match the JDK you run Minecraft with).
set -euo pipefail

cd "$(dirname "$0")"

if [[ -z "${JAVA_HOME:-}" ]]; then
    echo "JAVA_HOME is not set. Point it at your JDK, e.g.:" >&2
    echo "  export JAVA_HOME=/usr/lib/jvm/java-25-openjdk" >&2
    exit 1
fi

if [[ ! -f "$JAVA_HOME/include/jni.h" ]]; then
    echo "Couldn't find jni.h under \$JAVA_HOME/include ($JAVA_HOME/include)." >&2
    echo "Make sure JAVA_HOME points at a JDK (not a JRE)." >&2
    exit 1
fi

UNAME_S="$(uname -s)"
UNAME_M="$(uname -m)"

case "$UNAME_M" in
    aarch64|arm64) ARCH="aarch64" ;;
    x86_64|amd64)  ARCH="x86_64" ;;
    *) echo "Unrecognized architecture: $UNAME_M" >&2; exit 1 ;;
esac

CC="${CC:-cc}"
COMMON_FLAGS=(-shared -fPIC -O2 -Wall -Wextra -Inative/include -I"$JAVA_HOME/include")

case "$UNAME_S" in
    Linux)
        PLATFORM_DIR="linux-${ARCH}"
        OUT_NAME="libtechmod.so"
        COMMON_FLAGS+=(-I"$JAVA_HOME/include/linux")
        ;;
    Darwin)
        PLATFORM_DIR="macos-${ARCH}"
        OUT_NAME="libtechmod.dylib"
        COMMON_FLAGS+=(-I"$JAVA_HOME/include/darwin")
        ;;
    *)
        echo "This script doesn't handle $UNAME_S yet (Windows: build with MinGW/MSVC and drop the .dll into src/main/resources/natives/windows-x86_64/ by hand)." >&2
        exit 1
        ;;
esac

OUT_DIR="../src/main/resources/natives/${PLATFORM_DIR}"
mkdir -p "$OUT_DIR"

echo "Compiling for ${PLATFORM_DIR} with ${CC}..."
"$CC" "${COMMON_FLAGS[@]}" src/techmod.c -o "${OUT_DIR}/${OUT_NAME}"

echo "Built ${OUT_DIR}/${OUT_NAME}"
