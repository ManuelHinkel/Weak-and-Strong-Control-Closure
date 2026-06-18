#!/bin/bash
# example: ./job_script_run_time.sh "./data_weak" "w"
# example: ./job_script_run_time.sh "./data_strong" "s"
DATA_DIR="$1"
ALGORITHM="$2"

files=("$DATA_DIR"/*)
flags=("cubic" "quadratic" "polylog")

group=$(( (SLURM_ARRAY_TASK_ID -1) / 3 ))
offset=$(( (SLURM_ARRAY_TASK_ID -1) % 3 ))

java -cp ./../target/classes de.ControlClosure.Weak.Main  "${flags[offset]}" "${files[group]}" "5" "${ALGORITHM}" ""

