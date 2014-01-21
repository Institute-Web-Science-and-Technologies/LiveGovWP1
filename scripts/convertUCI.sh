#!/bin/bash

#Variables
FILES=(`ls | grep total_acc_`)
TEMP_FILES=()
NUM_FILES=${#FILES[@]}

if [[ $NUM_FILES != 3 ]]; then
  echo "No files found to convert"
  exit
fi

echo "Converting files..."

# Create separate file for each axis
for (( i = 0; i < $NUM_FILES; i++ )); do
  TEMP_FILES+=(${i}_convert.temp)
  `cat ${FILES[$i]} | tr -s ' ' | awk -F" " '{for (c=1; c <= 64; c++) print $c*9.81}' > ${TEMP_FILES[$i]}`
done

echo "Merging files..."

# merge files
`pr -m -t -s, ${TEMP_FILES[0]} ${TEMP_FILES[1]} ${TEMP_FILES[2]} | awk -F, 'BEGIN {ts=1386680000000}; {printf "UCI_HAR,%u,%g,%g,%g\n",ts,$1,$2,$3; ts+=20}' > out.csv`

echo "Cleanup..."

# remove temp files
for (( i = 0; i < $NUM_FILES; i++ )); do
  `rm ${TEMP_FILES[$i]}`
done
echo "Done!"