#!/bin/bash

DATA_DIR="$1"


java -Xss512m -Xmx60g -cp ./../target/classes de.ControlClosure.Evaluation.ComparisonRunner "$DATA_DIR" "20"
