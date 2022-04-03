from backend.src.services import dht22Service, gateStatusService, gateControlService
from backend import api
from flask import jsonify
from dataclasses import dataclass

from backend.src.services.dht22Service import DHT22Result


@api.route('/api/health')
def hello_world():  # put application's code here
    return 'ok'


@api.route('/api/status')
def status():
    gate_state = gateStatusService.get_actual_gate_status()
    garage_status = Status(dht22Service.get_indoor_data(), gate_state.label, gate_state.numeric_status, gate_state.name)
    return jsonify(garage_status)


@api.route('/api/test')
def test():
    return gateControlService.openGate().__getattribute__('name')


@dataclass()
class Status:
    indoor_climate_state: DHT22Result
    gate_state_label: str
    gate_state_numeric: float
    gate_state_name: str
