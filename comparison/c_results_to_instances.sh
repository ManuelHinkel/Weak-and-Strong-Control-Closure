#!/bin/bash

C_SUB_DIR="$1"

RESULT_DIR="results/${C_SUB_DIR}"

echo "$RESULT_DIR"

OUT_DIR="instances/${C_SUB_DIR}"

echo "$OUT_DIR"

./compile.sh

 java -cp ./../target/classes de.ControlClosure.Evaluation.Parser "$RESULT_DIR" "$OUT_DIR"