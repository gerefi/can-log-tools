package com.gerefi.can.deprecated.decoders;

import com.gerefi.can.CANPacket;
import com.gerefi.can.deprecated.PacketPayload;

public interface PacketDecoder {
    PacketPayload decode(CANPacket packet);

    int getId();
}
