from flask import Flask
import pigpio
import yaml

config = yaml.safe_load(open("config.yml"))
pi = pigpio.pi(config.get('pigpio').get('hostname'), config.get('pigpio').get('port'))
server = Flask(__name__)

from api import endpoints


if __name__ == '__main__':
    server.run()