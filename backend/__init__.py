from flask import Flask
import pigpio
import yaml

config = yaml.safe_load(open("config.yml"))
pi = pigpio.pi(config.get('pigpio').get('hostname'), config.get('pigpio').get('port'))
api = Flask(__name__)

from src.http.api import endpoints
