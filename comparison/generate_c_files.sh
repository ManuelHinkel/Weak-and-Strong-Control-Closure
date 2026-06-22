#!/bin/bash

OUT_DIR="c_files/custom_generated"

echo "$OUT_DIR"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=250
nMax=5001
nStep=250

pMin=0.90
pMax=0.91
pStep=0.15

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
      for ((k=0; k<20; k+=1)); do
        echo "$i" "$p" "$k"
        java -cp ./../target/classes de.ControlClosure.Evaluation.CFileGenerator "$OUT_DIR" "$i" "$p" "$k"
      done
    done
done
