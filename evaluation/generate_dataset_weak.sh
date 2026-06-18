#!/bin/bash

OUT_DIR="data_weak"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=250
nMax=2501
nStep=250

pMin=0.60
pMax=0.91
pStep=0.15

pPMin=0.1
pPMax=0.71
pPStep=0.3

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime"
        done
    done
done
