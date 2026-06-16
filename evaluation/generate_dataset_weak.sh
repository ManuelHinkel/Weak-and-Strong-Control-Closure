#!/bin/bash

OUT_DIR="data_weak"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=2000
nMax=2001
nStep=100

pMin=0.90
pMax=0.91
pStep=0.3

pPMin=0.2
pPMax=0.21
pPStep=0.2

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime"
        done
    done
done