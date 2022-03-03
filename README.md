# Hörmann Promatic 3 gate control and web ui

This project contains a python rest api to control the Hörmann Promatic 3 actuator.
Also the application contains a Web Ui to trigger events and read sensor data.

The main task of the application is to open the gate for ventilation, if the humidity is too high.

### Requirements

- Raspbian OS
- Python3 ```sudo apt install python3 python3-setuptools```
- pigpio ```sudo apt install pigpio python3-pigpio```

Activate remote interface binding in service by removing the "-l" flag from daemon configuration:
See: [pigpiod config](https://abyz.me.uk/rpi/pigpio/pigpiod.html)

```bash
sudo vim sudo vim /lib/systemd/system/pigpiod.service
sudo systemctl enable pigpiod
sudo systemctl start pigpiod
```

pigpiod should be now available under "host:8888"

### Configuration
GPIO Configuration

See: [GPIO Pins](https://projects.raspberrypi.org/en/projects/physical-computing/1)
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