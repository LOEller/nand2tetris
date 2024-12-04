// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.


    @0
    D=A 
    @2
    M=D // set R2 = 0
(LOOP)
    @0
    D=M 
    @END
    D;JEQ // jump to END if R0 == 0
    @2
    D = M 
    @1
    D = D + M 
    @2 
    M = D // R2 = R2 + R1
    @1
    D = A 
    @0 
    M = M - D // R0 = R0 - 1
    @LOOP
    0;JMP // go to loop start
(END)
    @END
    0;JMP



