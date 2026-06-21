#!/bin/bash

OUT_DIR="c_files/custom_generated"

echo "$OUT_DIR"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=250
nMax=2501
nStep=250

pMin=0.60
pMax=0.91
pStep=0.15

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
          echo "$i" "$p"
          java -cp ./../target/classes de.ControlClosure.Evaluation.CFileGenerator "$OUT_DIR" "$i" "$p"
    done
done