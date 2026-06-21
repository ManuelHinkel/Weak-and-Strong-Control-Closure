#!/bin/bash

C_SUB_DIR="$1"

INSTANCE_RESULT_DIR="instance_results/${C_SUB_DIR}"

echo "$INSTANCE_RESULT_DIR"

./compile.sh

 java -cp ./../target/classes de.ControlClosure.Evaluation.Summation "$INSTANCE_RESULT_DIR"