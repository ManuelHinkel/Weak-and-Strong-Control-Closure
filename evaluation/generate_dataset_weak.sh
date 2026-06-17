#!/bin/bash

OUT_DIR="data_weak"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=250
nMax=3001
nStep=250

pMin=0.50
pMax=0.91
pStep=0.1

pPMin=0.1
pPMax=0.41
pPStep=0.1

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime"
        done
    done
done
