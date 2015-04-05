#!/bin/bash
for filename in ./output/*.s; do 
  /u/cs444/bin/nasm -O1 -f elf -g -F dwarf $filename
done
ld -melf_i386 -o main output/*.o
