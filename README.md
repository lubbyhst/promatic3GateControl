# Hörmann Promatic 3 gate control and web ui

This project contains a spring boot application including pi4j to control the Hörmann Promatic 3 actuator.
Also the application contains a Web Ui to trigger events an read sensor data.

The main task of the application is to open the gate for ventilation, if the humidity is too high.


### Configuration
The Pin configuration could be adjusted via the application.properties under src/main/resouces

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