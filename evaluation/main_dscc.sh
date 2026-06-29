#!/bin/bash

#example ./main_dscc.sh [w | s | worst}  ['' | scc] (last flag determines runtime or scc statistics eval)

ALGORITHM="$1"
FLAG="$2"

if [ "$ALGORITHM" == "w" ]; then
  DATA_DIR="data_weak_dscc"
elif [ "$ALGORITHM" == "s" ]; then
  DATA_DIR="data_strong_dscc"
else
  DATA_DIR="data_worst"
  ALGORITHM="w"
fi

echo "$DATA_DIR"

OUT_DIR="${DATA_DIR}_out"

echo "$OUT_DIR"

#./compile.sh

n=$(($(ls $DATA_DIR -1 | wc -l) * 1))
sbatch --array=1-$n -n 1 -N 1 --exclusive -p infosun --constraint=chimaira --output="./${OUT_DIR}/slurm-%A_%a.out" --mem=255G  ./job_script_dscc.sh "$DATA_DIR" "$ALGORITHM" "$FLAG"
