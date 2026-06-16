#!/bin/bash
# example: ./job_script_weak.sh "./data_weak"
# important! No '/' at the end of path

DATA_DIR="$1"
OUT_DIR="${DATA_DIR}_out"

files=("$DATA_DIR"/*)
flags=("cubic" "quadratic" "polylog")
#flags=("quadratic" "polylog")

group=$(( (SLURM_ARRAY_TASK_ID ) / 3 ))
offset=$(( (SLURM_ARRAY_TASK_ID ) % 3 ))

java -cp ./../target/classes de.ControlClosure.Weak.Main  "${flags[offset]}" "${files[group]}" "$OUT_DIR"
