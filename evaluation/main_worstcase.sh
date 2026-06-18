#!/bin/bash

DATA_DIR="data_worstcase"

n=$(($(ls $DATA_DIR -1 | wc -l) * 2))
sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=thor --output="./out/slurm-%A_%a.out" --mem=10G  ./job_script_worstcase.sh "$DATA_DIR"