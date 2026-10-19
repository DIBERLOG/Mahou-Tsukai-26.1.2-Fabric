"""Send console commands only to this project's isolated loopback test server."""
from pathlib import Path
import socket
import struct
import sys

root = Path(__file__).resolve().parents[1]
import os
properties = {}
for line in (root/os.environ.get('MAHOU_RCON_PROPERTIES','MahouTsukai-Fabric/run/server/server.properties')).read_text().splitlines():
    if '=' in line and not line.startswith('#'):
        key, value = line.split('=', 1)
        properties[key] = value
assert properties['server-ip'] == '127.0.0.1'
assert properties['rcon.port'] == '25586'

def receive(connection, length):
    data = bytearray()
    while len(data) < length:
        part = connection.recv(length-len(data))
        if not part:
            raise ConnectionError('RCON closed the connection')
        data.extend(part)
    return bytes(data)

def exchange(connection, request_id, kind, text):
    payload = struct.pack('<ii', request_id, kind)+text.encode()+b'\0\0'
    connection.sendall(struct.pack('<i', len(payload))+payload)
    length = struct.unpack('<i', receive(connection, 4))[0]
    assert 10 <= length <= 16*1024*1024
    reply = receive(connection, length)
    identifier, _ = struct.unpack('<ii', reply[:8])
    assert identifier == request_id, 'RCON authentication or request failed'
    return reply[8:-2].decode()

with socket.create_connection(('127.0.0.1', 25586), timeout=15) as connection:
    exchange(connection, 1, 3, properties['rcon.password'])
    for index, command in enumerate(sys.argv[1:], 2):
        print(command+': '+exchange(connection, index, 2, command))
