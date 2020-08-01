# Hörmann Promatic 3 gate control and web ui

This project contains a spring boot application including pi4j to control the Hörmann Promatic 3 actuator.
Also the application contains a Web Ui to trigger events an read sensor data.

The main task of the application is to open the gate for ventilation, if the humidity is too high.

### Requirements

- Raspbian OS
- Open JDK 8 ```sudo apt install openjdk-8-jdk```
- wiringPi ```sudo apt install wiringpi```
- enabled i2c

### How to enable i2c

1. ```sudo echo "i2c-bcm2708" >> /etc/modules```
2. ```sudo echo "i2c-dev" >> /etc/modules```
3. ```sudo echo "dtparam=i2c1=on" >> /boot/config.txt```
4. ```sudo echo "dtparam=i2c_arm=on" >> /boot/config.txt```

### Configuration
The Pin configuration could be adjusted via the application.properties under src/main/resouces
Be aware that the pin addresses are not the regular Raspbery Pi pins.
Please use the wiringPi Pin addresses for configuration. 

See: [WiringPi Pins](https://hackage.haskell.org/package/wiringPi)
### How to compile?

```shell script
mvn clean install
```

### How to run?
Copy to raspberry pi with scp or other tooling and execute the following cmd on the pi.
````shell script
java -jar raspberry-promatic3-web-ui-1.0-SNAPSHOT.jar
````

### Wiring scheme

TBD