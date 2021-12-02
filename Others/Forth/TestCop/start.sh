#!/bin/bash

echo starting TSTCOP.EXE
#dosbox -C "MOUNT D $PWD" -C "D:\UTILS\RK.COM" -C "D:\BUILD\TESTCOP.EXE demo-mode" -C "exit"
dosbox -forcescaler normal2x -C "MOUNT D $PWD" -C "D:\UTILS\RK.COM" -C "D:\BUILD\TESTCOP.EXE demo-mode" -C "exit"
