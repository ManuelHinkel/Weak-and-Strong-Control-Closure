#!/bin/bash

#data_weak_all
#data_strong_all
#data_weak_dscc
#data_strong_dscc

OUT_DIR="$1"

echo "$OUT_DIR"

mkdir -p "$OUT_DIR"
find "$OUT_DIR" -mindepth 1 -delete

nMin=100000
nMax=500000
nStep=10000

pMin=0.75
pMax=1.01
pStep=0.25

pPMin=0.1
pPMax=0.11
pPStep=0.3

pFMin=0.0
pFMax=0.0
pFStep=1.0

for ((i=nMin; i<nMax; i+=nStep)); do
    for p in $(seq "$pMin" "$pStep" "$pMax"); do
        for pPrime in $(seq "$pPMin" "$pPStep" "$pPMax"); do
            for pF in $(seq "$pFMin" "$pFStep" "$pFMax"); do
              echo "$i" "$p" "$pPrime" "$pF"
              java -cp ./../target/classes de.ControlClosure.Evaluation.GraphGenerator "$OUT_DIR" "$i" "$p" "$pPrime" "$pF"
            done
        done
    done
done
