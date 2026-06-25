#!/bin/bash

OUT_DIR="data_worst"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

kMin=40000
kMax=80001
kStep=2000


for ((k=kMin; k<kMax; k+=kStep)); do
  echo "$k"
  java -cp ./../target/classes de.ControlClosure.Evaluation.GraphGenerator "$OUT_DIR" "$k"
done
