package com.gerefi.can.deprecated.decoders.bmw;

import com.gerefi.can.CANPacket;
import com.gerefi.can.SensorValue;
import com.gerefi.can.deprecated.PacketPayload;
import com.gerefi.can.deprecated.SensorType;
import com.gerefi.can.deprecated.decoders.AbstractPacketDecoder;

public class Bmw0AA extends AbstractPacketDecoder {
    public static final int ID = 0xAA;

    public Bmw0AA() {
        super(ID);
    }

    @Override
    public PacketPayload decode(CANPacket packet) {
        SensorValue pedal = new SensorValue(SensorType.PPS, packet.getUnsigned(3) * 0.39063);
        int rawRpm = packet.getTwoBytesByByteIndex(4);
        if (rawRpm == 0xFFFF)
            return null;
        SensorValue rpm = new SensorValue(SensorType.RPM, rawRpm * 0.25);
        return new PacketPayload(packet.getTimeStamp(), pedal, rpm);
    }

}
