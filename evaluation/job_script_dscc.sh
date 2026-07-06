#!/bin/bash

DATA_DIR="$1"
ALGORITHM="$2"
FLAG="$3"

files=("$DATA_DIR"/*)
#flags=("quadratic" "polylog")
flags=("quadratic")

group=$(( (SLURM_ARRAY_TASK_ID -1) / 1 ))
offset=$(( (SLURM_ARRAY_TASK_ID -1) % 1 ))


java -Xss512m -Xmx60g -cp ./../target/classes de.ControlClosure.Evaluation.Main  "${flags[offset]}" "${files[group]}" "1" "$ALGORITHM" "$FLAG"


