#!/bin/bash

DATA_DIR="$1"

files=("$DATA_DIR"/*)

i=$((SLURM_ARRAY_TASK_ID-1))
java -cp ./../target/classes de.ControlClosure.Evaluation.ComparisonRunner "${files[i]}" "20"