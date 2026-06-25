#!/bin/bash

C_SUB_DIR="$1"

INSTANCE_DIR="instances/${C_SUB_DIR}"

echo "$INSTANCE_DIR"

OUT_FILE="instance_results/${C_SUB_DIR}.csv"

echo "$OUT_FILE"

./compile.sh

sbatch --array=1-1 -n 1 -N 1 --exclusive -p infosun --constraint=chimaira --output="./${OUT_FILE}" --mem=32G  ./job_script.sh "$INSTANCE_DIR"
