#!/bin/bash

DATA_DIR="$1"
FILE="$2"

mkdir -p "$(dirname "$FILE")"

cat "$DATA_DIR"/* > "$FILE"