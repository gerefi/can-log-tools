package com.gerefi.can.reader.impl;

import com.gerefi.can.CANPacket;
import com.gerefi.can.reader.CANLineReader;

import java.io.IOException;
import java.util.List;

public enum CANoeReader implements CANLineReader {
    INSTANCE;

    @Override
    public CANPacket readLine(String line, String fileName, int lineIndex) {
        if (line.contains("ErrorFrame"))
            return null;
        String[] tokens = line.trim().split("\\s+");

        double timeStamp = Double.parseDouble(tokens[0]);
        int sid = Integer.parseInt(tokens[2], 16);
        int size = Integer.parseInt(tokens[5]);

        byte[] data = CANLineReader.readHexArray(tokens, 6, size);
        return new CANPacket(timeStamp, sid, data);
    }

    @Override
    public List<CANPacket> readFile(String fileName) throws IOException {
        return skipHeaderAndRead(fileName, 5);
    }
}
