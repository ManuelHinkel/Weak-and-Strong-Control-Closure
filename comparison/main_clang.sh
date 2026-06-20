#!/bin/bash

C_SUB_DIR="$1"

C_DIR="c_files/${C_SUB_DIR}"

echo "$C_DIR"

OUT_DIR="results/${C_SUB_DIR}"

echo "$OUT_DIR"

n=$(ls $C_DIR -1 | wc -l)
sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=chimaira --output="./${OUT_DIR}/slurm-%A_%a.out" --mem=10G  ./job_script_clang.sh "$C_DIR"
