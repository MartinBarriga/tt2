
#include <SoftwareSerial.h>

#include "MAX30100_PulseOximeter.h"
 
#define REPORTING_PERIOD_MS     1000

/* Programa el modulo bluetooth HC-06 con un nuevo: 
  NOMBRE  (Nombre de 20 caracteres)
  PIN     (Clave de cuatro numeros)
  BPS     (Velocidad de conexion en baudios)
  
  Tienda donde se compro el modulo: http://dinastiatecnologica.com/producto/modulo-bluetooth-hc-05/
  By: http://elprofegarcia.com
  
  CONEXIONES:
  ARDUINO   BLUETOOTH
  5V        VCC
  GND       GND
  PIN 2     TX
  PIN 3     RX
  
 */

SoftwareSerial blue(2, 3);   //Crea conexion al bluetooth - PIN 2 a TX y PIN 3 a RX

int pinLOMasAD8232 = 10;
int pinLOMenosAD8232 = 11;
int pinOutputAD8232 = A0;
int unsigned long lastMillis = 0;
 
PulseOximeter pox;
uint32_t tsLastReport = 0;
void setup()
{
    blue.begin(9600); // inicialmente la comunicacion serial a 9600 Baudios (velocidad de fabrica)
    Serial.begin(9600);
    pinMode(pinLOMasAD8232, INPUT);
    pinMode(pinLOMenosAD8232, INPUT);
    pox.begin();
    pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);
    /*pinMode(13,OUTPUT);
    digitalWrite(13,HIGH); // Enciende el LED 13 durante 4s antes de configurar el Bluetooth
    delay(4000);
    
    digitalWrite(13,LOW); // Apaga el LED 13 para iniciar la programacion
    
    blue.print("AT");  // Inicializa comando AT
    delay(1000);
 
    blue.print("AT+NAME"); // Configura el nuevo nombre 
    blue.print(NOMBRE);
    delay(1000);                  // espera 1 segundo
 
    blue.print("AT+BAUD");  // Configura la nueva velocidad 
    blue.print(BPS); 
    delay(1000);
 
    blue.print("AT+PIN");   // Configura el nuevo PIN
    blue.print(PASS); 
    delay(1000);    */
}
 
void loop()
{
  char mensaje[10] = "+00000000";
  //digitalWrite(13, !digitalRead(13)); // cuando termina de configurar el Bluetooth queda el LED 13 parpadeando
  //delay(300);
  //blue.write(72);
  //El indice 0 indica si se presionó el botón de envío manual.
  //TODO: En caso de ser mensaje de alerta manual se debe de escribir un ! en el primer indice. 
  if(digitalRead(pinLOMasAD8232) == 1 || digitalRead(pinLOMenosAD8232) == 1) { //En caso de que no se detecte señal por parte del sensor ECG, escribir un - en el indice 0.
   mensaje[0] = '-';
   //blue.write(-1);
   //Serial.println(-1);
  }
  else { //En caso contrario debemos de meter el número arrojado por el ECG en el mensaje, en los indices 1 al 4 (El entero más grande que puede arrojar analogRead es 1024).
   //blue.write(analogRead(pinOutputAD8232));
   int valorECG = analogRead(pinOutputAD8232);
   int indice = 4;
   while(valorECG > 0) {
    mensaje[indice] = (valorECG%10) + '0';
    valorECG = valorECG/10;
    indice--;
   }
   //Serial.println(analogRead(pinOutputAD8232));
  }
  // Se mete el número arrojado por el ECG en el mensaje, en los indices 6 al 9 (El entero más grande que puede arrojar analogRead es 1024).
  int valorOximetro = pox.getSpO2();
  int indice = 8;
  while(valorOximetro > 0) {
    mensaje[indice] = valorOximetro%10 + '0';
    valorOximetro = valorOximetro/10;
    indice--;
   }
  blue.write(mensaje);
}
