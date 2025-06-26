#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Run the application with the correct classpath and native library path
java -Dorg.lwjgl.librarypath="${SCRIPT_DIR}/build/libs" \
     -cp "${SCRIPT_DIR}/build/classes/java/main:${SCRIPT_DIR}/build/resources/main:${SCRIPT_DIR}/build/libs/*" \
     com.half.Main "$@"
