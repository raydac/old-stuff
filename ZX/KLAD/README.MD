In 1997 I found the Micro-C translator (written by Leon Obuhov in 1989 and based on Small C of R.Cain) which provided way to generate MASM x86 assembler files from some pseudo-C sources and because translator sources were provided I adapted them to generate Z80 assembler lines which could be processed by ASM80 of Vyacheslav Mednonogov. As a small demo I wrote a small arcade game, the game uses Laser Basic library decompiled and prepared by Eugeniy Mikhailov, sound was written by Alesander Yukhno. The Translator and the game both were published in FidoNet in 1997 as prebuilt binary files and mainly their sources were lost, I found some their parts on my old disks and restored build process for Linux, so that now it can be compiled (I am not sure that totally have repaired PC110 because I had only printed sources of PC107 version but the game can be compiled well).   

To be built under linux, PC110 needs:
* DOSBox emulator
* MASM 5.10
* TLINK 4.01

The game is written in LODE RUNNER style and mainly clone of well-known game "KLAD" from BK-0010, as a "killer feature" - there is "only attempt" provided for all labyrinths. The Game can be built under Linux with DOSBox emulator. It makes TRD file in TRGT folder which can be player in any ZX-Spectrum emulator supports TR-DOS images.

![Screencast of demo](https://raw.githubusercontent.com/raydac/old-stuff/master/ZX/KLAD/screencast.gif)
