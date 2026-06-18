#!/bin/bash

DATA_DIR="$1"
ALGORITHM="$2"

files=("$DATA_DIR"/*)
flags=("quadratic" "polylog")

group=$(( (SLURM_ARRAY_TASK_ID -1) / 2 ))
offset=$(( (SLURM_ARRAY_TASK_ID -1) % 2 ))

java -cp ./../target/classes de.ControlClosure.Weak.Main  "${flags[offset]}" "${files[group]}" "5" "${ALGORITHM}" "scc"

