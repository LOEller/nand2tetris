// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.


    
(INPUT)
    @KBD
    D=M
    @CLEAR 
    D;JEQ // go to CLEAR if there is no keyboard input
    @FILL 
    0;JMP // go to FILL if there is a keyboard input
(FILL)
    @SCREEN
    D=A
    @1
    M=D // store base screen address in R1
(FILL_LOOP)
    @1
    D=M
    @24575
    D=D-A
    @INPUT
    D;JGT // go back to INPUT if R1 > 24575 (screen is full)
    @KBD
    D=M
    @CLEAR 
    D;JEQ // if the keyboard input has stopped go right to CLEAR
    @1
    A=M 
    M=-1 // fill in the next pixel
    @1
    D=A
    @1
    M=M+D // increment R1
    @FILL_LOOP
    0;JMP
(CLEAR)
    @24575
    D=A
    @1
    M=D // store last screen address in R1
(CLEAR_LOOP)
    @1
    D=M
    @SCREEN
    D=D-A
    @INPUT
    D;JLT // go back to INPUT if R1 < SCREEN (screen is clear)
    @1
    A=M 
    M=0 // clear the next pixel
    @1
    D=A
    @1
    M=M-D // decrement R1
    @CLEAR_LOOP
    0;JMP
