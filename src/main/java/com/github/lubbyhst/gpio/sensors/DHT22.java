package com.github.lubbyhst.gpio.sensors;

import java.util.Random;
import java.util.logging.Logger;

import com.github.lubbyhst.dto.DHT22Result;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformManager;
import com.pi4j.wiringpi.Gpio;

public class DHT22{
    private static final Logger logger = Logger.getLogger(DHT22.class.getName());
    private static final int maxTimings = 85;
    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private final boolean isSimulated = PlatformManager.getPlatform().equals(Platform.SIMULATED);

    public DHT22() {
        if(isSimulated){
         return;
        }
        // setup wiringPi
        if (Gpio.wiringPiSetup() == -1) {
            logger.warning(" ==>> GPIO SETUP FAILED");
            return;
        }
    }

    private int pollDHT22(final int readingPin) {
        int lastState = Gpio.HIGH;
        int j = 0;
        dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

        Gpio.pinMode(readingPin, Gpio.OUTPUT);
        Gpio.digitalWrite(readingPin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(readingPin, Gpio.HIGH);
        Gpio.pinMode(readingPin, Gpio.INPUT);

        for (int i = 0; i < maxTimings; i++) {
            int counter = 0;
            while (Gpio.digitalRead(readingPin) == lastState) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            lastState = Gpio.digitalRead(readingPin);

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                dht22_dat[j / 8] <<= 1;
                if (counter > 16) {
                    dht22_dat[j / 8] |= 1;
                }
                j++;
            }
        }
        return j;

    }

    public DHT22Result read(final int pinNumber) {
        if(isSimulated){
            logger.fine("Simulated ENV. Returning random values.");
            return new DHT22Result(new Random(System.currentTimeMillis()).nextFloat()*100,new Random(System.currentTimeMillis()+100).nextFloat()*100);
        }
        final int pollDataCheck = pollDHT22(pinNumber);
        if (pollDataCheck >= 40 && checkParity()) {

            float temperature;
            float humidity;

                humidity = (float) ((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
                if (humidity > 100) {
                    humidity = dht22_dat[0]; // for DHT22
                }


                temperature = (float) (((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
                if (temperature > 125) {
                    temperature = dht22_dat[2]; // for DHT22
                }
                if ((dht22_dat[2] & 0x80) != 0) {
                    temperature = -temperature;
                }
                return new DHT22Result(humidity,temperature);
        }
        logger.warning("Data reading failed.");
        return null;
    }



    private boolean checkParity() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }
}