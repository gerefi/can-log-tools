package com.gerefi.can.writer;

import com.gerefi.can.CANPacket;

import java.io.IOException;
import java.util.List;

public interface CANTextWriter {
    void write(List<CANPacket> packetList) throws IOException;
}
