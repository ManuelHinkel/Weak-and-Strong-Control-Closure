#!/bin/bash

OUT_DIR="data_worst"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

kMin=1000
kMax=20001
kStep=1000


for ((k=kMin; k<kMax; k+=kStep)); do
  echo "$k"
  java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$k"
done