import pigpio

from app import config
from services import gateStatusService
from services.gateStatusService import GateStatus
from app import pi
import time

switch_pin_config = config.get('gpio').get('switch')
press_timeout = switch_pin_config.get('press_timeout_in_ms')
switch_pin_open_close = switch_pin_config.get('close_open')
switch_pin_ventilation = switch_pin_config.get('ventilation')


def changeGateToVentilation():
    if gateStatusService.interaction_in_progress:
        return gateStatusService.get_actual_gate_status()
    gateStatusService.interaction_in_progress = True
    if GateStatus.VENTILATION != gateStatusService.get_actual_gate_status():
        _trigger_ventilation_switch()
    gateStatusService.interaction_in_progress = False
    return gateStatusService.get_actual_gate_status()


def closeGate():
    if gateStatusService.interaction_in_progress:
        return gateStatusService.get_actual_gate_status()
    if GateStatus.CLOSED == gateStatusService.get_actual_gate_status():
        return gateStatusService.get_actual_gate_status()

    changeGateToVentilation()
    gateStatusService.interaction_in_progress = True
    _trigger_ventilation_switch()
    gateStatusService.interaction_in_progress = False

    return gateStatusService.get_actual_gate_status()


def openGate():
    if gateStatusService.interaction_in_progress:
        return gateStatusService.get_actual_gate_status()
    if GateStatus.OPEN == gateStatusService.get_actual_gate_status():
        return gateStatusService.get_actual_gate_status()

    changeGateToVentilation()
    gateStatusService.interaction_in_progress = True
    _trigger_open_close_switch()
    gateStatusService.interaction_in_progress = False

    return gateStatusService.get_actual_gate_status()


def _trigger_ventilation_switch():
    print('Trigger ventilation')
    pi.set_mode(switch_pin_ventilation, pigpio.OUTPUT)
    pi.write(switch_pin_ventilation, 0)
    time.sleep(0.5)
    pi.write(switch_pin_ventilation, 1)


def _trigger_open_close_switch():
    print('Trigger open_close')
    pi.set_mode(switch_pin_open_close, pigpio.OUTPUT)
    pi.write(switch_pin_open_close, 0)
    time.sleep(0.5)
    pi.write(switch_pin_open_close, 1)
