#!/bin/bash

C_SUB_DIR="$1"

INSTANCE_DIR="instances/${C_SUB_DIR}"

echo "$INSTANCE_DIR"

OUT_DIR="instance_results/${C_SUB_DIR}"

echo "$OUT_DIR"

n=$(ls $C_DIR -1 | wc -l)
sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=chimaira --output="./${OUT_DIR}/slurm-%A_%a.out" --mem=10G  ./job_script.sh "$INSTANCE_DIR"
