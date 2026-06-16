#!/bin/bash

#start obe slurm job for each file in directory

javac -d ./../target/classes $(find ./../src/main/java -name "*.java")

#DATA_DIR = "data_weak"

#n=$(ls $DATA_DIR -1 | wc -l) * 3
#sbatch --array=1-$n -n 1 -N 1 --exclusive=user -p infosun --constraint=chimaira --output="./out/slurm-%A_%a.out" --mem=10G  ./job_script_weak.sh "$DATA_DIR"