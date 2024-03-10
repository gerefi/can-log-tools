package com.gerefi.can.reader.impl;

import com.gerefi.can.CANPacket;
import com.gerefi.can.reader.CANLineReader;

/**
 * @see PcanTrcReader1_1 for version 1.1 format
 * TODO: merge these two?
 */
public enum PcanTrcReader2_0 implements CANLineReader {
    INSTANCE;

    public static final String FILEVERSION = ";$FILEVERSION";

    @Override
    public CANPacket readLine(String line, String fileName, int lineIndex) {
        line = line.trim();
        if (line.startsWith(FILEVERSION) && !line.startsWith(FILEVERSION + "=2.0"))
            throw new IllegalStateException("Unexpected fileversion " + line + " in " + fileName);
        if (line.startsWith(";"))
            return null;
        String[] tokens = line.split("\\s+");
        double timeStamp = Double.parseDouble(tokens[1]);
        int sid = Integer.parseInt(tokens[3], 16);
        int size = Integer.parseInt(tokens[5]);

        byte[] data = CANLineReader.readHexArray(tokens, 6, size);


        return new CANPacket(timeStamp, sid, data);
    }
}
