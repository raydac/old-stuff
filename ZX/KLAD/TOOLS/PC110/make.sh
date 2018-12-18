#!/bin/bash

rm -rf $PWD/PC110A.COM

echo Here you need MICRO-C0.COM
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "MICRO-C0.COM PC110A.C PC110A.ASM > MICROC.TXT" -c "exit"

echo Here you need MASM 5.10
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c "MASM.EXE PC110A.ASM,PC110A.OBJ,Nul.lst,nul.crf > MASM.TXT" -c "exit"

echo Here you need TLINK 4.01
dosbox -c "cycles max" -c "MOUNT D $PWD" -c "d:" -c  "TLINK.EXE /t PC110A.OBJ,PC110A.COM,Nul.lst  > TLINK.TXT" -c "exit"


if [ -f $PWD/PC110A.COM ]; then
    rm -rf $PWD/MICROC.TXT $PWD/PC110A.ASM $PWD/PC110A.OBJ $PWD/MASM.TXT $PWD/TLINK.TXT
fi

