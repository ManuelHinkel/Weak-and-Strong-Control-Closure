#!/bin/bash

# example: ./generate_dataset_weak.sh "./data_weak"

OUT_DIR="data_worstcase"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

kMin=50
kMax=51
kStep=100


for ((k=kMin; k<kMax; k+=kStep)); do
  java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$k"
done