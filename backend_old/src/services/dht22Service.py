from dataclasses import dataclass
from backend import pi
from pigpio_dht import DHT22

sensor = DHT22(gpio=5, pi=pi)


def get_indoor_data():
    return read_indoor_data()


def read_indoor_data():
    read_error_count = 0
    while read_error_count <= 10:
        try:
            result = sensor.read(retries=5)
            dht22_result = DHT22Result(result.get('temp_c'), result.get('humidity'), 0)
            return dht22_result
        except TimeoutError as e:
            read_error_count += 1
            print(f'Error while reading sensor data. Increasing error counter to: {read_error_count}')



@dataclass
class DHT22Result:
    temperature: float
    humidity_relative: float
    humidity_absolute: float
