package com.gerefi.can.deprecated.decoders;

import com.gerefi.can.CANPacket;
import com.gerefi.can.Utils;

public abstract class AbstractPacketDecoder implements PacketDecoder {
    private final int id;

    public AbstractPacketDecoder(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    protected void throwUnexpected(String reason, CANPacket packet) {
        throw unexpected(reason, packet);
    }

    protected IllegalStateException unexpected(String reason, CANPacket packet) {
        return new IllegalStateException(reason + ": " + Utils.bytesToHexWithSpaces(packet.getData()));
    }
}
