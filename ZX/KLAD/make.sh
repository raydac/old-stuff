#!/bin/bash

mkdir $PWD/TRGT
rm -rf $PWD/TRGT/*.*

echo Translating LASER.LIB with PC110
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\PC110\\PC110A.COM LASER.LIB LASER.A80 > TRGT\\LASER.OUT" -c "exit"

echo Translating KLAD with PC110
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\PC110\\PC110A.COM KLAD_D.C KLAD_D.A80 > TRGT\\KLAD_D.OUT" -c "exit"

echo Compiling KLADDEMO with ASM80
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\ASM80.EXE KLADDEMO.A80 > TRGT\\KLADDEMO.OUT" -c "exit"

echo Saving all files to TRD
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\EMPTYTRD.EXE TRGT\\\KLADD.TRD" -c "exit"

dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\HOB2TRD.EXE BIN\\BOOTRG.\$B TRGT\\KLADD.TRD" -c "exit"
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\HOB2TRD.EXE BIN\\KLADD.\$B TRGT\\KLADD.TRD" -c "exit"
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\HOB2TRD.EXE BIN\\KL_SCR.\$C TRGT\\KLADD.TRD" -c "exit"
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "TOOLS\\HOB2TRD.EXE KLADDEMO.\$C TRGT\\KLADD.TRD" -c "exit"

echo clean
if [ -f $PWD/KLADDEMO.\$C ]
then
    rm -rf ./KLAD_D.A80
    rm -rf ./LASER.A80
    rm -rf ./KLADDEMO.LST
    rm -rf ./KLADDEMO.\$C
fi

