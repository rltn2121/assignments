#include "includes.h"

#define F_CPU	16000000UL		// CPU frequency = 16 Mhz
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#define UCHAR unsigned char		// UCHAR 정의
#define USHORT unsigned short	// USHORT 정의
#define TASK_STK_SIZE  OS_TASK_DEF_STK_SIZE
#define N_TASKS        5
#define ATS75_ADDR 0x98			// 0b10011000, 7비트를 1비트 left shift
#define ATS75_CONFIG_REG 1
#define ATS75_TEMP_REG 0

#define ON 1					// 버저음 출력을 위해 파형 생성
#define OFF 0

#define DO 17					// 낮은 도
#define UDO 137					// 높은 도

OS_STK		TaskStk[N_TASKS][TASK_STK_SIZE];
OS_EVENT* Mbox;					// 측정한 온도, 조도 전달
OS_EVENT* sem;
OS_FLAG_GRP* f_tmp;				// SW1 눌리면 temperature task에게 전달
OS_FLAG_GRP* f_lit;				// SW2 눌리면 light task에게 전달
OS_FLAG_GRP* f_tmp_disp;		// delivery task에서 temperature display task에게 전달
OS_FLAG_GRP* f_lit_disp;		// delivery task에서 light display task에게 전달

volatile USHORT	measured_value; // 측정한 온도, 조도 저장
volatile char mode = 't';		// 현재 모드 저장 (온도 또는 조도)
volatile int state = OFF;		// 버저음 출력
UCHAR FND_DATA[] = {
	0x3f, // 0
	0x06, // 1
	0x5b, // 2
	0x4f, // 3
	0x66, // 4
	0x6d, // 5
	0x7d, // 6
	0x27, // 7
	0x7f, // 8
	0x6f, // 9
	0x77, // A
	0x7c, // B
	0x39, // C
	0x5e, // D
	0x79, // E
	0x71, // F
	0x80, // .
	0x40, // -
	0x08, // _
	0x00  // NULL
};
UCHAR fnd_sel[4] = { 0x01, 0x02, 0x04, 0x08 };

void TemperatureReadTask(void* data);	// 온도 측정 task
void LightReadTask(void* data);			// 조도 측정 task

void DeliveryTask(void* data);			// 측정한 온도 또는 조도 전달 받고, display task에게 알려줌
void TemperatureDisplayTask(void* data);// 온도 출력
void LightDisplayTask(void* data);		// 조도 출력

void showTempLed(USHORT value);			// LED에 온도 출력
void showTempFnd(USHORT value);			// FND에 온도 출력
void showLightLed(USHORT value);		// LED에 조도 출력
void showLightFnd(USHORT value);		// FND에 조도 출력

void write_twi_1byte_nopreset(UCHAR reg, UCHAR data);
void write_twi_0byte_nopreset(UCHAR reg);

ISR(TIMER2_OVF_vect) {
	INT8U err;
	if (state == ON) {			// 버저음 파형 생성
		PORTB = 0x00;
		state = OFF;
	}
	else {
		PORTB = 0x10;
		state = ON;
	}
	OSSemPend(sem, 0, &err);
	if (mode == 't')			// SW1 눌리면 '낮은 도' 출력 (온도 모드)
		TCNT2 = DO;

	else if (mode == 'l')		// SW2 눌리면 '높은 도' 출력 (조도 모드)
		TCNT2 = UDO;
	OSSemPost(sem);
}

ISR(INT4_vect) {
	INT8U	err;
	OS_ENTER_CRITICAL();

	OSSemPend(sem, 0, &err);
	mode = 't';					// SW1 누르면 온도 모드로 변경
	measured_value = 0;			// 측정 값 초기화
	OSSemPost(sem);

	TIMSK |= 0x40;				// 10ms 동안 버저음 출력
	_delay_ms(10);
	TIMSK &= ~0x40;

	OSFlagPost(f_tmp, 0x01, OS_FLAG_SET, &err);	// temperature task 에게 모드 변경을 알림


	OS_EXIT_CRITICAL();
}

ISR(INT5_vect) {
	INT8U	err;
	OS_ENTER_CRITICAL();

	OSSemPend(sem, 0, &err);
	mode = 'l';					// SW2 누르면 조도 모드로 변경
	measured_value = 0;			// 측정 값 초기화
	OSSemPost(sem);

	TIMSK |= 0x40;				// 10ms 동안 버저음 출력
	_delay_ms(10);
	TIMSK &= ~0x40;

	OSFlagPost(f_lit, 0x01, OS_FLAG_SET, &err);	// light task 에게 모드 변경을 알림

	OS_EXIT_CRITICAL();
}

int main(void)
{
	OSInit();
	OS_ENTER_CRITICAL();
	// 레지스터 입출력 설정
	DDRA = 0xff;
	DDRB = 0x10;
	DDRE = 0xCF;
	DDRC = 0xff;
	DDRG = 0x0f;

	// 인터럽트 관련 레지스터 설정
	TCCR0 = 0x07;
	TCNT0 = 256 - (CPU_CLOCK_HZ / OS_TICKS_PER_SEC / 1024);
	TCCR2 = 0x03;
	TCNT2 = DO;
	TIMSK = _BV(TOIE0);
	EICRB = 0x0A;
	EIMSK = 0x30;
	SREG |= 1 << 7;
	OS_EXIT_CRITICAL();

	// ECB 생성
	INT8U err;
	Mbox = OSMboxCreate((void*)0);
	sem = OSSemCreate(1);
	f_tmp = OSFlagCreate(0x00, &err);
	f_lit = OSFlagCreate(0x00, &err);
	f_tmp_disp = OSFlagCreate(0x00, &err);
	f_lit_disp = OSFlagCreate(0x00, &err);

	// TASK 생성
	OSTaskCreate(TemperatureReadTask, (void*)0, (void*)&TaskStk[0][TASK_STK_SIZE - 1], 0);
	OSTaskCreate(LightReadTask, (void*)0, (void*)&TaskStk[1][TASK_STK_SIZE - 1], 1);
	OSTaskCreate(DeliveryTask, (void*)0, (void*)&TaskStk[2][TASK_STK_SIZE - 1], 2);
	OSTaskCreate(TemperatureDisplayTask, (void*)0, (void*)&TaskStk[3][TASK_STK_SIZE - 1], 3);
	OSTaskCreate(LightDisplayTask, (void*)0, (void*)&TaskStk[4][TASK_STK_SIZE - 1], 4);
	OSStart();
	return 0;
}

void InitI2C()
{
	PORTD = 3; 						// For Pull-up override value
	SFIOR &= ~(1 << PUD); 			// PUD
	TWSR = 0; 						// TWPS0 = 0, TWPS1 = 0
	TWBR = 32;						// for 100  K Hz bus clock
	TWCR = _BV(TWEA) | _BV(TWEN);	// TWEA = Ack pulse is generated
									// TWEN = TWI 동작을 가능하게 한다
}
void init_adc() {
	ADMUX = 0x00;
	// 00000000
	// REFS(1:0) = "00" AREF(+5V) 기준전압 사용
	// ADLAR = '0' 디폴트 오른쪽 정렬
	// MUX(4:0) = "00000" ADC0 사용, 단극 입력
	ADCSRA = 0x87;
	// 10000111
	// ADEN = '1' ADC를 Enable
	// ADFR = '0' single conversion 모드
	// ADPS(2:0) = "111" 프리스케일러 128분주
}

USHORT read_adc() {
	UCHAR adc_low, adc_high;
	USHORT value;
	ADCSRA |= 0x40;						// ADC start conversion, ADSC = '1'
	while ((ADCSRA & (0x10)) != 0x10);	// ADC 변환 완료 검사
	adc_low = ADCL;
	adc_high = ADCH;
	value = (adc_high << 8) | adc_low;

	return value;
}
void show_adc(USHORT value) {
	int temp = 1024 / 8;
	int count = value / temp;
	int i;
	for (i = 0; i < 8; i++) {
		if (i <= count)
			PORTA |= 1 << i;
		else
			PORTA &= ~(1 << i);
	}
}
void write_twi_1byte_nopreset(UCHAR reg, UCHAR data)
{
	TWCR = (1 << TWINT) | (1 << TWSTA) | (1 << TWEN); // START 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || ((TWSR & 0xf8) != 0x08 && (TWSR & 0xf8) != 0x10)); // ACK를 기다림
	TWDR = ATS75_ADDR | 0;  // SLA+W 준비, W=0
	TWCR = (1 << TWINT) | (1 << TWEN);  // SLA+W 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xf8) != 0x18);
	TWDR = reg;    // aTS75 Reg 값 준비
	TWCR = (1 << TWINT) | (1 << TWEN);  // aTS75 Reg 값 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xF8) != 0x28);
	TWDR = data;    // DATA 준비
	TWCR = (1 << TWINT) | (1 << TWEN);  // DATA 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xF8) != 0x28);
	TWCR = (1 << TWINT) | (1 << TWSTO) | (1 << TWEN); // STOP 전송
}
void write_twi_0byte_nopreset(UCHAR reg)
{
	TWCR = (1 << TWINT) | (1 << TWSTA) | (1 << TWEN); // START 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || ((TWSR & 0xf8) != 0x08 && (TWSR & 0xf8) != 0x10));  // ACK를 기다림
	TWDR = ATS75_ADDR | 0; // SLA+W 준비, W=0
	TWCR = (1 << TWINT) | (1 << TWEN);  // SLA+W 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xf8) != 0x18);
	TWDR = reg;    // aTS75 Reg 값 준비
	TWCR = (1 << TWINT) | (1 << TWEN);  // aTS75 Reg 값 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xF8) != 0x28);
	TWCR = (1 << TWINT) | (1 << TWSTO) | (1 << TWEN); // STOP 전송
}
USHORT ReadTemperature(void)
{
	UCHAR high_byte, low_byte;
	TWCR = (1 << TWINT) | (1 << TWSTA) | (1 << TWEN); // START 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || ((TWSR & 0xf8) != 0x08 && (TWSR & 0xf8) != 0x10));  // ACK를 기다림
	TWDR = ATS75_ADDR | 1;  // SLA+R 준비, R=1
	TWCR = (1 << TWINT) | (1 << TWEN);  // SLA+R 전송
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xf8) != 0x40);
	TWCR = (1 << TWINT) | (1 << TWEN | 1 << TWEA);// 1st DATA 준비
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xf8) != 0x50);
	high_byte = TWDR;    // 1 byte DATA 수신
	TWCR = (1 << TWINT) | (1 << TWEN | 1 << TWEA);// 2nd DATA 준비 
	while (((TWCR & (1 << TWINT)) == 0x00) || (TWSR & 0xf8) != 0x50);
	low_byte = TWDR;    // 1 byte DATA 수신
	TWCR = (1 << TWINT) | (1 << TWSTO) | (1 << TWEN); // STOP 전송

	return ((high_byte << 8) | low_byte);  // 수신 DATA 리턴
}

void showTempLed(USHORT value) {
	UCHAR value_int;
	int i;
	int mask = 1;
	int led_val = 0;
	value_int = (UCHAR)((value & 0x7f00) >> 8);
	PORTA = 0;
	for (i = 1; i <= 8; i++) {		// 측정한 온도를 2진수로 출력 
		if (value_int & mask)
			led_val += mask;
		mask = 1 << i;
	}
	PORTA = led_val;
}
void showTempFnd(USHORT value) {
	int i;

	UCHAR value_int, value_deci, num[4];
	if ((value & 0x8000) != 0x8000)  // Sign 비트 체크
		num[3] = 19;
	else
	{
		num[3] = 17;
		value = (~value) - 1;		// 2’s Compliment
	}

	value_int = (UCHAR)((value & 0x7f00) >> 8);
	value_deci = (UCHAR)(value & 0x00ff);

	num[2] = (value_int / 10) % 10;
	num[1] = value_int % 10;
	num[0] = ((value_deci & 0x80) == 0x80) * 5;

	for (i = 0; i < 4; i++)			// 측정한 온도를 0.5 단위로 출력
	{
		PORTC = FND_DATA[num[i]];
		PORTG = fnd_sel[i];
		if (i == 1)
			PORTC |= 0x80;
		_delay_ms(2);
	}
}
void showLightLed(USHORT value) {
	show_adc(value);				// 측정한 조도를 단계별로 출력
}
void showLightFnd(USHORT value) {
	int i;
	UCHAR num[4];
	num[3] = (value / 1000) % 10;
	num[2] = (value / 100) % 10;
	num[1] = (value / 10) % 10;
	num[0] = value % 10;

	for (i = 0; i < 4; i++)			// 측정한 조도 출력
	{
		PORTC = FND_DATA[num[i]];
		PORTG = fnd_sel[i];
		_delay_ms(2);
	}

}

void TemperatureReadTask(void* data)
{
	INT8U	err;
	char current_mode;
	USHORT	value = 0;

	data = data;
	InitI2C();

	write_twi_1byte_nopreset(ATS75_CONFIG_REG, 0x00); // 9비트, Normal
	write_twi_0byte_nopreset(ATS75_TEMP_REG);
	while (1) {
		OSSemPend(sem, 0, &err);
		current_mode = mode;		// 현재 모드 확인
		OSSemPost(sem);

		if (current_mode != 't')	// 온도 모드가 아니면 Event flag 이용해서 대기
			OSFlagPend(f_tmp, 0x01, OS_FLAG_WAIT_SET_ALL + OS_FLAG_CONSUME, 0, &err);

		OS_ENTER_CRITICAL();
		value = ReadTemperature();			// 온도 읽기
		OSMboxPost(Mbox, (void*)&value);	// 공유변수 measured_value에 온도 전달
		OS_EXIT_CRITICAL();

		OSTimeDly(100);
	}
}

void LightReadTask(void* data)
{
	INT8U	err;
	char current_mode;
	USHORT	value = 0;

	data = data;
	init_adc();

	while (1) {
		OSSemPend(sem, 0, &err);
		current_mode = mode;		// 현재 모드 확인
		OSSemPost(sem);

		if (current_mode != 'l')	// 조도 모드가 아니면 Event flag 이용해서 대기
			OSFlagPend(f_lit, 0x01, OS_FLAG_WAIT_SET_ALL + OS_FLAG_CONSUME, 0, &err);

		OS_ENTER_CRITICAL();
		value = read_adc();					// 조도 읽기
		OSMboxPost(Mbox, (void*)&value);	// 공유변수 measured_value에 조도 전달
		OS_EXIT_CRITICAL();

		OSTimeDly(100);
	}
}

void DeliveryTask(void* data)
{
	INT8U	err;
	data = data;
	char current_mode;
	while (1) {
		OS_ENTER_CRITICAL();

		OSSemPend(sem, 0, &err);
		current_mode = mode;		// 현재 모드 확인
		OSSemPost(sem);
		measured_value = *(USHORT*)OSMboxPend(Mbox, 0, &err); // 측정한 값 저장

		if (current_mode == 't')	// 현재 모드에 따라서 적절한 display task에게 event flag 전달
			OSFlagPost(f_tmp_disp, 0x01, OS_FLAG_SET, &err);
		else
			OSFlagPost(f_lit_disp, 0x01, OS_FLAG_SET, &err);

		OS_EXIT_CRITICAL();
		OSTimeDly(100);
	}
}

void TemperatureDisplayTask(void* data)
{
	INT8U	err;
	USHORT value;
	char current_mode;

	data = data;

	while (1) {
		OSSemPend(sem, 0, &err);
		current_mode = mode;		// 현재 모드 확인
		value = measured_value;		// 측정한 값 확인
		OSSemPost(sem);

		if (current_mode != 't')	// 온도 모드가 아니면 대기
			OSFlagPend(f_tmp_disp, 0x01, OS_FLAG_WAIT_SET_ALL + OS_FLAG_CONSUME, 0, &err);
		OS_ENTER_CRITICAL();
		showTempLed(value);			// LED, FND에 온도 출력
		showTempFnd(value);
		OS_EXIT_CRITICAL();
	}
}
void LightDisplayTask(void* data)
{
	INT8U	err;
	USHORT value;
	char current_mode;

	data = data;


	while (1) {
		OSSemPend(sem, 0, &err);
		current_mode = mode;		// 현재 모드 확인
		value = measured_value;		// 측정한 값 확인
		OSSemPost(sem);

		if (current_mode != 'l')	// 조도 모드가 아니면 대기
			OSFlagPend(f_lit_disp, 0x01, OS_FLAG_WAIT_SET_ALL + OS_FLAG_CONSUME, 0, &err);
		OS_ENTER_CRITICAL();
		showLightLed(value);		// LED, FND에 온도 출력
		showLightFnd(value);
		OS_EXIT_CRITICAL();
	}
}
