package com.gerefi.can.reader.impl;

import com.gerefi.can.CANPacket;
import com.gerefi.can.reader.CANLineReader;

/**
 * @see PcanTrcReader2_0 for version 2.0 format
 * TODO: merge these two?
 */
public class PcanTrcReader1_1 implements CANLineReader {
    public static final CANLineReader INSTANCE = new PcanTrcReader1_1();

    @Override
    public CANPacket readLine(String line, String fileName, int lineIndex) {
        line = line.trim();
        if (line.startsWith(PcanTrcReader2_0.FILEVERSION) && !line.startsWith(PcanTrcReader2_0.FILEVERSION + "=1.1"))
            throw new IllegalStateException("Unexpected fileversion " + line);
        if (line.startsWith(";"))
            return null;
        String[] tokens = line.split("\\s+");
        if (tokens.length < 2)
            throw new IllegalStateException("Unexpected token count in " + fileName + "@" + lineIndex + ": [" + line + "]");
        double timeStamp = Double.parseDouble(tokens[1]);

        int sid = Integer.parseInt(tokens[3], 16);
        int size = Integer.parseInt(tokens[4]);

        byte[] data = CANLineReader.readHexArray(tokens, 5, size);


        return new CANPacket(timeStamp, sid, data);
    }
}
