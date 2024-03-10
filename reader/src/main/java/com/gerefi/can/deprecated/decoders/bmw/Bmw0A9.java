package com.gerefi.can.deprecated.decoders.bmw;

import com.gerefi.can.CANPacket;
import com.gerefi.can.SensorValue;
import com.gerefi.can.deprecated.PacketPayload;
import com.gerefi.can.deprecated.SensorType;
import com.gerefi.can.deprecated.decoders.AbstractPacketDecoder;

public class Bmw0A9 extends AbstractPacketDecoder {
    public static final AbstractPacketDecoder INSTANCE = new Bmw0A9();

    public static final int ID = 0xA9;

    public Bmw0A9() {
        super(ID);
    }

    @Override
    public PacketPayload decode(CANPacket packet) {
        int TORQ_AVL_MAX = (int) (packet.getByBitIndex(28, 12) * 0.5);
        return new PacketPayload(packet.getTimeStamp(),
                new SensorValue(SensorType.TORQ_AVL_MAX, TORQ_AVL_MAX)
        );
    }
}
