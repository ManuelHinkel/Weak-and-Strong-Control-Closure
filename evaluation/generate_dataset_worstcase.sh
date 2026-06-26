#!/bin/bash

OUT_DIR="data_worst"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

kMin=10000
kMax=60001
kStep=2000


for ((k=kMin; k<kMax; k+=kStep)); do
  echo "$k"
  java -cp ./../target/classes de.ControlClosure.Evaluation.GraphGenerator "$OUT_DIR" "$k"
done
