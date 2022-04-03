from enum import Enum
from app import config
from app import pi

status_pin_config = config.get('gpio').get('sensor').get('status')
status_pin_open = status_pin_config.get('open')
status_pin_close = status_pin_config.get('close')
status_pin_ventilation = status_pin_config.get('ventilation')
interaction_in_progress = False


def get_actual_gate_status():
    return _read_gate_status()


def _read_gate_status():
    if pi.read(gpio=status_pin_close) == 0:
        return GateStatus.CLOSED
    if pi.read(gpio=status_pin_open) == 0:
        return GateStatus.OPEN
    if pi.read(gpio=status_pin_ventilation) == 0:
        return GateStatus.VENTILATION

    # assume partial open gate
    return GateStatus.OPEN


class GateStatus(Enum):

    def __new__(cls, *args, **kwds):
        value = len(cls.__members__) + 1
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, label, numeric_status):
        self.label = label
        self.numeric_status = numeric_status

    OPEN = 'open', 1
    CLOSED = 'closed', 0
    VENTILATION = 'ventilation', 0.2
