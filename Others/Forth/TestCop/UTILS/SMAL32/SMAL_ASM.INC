
        ; =============================================================
                .386
                .model  tiny
                .code
                org     100h

__unit:         ; Start label

                db      'L32!'          ; Unit ID
                dd      0               ; Compiler version
                dd      0               ; Compile Date
                dd      100h+UNITHEADERSIZE
                __name_offset = $
                dd      0               ; Last NFA
                __init_offset = $
                dd      0               ; Init Word
                __size_offset = $
                dd      0               ; Module size
                dd      UNITHEADERSIZE
        ; =============================================================

endunit         macro   _label
                local   __endunit
                __endunit = $
                org     __name_offset
                dd      _prev_nfa
                ifnb    <_label>
                org     __init_offset
                dd      _label
                endif
                org     __size_offset
                dd      __endunit-UNITHEADERSIZE-100h
                org     __endunit
                end     __unit
                endm

gpf             macro
                mov     byte ptr cs:[0],0
                endm

fw              macro   _name,_label,_nlabel
                if      _fend_flag eq 0
                err     "FEND expected"
                endif
                align   4
                ifnb    <_nlabel>
                _nlabel&:
                endif
_nameoffset     =       $
                dd      0                       ;; HASH pointer
_rtbl_addr_off  =       $
                dd      0                       ;; RTABLE Pointer
                dd      0                       ;; Reserved field
                dd      _prev_nfa               ;; Link field
_prev_nfa       =       _nameoffset
_attroffset     =       $
                db      0,0,0,0                 ;; Attributes
_cfastorage     =       $
                dd      0                       ;; Code field address (1)
_codelength     =       $
                dd      0                       ;; Code length
                dd      vocabulary              ;; Vocabulary identifier
_namesize1      =       $
                db      0                       ;; Name length #1
                db      _name
_namesize       =       $-_namesize1-1
                db      0                       ;; Zero byte (NFA terminator)
_nameendoff     =       $
                org     _namesize1
                db      _namesize               ;; Name length #1 (2)
                org     _nameendoff
                ;; Additional fields
_addrrest       =       $
                align   4
_codestart      =       $
                org     _cfastorage
                dd      _codestart              ;; Code field address (2)
                org     _codestart
                ifnb    <_label>
                _label&:
                endif
_tail           =       0
_fend_flag      =       0
_attr_byte2     =       0
                endm

do              macro   _label
                org     _addrrest
                rept    3
_addrrest       =       $-_nameoffset
                if      3 ne (_addrrest and 3)
                nop
                endif
                endm
_codestart      =       $
                org     _cfastorage
                dd      _codestart              ;; Code field address (3)
                org     _codestart
                ifnb    <_label>
                _label&:
                endif
                ;; Next line will be replaced to "CALL NEST" instruction
                db      5 dup (90h)
_attr_byte2     =       _attr_byte2 or 8
_rtable_start   =       0
                endm

create          macro   _label
                org     _addrrest
                rept    3
_addrrest       =       $-_nameoffset
                if      3 ne (_addrrest and 3)
                nop
                endif
                endm
_codestart      =       $
                org     _cfastorage
                dd      _codestart              ;; Code field address (3)
                org     _codestart
                ifnb    <_label>
                _label&:
                endif
                ;; Next line will be replaced to "CALL NEXT" instruction
                db      5 dup (90h)
_attr_byte2     =       _attr_byte2 or 10h
_rtable_start   =       0
                endm

voc             macro   _label,_vocpfa
                org     _addrrest
                rept    3
_addrrest       =       $-_nameoffset
                if      3 ne (_addrrest and 3)
                nop
                endif
                endm
_codestart      =       $
                org     _cfastorage
                dd      _codestart              ;; Code field address (3)
                org     _codestart
                ifnb    <_label>
                _label&:
                endif
                ;; Next line will be replaced to "CALL VOCDOES" instruction
                db      5 dup (90h)
                ifnb    <_vocpfa>
                _vocpfa&:
                endif
                dd      0
_attr_byte2     =       _attr_byte2 or 20h
_rtable_start   =       0
                endm

does            macro   _label
                org     _addrrest
                rept    3
_addrrest       =       $-_nameoffset
                if      3 ne (_addrrest and 3)
                nop
                endif
                endm
_codestart      =       $
                org     _cfastorage
                dd      _codestart              ;; Code field address (3)
                org     _codestart
                ifnb    <_label>
                _label&:
                endif
_attr_byte2     =       _attr_byte2 or 28h
_rtable_start   =       0
                endm

fend            macro
_codeend        =       $
                org     _codelength
                dw      _codeend-_codestart
                org     _attroffset+1
                db      _tail,_attr_byte2
                org     _codeend
_fend_flag      =       1
                endm

rtable          macro
                align   4
_rtable_start   =       $
                org     _rtbl_addr_off
                dd      _rtable_start
                org     _rtable_start
                dd      0
                endm

rend            macro
_rtable_size    =       ($-_rtable_start-4) shr 2
_rtable_end     =       $
                org     _rtable_start
                dd      _rtable_size
                org     _rtable_end
                endm

immediate       macro
_tail           =       _tail or 128
                endm

tail_asciiz     macro
_tail           =       _tail or 3
                endm

tail_dword      macro
_tail           =       _tail or 4
                endm

tail_string     macro
_tail           =       _tail or 5
                endm

tail_dstring    macro
_tail           =       _tail or 7
                endm

tail_qword      macro
_tail           =       _tail or 8
                endm

tail_address    macro
_tail           =       _tail or 9
                endm

tail_cfa        macro
_tail           =       _tail or 10
                endm

next            macro
                add     esi,4
                jmp     dword ptr [esi-4]
                endm

ltsb            macro   _reg,_tsb_param
                mov     _reg,dword ptr ds:[tsbaddr]
                add     _reg,_tsb_param-tsb
                endm

api             macro   _func
                local   _retaddr,_jmpaddr
                mov     eax,_func
                push    cs
                call    _retaddr
                jmp     short _jmpaddr
                _retaddr:
                push    dword ptr ds:[118h]
                push    dword ptr ds:[114h]
                retf
                _jmpaddr:
                endm

fcall           macro   _reg
                local   _jmpaddr,_nextaddr
                call    _jmpaddr
                dd      0
                jmp     short _nextaddr
                _jmpaddr:
                pop     esi
                mov     dword ptr [esi],esi
                add     dword ptr [esi],4
                jmp     _reg
                _nextaddr:
                endm

_prev_nfa       =       0
_fend_flag      =       1
vocabulary      =       0

        ; =============================================================

upvoc           equ     00000016Ch
lastword        equ     000000170h
tsbaddr         equ     000000174h
tibaddr         equ     000000178h
sourceaddr      equ     00000017Ch
maxpos          equ     000000180h
inpos           equ     000000184h
state           equ     000000188h
stack_size      equ     00000018Ch
r_stack_size    equ     000000190h
l_stack_size    equ     000000194h
hash_table      equ     000000198h
restart         equ     00000019Ch
errproc         equ     0000001A0h
voc_link        equ     0000001A4h
pad_size        equ     0000001A8h
file_frame      equ     0000001ACh
file_buff_size  equ     0000001B0h
vram_selector   equ     0000001B4h
graph_selector  equ     0000001B8h
bios_selector   equ     0000001BCh
first_tsb       equ     0000001C8h
tsb             equ     0000001CCh
_@task_number   equ     0000001CCh
_@base          equ     0000001D0h
_@span          equ     0000001D4h
_@dpl           equ     0000001D8h
_@s0            equ     0000001DCh
_@r0            equ     0000001E0h
_@ls0           equ     0000001E4h
_@lsp           equ     0000001E8h
_@next_tsb      equ     0000001ECh
_@video_block   equ     000000208h
_@virt_vram_addr equ     000000208h
_@vram_xsize    equ     00000020Ch
_@vram_ysize    equ     000000210h
_@vram_char_size equ     000000214h
_@vram_line_size equ     000000218h
_@vram_x1       equ     00000021Ch
_@vram_y1       equ     000000220h
_@vram_x2       equ     000000224h
_@vram_y2       equ     000000228h
_@vram_cursor_x equ     00000022Ch
_@vram_cursor_y equ     000000230h
_@graph_color1  equ     000000234h
_@graph_color2  equ     000000238h
_@graph_font    equ     00000023Ch
_@video_end     equ     000000240h
_@current       equ     000000240h
_@context       equ     000000244h
_@errno         equ     000000284h
time_x          equ     0000002E4h
time_y          equ     0000002E8h
time_z          equ     0000002ECh
time_t          equ     0000002F0h
temp_bp         equ     0000002F4h
temp_sp         equ     0000002F8h
temp_si         equ     0000002FCh
var_i           equ     000000300h
var_j           equ     000000304h
var_k           equ     000000308h
var_l           equ     00000030Ch
var_m           equ     000000310h
var_n           equ     000000314h
var_r           equ     000000318h
var_s           equ     00000031Ch
putchar_addr    equ     0000002BCh
getchar_addr    equ     0000002C0h
getstat_addr    equ     0000002C4h
bye_addr        equ     0000002C8h
min_mem         equ     0000002DCh
max_mem         equ     0000002E0h
tsb_size        equ     0000000D0h
nvoc            equ     000000010h
UNITHEADERSIZE  equ     000000020h
ai_call         equ     000000138h
ai_next         equ     000000148h
ai_exit         equ     000000150h
_@task_priority equ     0000001F0h
_@task_status   equ     0000001F4h
_@task_esp      equ     0000001F8h
_@task_ebp      equ     0000001FCh
_@task_esi      equ     000000200h
_@user_data1    equ     00000028Ch
_@user_data2    equ     000000290h
_@user_data3    equ     000000294h
_@user_data4    equ     000000298h
_@all_stacks_mem equ     000000204h
dos_putchar     equ     000000324h
dos_getchar     equ     00000032Bh
dos_getstat     equ     000000347h
bios_putchar    equ     000000355h
bios_getchar    equ     00000035Ah
bios_getstat    equ     00000037Fh
null_getstat    equ     00000038Dh
null_putchar    equ     00000038Fh
null_getchar    equ     000000390h
