#!/bin/bash

#Variables
FILES=(`ls | grep total_acc_`)
Y_TRAIN=../`ls .. | grep y_`
ACTIVITY_LABELS=(WALKING STAIRS SITTING STANDING LAYING)
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
  `cat ${FILES[$i]} | tr -s ' ' > ${TEMP_FILES[$i]}.clean`
  `pr -m -t -s" " -w4096 ${Y_TRAIN} ${TEMP_FILES[$i]}.clean | tr -s ' ' | awk -F" " '{for (c=2; c <= 65; c++) printf "%u,%g\n",$1,$c*9.81}' > ${TEMP_FILES[$i]}`
done

echo "Merging files..."
# merge files
`pr -m -t -s, ${TEMP_FILES[0]} ${TEMP_FILES[1]} ${TEMP_FILES[2]} | awk -F, 'BEGIN {
    al[1]="WALKING"
    al[2]="STAIRS"
    al[3]="STAIRS"
    al[4]="SITTING"
    al[5]="STANDING"
    al[6]="LAYING";ts=1386680000000}; {printf "%s,UCI_HAR,%s,%g,%g,%g\n",al[$1],ts,$2,$4,$6; ts+=20}' > out.csv`

echo "Building directory tree"

`mkdir Output`
for (( i = 0; i < ${#ACTIVITY_LABELS[@]}; i++ )); do
  `mkdir Output/${ACTIVITY_LABELS[$i]}`
  `cat out.csv | grep ${ACTIVITY_LABELS[$i]} | awk -F, '{printf "%s,%s,%g,%g,%g\n",$2,$3,$4,$5,$6}' > Output/${ACTIVITY_LABELS[$i]}/UCI_HAR`
done

echo "Cleanup..."

# remove temp files
for (( i = 0; i < $NUM_FILES; i++ )); do
  `rm ${TEMP_FILES[$i]} ${TEMP_FILES[$i]}.clean`
done
echo "Done!"