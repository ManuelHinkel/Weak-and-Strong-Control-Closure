#!/bin/bash

DATA_DIR="$1"

files=($(find $DATA_DIR -name "*.c"))

i=$((SLURM_ARRAY_TASK_ID-1))
./../clang-cda "${files[i]}" --dl=l2

