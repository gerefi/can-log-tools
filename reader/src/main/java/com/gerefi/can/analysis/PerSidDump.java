package com.gerefi.can.analysis;

import com.gerefi.can.CANPacket;
import com.gerefi.can.DualSid;
import com.gerefi.can.writer.SteveWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Write a separate file for each unique packet ID
 */
public class PerSidDump {
    public static void handle(String reportDestinationFolder, String simpleFileName, List<CANPacket> packets) throws IOException {
        String filteredDestinationFolder = reportDestinationFolder + File.separator + "filtered";
        new File(filteredDestinationFolder).mkdirs();

        TreeSet<Integer> sids = new TreeSet<>();
        // todo: one day I will let streams into my heart
        for (CANPacket packet : packets)
            sids.add(packet.getId());

        // O(n*M) is not so bad
        for (int sid : sids) {

            String outputFileName = filteredDestinationFolder + File.separator + simpleFileName + "_filtered_" + DualSid.dualSid(sid, "_") + ".txt";
            PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));

            List<CANPacket> filteredPackets = new ArrayList<>();

            for (CANPacket packet : packets) {
                if (packet.getId() != sid)
                    continue;

                // no specific reason to use SteveWriter just need any human-readable writer here
                SteveWriter.append(pw, packet);
                filteredPackets.add(packet);
            }
            pw.close();

            int middleIndex = filteredPackets.size() / 2;
            CANPacket middlePacket = filteredPackets.get(middleIndex);

            String middleOutputFileName = filteredDestinationFolder + File.separator + simpleFileName + "_filtered_" + DualSid.dualSid(sid, "_") + "_middle.txt";
            PrintWriter middle = new PrintWriter(new FileOutputStream(middleOutputFileName));

            String decAndHex = middlePacket.getId() + "_" + Integer.toHexString(middlePacket.getId());
            String payloadVariableName = "payload" + decAndHex;
            String variableName = "CAN_" + decAndHex;

            StringBuilder payloadLine = middlePacket.asLua(payloadVariableName);

            middle.println(variableName + " = " + middlePacket.getId());
            middle.println(payloadLine);

            String counterVariable = "counter" + decAndHex;

            middle.println();
            middle.println(counterVariable + " = 0");
            String methodName = "onMotor" + decAndHex;
            middle.println("function " + methodName + "(bus, id, dlc, data)");
            middle.println("\t" + counterVariable + " = (" + counterVariable + " + 1) % 256");
            middle.println("\t" + payloadVariableName + "[x] = " + counterVariable);
            //middle.println("\tprint ('MOTOR_" + middlePacket.getId() + "' " ..)

            middle.println("\ttxCan(VEHICLE_BUS, " + variableName + ", 0, " + payloadVariableName + ")");

            middle.println("end");
            middle.println();

            middle.println("canRxAdd(ECU_BUS, " + variableName + ", " + methodName + ")");
            middle.println("canRxAdd(ECU_BUS, " + variableName + ", " + "drop" + ")");
            middle.println("canRxAdd(ECU_BUS, " + variableName + ", " + "drop" + ")");

            middle.println();

            middle.println(middlePacket.getBytesAsString());
            middle.close();
        }
    }
}
