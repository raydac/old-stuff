;==============================================================
;Прошивка контроллера RSL на основе AT90S4414 при Fc=8MHz
;Кодовое имя: R-M-C79D90-1-113E
;Автор: И.А.Мазница
;(C)2000 All Copyright by Raydac Research Group URG
; URL: http://www.forth.org.ru/~rrg/
; E-Mail: rrg@forth.org.ru
;==============================================================
; PC7 - вывод синхроимпульсов
; PC6 - вывод информационных импульсов
; PC5 - ввод информационных импульсов
; PC4 - режим работы контроллера (1 - запись, 0 - чтение)
; PC0 - бит 0 из порта 0 на ввод
; PC1 - бит 1 из порта 0 на ввод
; PA0-PA5 - адрес требуемого порта
; PB0-PB4 - данные в порт (на ввод)
; PD0-PD3 - данные из порта (на вывод)
; PA6 - команда на запись данных в порт, активна в единице
; PA7 - вывод меандра с частотой 1 кГц
;==============================================================
; Ячейки памяти с 0-63 содержат записываемые в порты значения
; Ячейки памяти с 64-127 содержат считанные из портов значения
;==============================================================
; r20 - счетчик импульсов (0-15)
; r21 - счетчик пакетов (0-129)
; r22 - cчетчик сигнала частотой 1 кГц
; r0  - циклический регистр на ввод из канала
; r1  - циклический регистр на вывод в канал
; r23 - содержит байт на вывод в COP
; r24 - накапливает информацию с COP
; r16-r19 - регистры временного хранения
; r31,r30 - регистр Z для доступа из главной машины
; r27,r26 - регистр X для доступа к портам на чтение
; r29,r28 - регистр Y для доступа к портам на запись
; r2 - служебный порт с номером 0 (на запись)
;==============================================================
;Карта памяти
;64-127 - порты на запись (контроллер-COP) (0x60)
;128-191 - порты на считывание (COP-контроллер)(0xA0)
;==============================================================
; Служебный порт 0
; на запись
; бит 0 - флаг запуска звукового генератора (1 - запущен, 0 - отключен)
; бит 1 - Флаг адресации записи считывания (0 - стандарт, 1 - обратное)
; бит 2 - Флаг разрешения записи-считывания из портов COP (0-разрешено,1-запрещено)
; бит 3 - Инициализация индикатора положения ( порт 0=0b11)
; бит 4 - Запрещение генерации звуковой частоты и вывод на линию информации из бита 0
; на чтение
; бит 0 - Сигнал с ввода PC0
;==============================================================


.include "4414def.inc"

.MACRO SetTimerOffset
	LDI r19,207	; Смещение для получения опорной частоты 20 кГц
	OUT TCNT0,r19
.ENDMACRO

	.ORG 0
	RJMP reset
	RJMP reset
	RJMP reset
	RJMP reset
	RJMP reset
	RJMP reset
	RJMP reset
	RJMP tim1_ovf
	
reset:
	CLI
	CLR r20
	CLR r21
	LDI r16,1
	MOV r0,r16
	MOV r1,r16
	; Выставляем направление у порта A
	SBI DDRA,PA7 ; Вывод 7 на вывод
	CBI PORTA,PA7

	CLR r19
	OUT PORTA,r19
	OUT PORTB,r19

	
	; Выставляем направление выводов у порта D
	LDI r19,0b1111
	OUT DDRD,r19

	; Выставляем направление 

	; Выставляем направления выводов у порта C
	SBI DDRC,PC7 ; Вывод 7 на вывод
	SBI DDRC,PC6 ; Вывод 6 на вывод
	SBI DDRC,PC4 ; Вывод 4 на вывод

	CBI PORTC,PC7
	CBI PORTC,PC6
	CBI PORTC,PC4

	CBI PORTC,PC3
	CBI PORTC,PC2
	CBI PORTC,PC1
	CBI PORTC,PC0

	; Инициализация регистров X,Y
	CLR r27
	LDI r26,0x60 ; X на область памяти "запись в порт"
	CLR r29
	LDI r28,0xA0 ; Y на область памяти "чтение из порта"

	; Инициализация накопителя и циклического регистра
	CLR r24

	; Выставляем указатель стека
	LDI r16,0x50
	OUT SPL,r16
	LDI r16,0x1
	OUT SPH,r16

	; Инициализируем все порты нулем
	CLR r16 ; Инициализируем счетчик нулем
	CLR r31
	LDI r30,0x60
	CLR r17


init_loop:
	ST Z+,r17
	CPI r16,127
	BREQ exit_init
	INC r16
	RJMP init_loop

exit_init:
	CLR r2 ; Очищаем служебный порт 0
	CLR r3
	OUT MCUCR,r3
	OUT GIMSK,r3

	; Разрешаем прерывания	
	; Выставляем смещение таймера 
	SetTimerOffset	
	LDI r19,(1<<TOIE0)
	OUT TIMSK,r19
	LDI r19,(1<<CS01)	; Выставляем у таймера TC0 коэффициент деления 8 и запускаем его
	OUT TCCR0,r19
	SEI

main_loop:	

	NOP
	NOP
	RJMP main_loop

tim1_ovf:
	SetTimerOffset 	; Выставляем начальное смезщение таймера

; Проверяем сигнал записи/считывания на PA6
	IN r16,PINA
	MOV r17,r16
	ANDI r17,0b00111111 ; адрес порта
	BST r16,6
	BRTS wr_port

rd_port:
	TST r17
	BRNE rd_nozeroport
	; Формируем содержимое порта 0 на чтение
	IN r17,PINC
	ANDI r17,0b1111
	OUT PORTD,r17
	RJMP end_test_rw

rd_nozeroport:
	CLR r31
	BST r2,1
	BRTS rd_mem0
	LDI r30,0xA0
	RJMP rd_mem1
rd_mem0:
	LDI r30,0x60
rd_mem1:
	ADD r30,r17
	LD r16,Z
	ANDI r16,0b1111
	OUT PORTD,r16
	RJMP end_test_rw

wr_port:
	IN r16,PINB
	ANDI r16,0b11111
	TST r17
	BRNE wr_nozeroport
	MOV r2,r16
	BST r16,3
	BRTS init_indicators
	CLR r16
	RJMP wr_nozeroport

init_indicators:
	LDI r16,0b11		

	
wr_nozeroport:
	BST r2,1
	BRTC normal_mode
	LDI r30,0xA0
	RJMP save2z
normal_mode:
	LDI r30,0x60
save2z:
	ADD r30,r17
	ST Z,r16

end_test_rw:


; Формируем синхроимпульсы
	CPI r21,2 		; Выявляем синхропакеты
	BRSH no_synchro
	CBI PORTC,PC7 		; Сбрасываем синхроимпульс
	RJMP end_common	

no_synchro:	
	CPI r20,2
	BRSH lbl_00
	SBI PORTC,PC7
	RJMP lbl_01
lbl_00: 
	CBI PORTC,PC7
lbl_01:
	
; Получаем и передаем данные
 	CPI r21,66 		; Выявляем тип цикла (прием/передача)
	BRSH read_cycle

; Цикл передачи
	SBI PORTC,PC4	
	CPI r20,4
	BRLO wrlbl_00
	CPI r20,14
	BRSH wrlbl_00
	MOV r13,r24
	AND r13,r0
	BREQ write_no_signal
	SBI PORTC,PC6
	RJMP end_common

write_no_signal:
	CBI PORTC,PC6
	RJMP end_common

wrlbl_00:
	CBI PORTC,PC6
	RJMP end_common

read_cycle:
	CBI PORTC,PC4
	MOV r16,r21
	SUBI r16,2
	ANDI r16,63
	CBI PORTC,PC6
	CPI r20,4
	BRLO rdlbl_00
	CPI r20,14
	BRSH rdlbl_00
	
	; Считываем состояние в накопитель по маске в r1 при нечетном сосотоянии r20
	MOV r16,r20
	ANDI r16,1
	BREQ rdlbl_00
	IN r16,PINC
	ANDI r16,(1<<PC5)
	BREQ rc_zero
	OR r24,r1
	RJMP rdlbl_00
rc_zero:
	LDI r16,0xFF
	EOR r1,r16
	AND r24,r1
	EOR r1,r16
	
rdlbl_00:

end_common:
	INC r22
	CPI r22,20
	BRLO nomore39
	CLR r22
nomore39:

; Выясняем режим работы звукового генератора
	BST r2,4
	BRTC normal_soundmode
	; Специальный режим работы генератора, на линию выводится состояние бита 0	
	BST r2,0
	BRTC soundoff
	SBI PORTA,PA7
	RJMP end_common1	

	; Нормальный режим работы генератора, при выставлении бита 0 в 1 выв. сигнал с частотой 1 кГц
normal_soundmode:
	CPI r22,10
	BRLO soundoff
soundon:
	BST r2,0
	BRTC soundoff
	SBI PORTA,PA7
	RJMP end_common1
soundoff:
	CBI PORTA,PA7

end_common1:
	INC r20
	CPI r20,16
	BRLO elbl_00
	
	LDI r19,1
	MOV r0,r19
	MOV r1,r19
	CLR r20

	CPI r21,66
	BRSH ec_rd_cycle

ec_wr_cycle:
	CPI r21,1
	BRLO ec_lblend
	LD r24,X+
	RJMP ec_lblend

ec_rd_cycle:
	BST r2,2
	BRTS ec_rd_cycle0
	ST Y+,r24
	RJMP ec_lblend

ec_rd_cycle0:
	ADIW r28,1

ec_lblend:
	INC r21
	CPI r21,130
	BRLO elbl_00
	CLR r21
	CLR r27
	CLR r29
	LDI r26,0x60
	LDI r28,0xA0

elbl_00:
	CPI r20,6
	BRLO elbl_01
	CPI r20,14
	BRSH elbl_01
	MOV r19,r20
	ANDI r19,1	
	TST r19
	BRNE elbl_02
	LSL r0
	RJMP elbl_01

elbl_02:
	LSL r1	

elbl_01:
	RETI