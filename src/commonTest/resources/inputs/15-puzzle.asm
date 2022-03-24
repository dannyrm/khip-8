cls
LD vC 0
snei vC 0
LD vE 15
iLD label-12
LD v0 32
store v0
cls
call label-5
call label-1
call label-2
call label-16
call label-15
jump label-3
LD v1 0
LD v2 23
LD v3 4
snei v1 16
return
iLD label-9
iaddr v1
load v0
snei v0 0
jump label-14
digit v0
sprite v2 v3 5
addi v1 1
addi v2 5
LD v4 3
movr v4 v1
sei v4 0
jump label-13
LD v2 23
addi v3 6
jump label-13
LD v4 3
movr v4 vE
LD v5 3
movr v5 vD
or v4 v5
return
snei v4 3
return
LD v4 1
movr v4 vE
call label-4
jump label-15
LD v4 3
movr v4 vE
LD v5 3
movr v5 vD
or v4 v5
return
snei v4 0
return
LD v4 255
movr v4 vE
call label-4
jump label-16
label-1:
LD v4 12
movr v4 vE
LD v5 12
movr v5 vD
or v4 v5
return
snei v4 0
return
LD v4 252
movr v4 vE
call label-4
jump label-1
label-2:
label-3:
LD v4 12
movr v4 vE
LD v5 12
movr v5 vD
or v4 v5
return
snei v4 12
return
LD v4 4
movr v4 vE
call label-4
jump label-2
label-4:
iLD label-9
iaddr v4
load v0
iLD label-9
iaddr vE
store v0
LD v0 0
iLD label-9
iaddr v4
store v0
movr vE v4
return
label-5:
sei vC 0
jump label-6
call label-11
call label-7
call label-11
iLD label-10
iaddr vD
load v0
movr vD v0
return
label-6:
addi vC 255
rnd vD 15
return
label-7:
addi vD 1
LD v0 15
movr vD v0
skr vD
jump label-7
label-8:
snkr vD
jump label-8
return
label-9:
dw 0x102 ; 258 ; 0b100000010
dw 0x304 ; 772 ; 0b1100000100
dw 0x506 ; 1286 ; 0b10100000110
dw 0x708 ; 1800 ; 0b11100001000
dw 0x90A ; 2314 ; 0b100100001010
dw 0xB0C ; 2828 ; 0b101100001100
dw 0xD0E ; 3342 ; 0b110100001110
dw 0xF00 ; 3840 ; 0b111100000000
label-10:
label-11:
label-12:
label-13:
label-14:
label-15:
label-16:
dw 0xD00 ; 3328 ; 0b110100000000
dw 0x102 ; 258 ; 0b100000010
dw 0x405 ; 1029 ; 0b10000000101
dw 0x608 ; 1544 ; 0b11000001000
dw 0x90A ; 2314 ; 0b100100001010
dw 0xC0E ; 3086 ; 0b110000001110
dw 0x307 ; 775 ; 0b1100000111
dw 0xB0F ; 2831 ; 0b101100001111
movr v4 vE
call label-4
jump label-1
LD v4 12
movr v4 vE
LD v5 12
movr v5 vD
or v4 v5
return
snei v4 12
return
LD v4 4
movr v4 vE
call label-4
jump label-2
iLD label-9
iaddr v4
load v0
iLD label-9
iaddr vE
store v0
LD v0 0
iLD label-9
iaddr v4
store v0
movr vE v4
return
sei vC 0
jump label-6
call label-11
call label-7
call label-11
iLD label-10
iaddr vD
load v0
movr vD v0
return
addi vC 255
rnd vD 15
return
addi vD 1
LD v0 15
movr vD v0
skr vD
jump label-7
snkr vD
jump label-8
return
dw 0x102 ; 258 ; 0b100000010
dw 0x304 ; 772 ; 0b1100000100
dw 0x506 ; 1286 ; 0b10100000110
dw 0x708 ; 1800 ; 0b11100001000
dw 0x90A ; 2314 ; 0b100100001010
dw 0xB0C ; 2828 ; 0b101100001100
dw 0xD0E ; 3342 ; 0b110100001110
dw 0xF00 ; 3840 ; 0b111100000000
dw 0xD00 ; 3328 ; 0b110100000000
dw 0x102 ; 258 ; 0b100000010
dw 0x405 ; 1029 ; 0b10000000101
dw 0x608 ; 1544 ; 0b11000001000