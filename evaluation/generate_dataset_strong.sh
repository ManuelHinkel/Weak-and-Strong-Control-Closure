#!/bin/bash

OUT_DIR="data_strong"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=250
nMax=2001
nStep=250

pMin=0.70
pMax=0.91
pStep=0.1

pPMin=0.1
pPMax=0.31
pPStep=0.1

pFMin=0.1
pFMax=0.51
pFStep=0.4

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            for pF in $(seq "$pFMin" "$pFStep" "$pFMax"); do
              java -cp ./../target/classes de.ControlClosure.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime" "$pF"
            done
        done
    done
done