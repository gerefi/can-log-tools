package com.gerefi.can.reader.impl;

import com.gerefi.can.CANPacket;
import com.gerefi.mlv.LoggingStrategy;
import com.gerefi.can.reader.dbc.DbcFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static com.gerefi.can.reader.impl.ParseDBCTest.VAG_MOTOR_1;

public class TrcToMlqSandbox {

    //    private static final String fileName = "C:\\stuff\\gerefi_documentation\\OEM-Docs\\VAG\\2006-Passat-B6\\passat-b6-stock-ecu-ecu-ptcan-not-running-pedal-up-and-down.trc";
    private static final String trcFileName = "C:\\stuff\\gerefi_documentation\\OEM-Docs\\VAG\\2006-Passat-B6\\passat-b6-stock-ecu-ecu-ptcan-parked-revving.trc";

    public static void main(String[] args) throws IOException {
        DbcFile dbc = new DbcFile(LoggingStrategy.LOG_ONLY_TRANSLATED_FIELDS);
        {
            BufferedReader reader = new BufferedReader(new StringReader(VAG_MOTOR_1));
            dbc.read(reader);
        }

        List<CANPacket> packets = new PcanTrcReader1_1().readFile(trcFileName);
        System.out.println(packets.size() + " packets");

        LoggingStrategy.writeLog(dbc, packets, "gauges.mlg");
    }

}
