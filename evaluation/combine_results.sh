#!/bin/bash

# example: ./combine_results.sh "./data_weak_out"

DATA_DIR="$1"

java -cp ./../target/classes de.ControlClosure.IOUtils  "${DATA_DIR}" "./res"