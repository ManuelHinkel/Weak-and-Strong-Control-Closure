#!/bin/bash

OUT_DIR="data_weak"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=100
nMax=501
nStep=100

pMin=0.80
pMax=0.91
pStep=0.1

pPMin=0.2
pPMax=0.31
pPStep=0.1

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime"
        done
    done
done