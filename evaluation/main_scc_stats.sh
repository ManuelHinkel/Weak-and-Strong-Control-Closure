#!/bin/bash

ALGORITHM="$1"

if [ "$ALGORITHM"="w" ]; then
  DATA_DIR="data_weak"
else
  DATA_DIR="data_strong"
fi

n=$(($(ls $DATA_DIR -1 | wc -l) * 2))
sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=thor --output="./out/slurm-%A_%a.out" --mem=10G  ./job_script_scc_stats.sh "$DATA_DIR" "$ALGORITHM"