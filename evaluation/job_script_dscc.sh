#!/bin/bash

DATA_DIR="$1"
ALGORITHM="$2"
FLAG="$3"

files=("$DATA_DIR"/*)
flags=("quadratic" "polylog")

group=$(( (SLURM_ARRAY_TASK_ID -1) / 2 ))
offset=$(( (SLURM_ARRAY_TASK_ID -1) % 2 ))

java -Xmx64g -cp ./../target/classes de.ControlClosure.Evaluation.Main  "${flags[offset]}" "${files[group]}" "1" "$ALGORITHM" "$FLAG"

