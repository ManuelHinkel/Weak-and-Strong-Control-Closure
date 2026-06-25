#!/bin/bash

DATA_DIR="$1"


java -cp ./../target/classes de.ControlClosure.Evaluation.ComparisonRunner "$DATA_DIR" "20"
