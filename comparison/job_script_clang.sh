#!/bin/bash

DATA_DIR="$1"

files=("$DATA_DIR"/*)

i=$((SLURM_ARRAY_TASK_ID-1))
./../clang-cda "${files[i]}" --dl=l2

