#!/bin/bash

START_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

rm -rf $START_DIR/BUILD
mkdir $START_DIR/BUILD
mkdir $START_DIR/BUILD/AVR

echo build ATMEL AVR ROM, requiring installed AVRA package!
cp $START_DIR/SRC/ATMEL/*.* $START_DIR/BUILD/AVR
cd $START_DIR/BUILD/AVR
avra TST4414.ASM
rm -f $START_DIR/BUILD/AVR/*.ASM
rm -f $START_DIR/BUILD/AVR/*.inc



echo build TSTCOP.EXE
dosbox -C "MOUNT D $START_DIR" -C "D:" -C "D:\UTILS\SMAL32\FORTH.EXE D:\SRC\TSTCOP.F32" -C "EXIT"
