#!/bin/bash

# example: ./main_all.sh [w | s]

ALGORITHM="$1"

if [ "$ALGORITHM" == "w" ]; then
  DATA_DIR="data_weak_all"
else
  DATA_DIR="data_strong_all"
fi

echo "$DATA_DIR"

OUT_DIR="${DATA_DIR}_out"

echo "$OUT_DIR"

n=$(($(ls $DATA_DIR -1 | wc -l) * 3))
sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=thor --output="./${OUT_DIR}/slurm-%A_%a.out" --mem=10G  ./job_script_all.sh "$DATA_DIR" "$ALGORITHM"