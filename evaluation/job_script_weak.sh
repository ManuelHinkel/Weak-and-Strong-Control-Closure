#!/bin/bash
# example: ./job_script_weak.sh "./data_weak"

DATA_DIR="$1"

files=("$DATA_DIR"/*)
flags=("cubic" "quadratic" "polylog")

group=$(( (SLURM_ARRAY_TASK_ID ) / 3 ))
offset=$(( (SLURM_ARRAY_TASK_ID ) % 3 ))

java -cp ./../target/classes de.ControlClosure.Weak.Main  "${flags[offset]}" "${files[group]}"
