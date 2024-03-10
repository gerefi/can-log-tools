package com.gerefi.can.reader.dbc;

import com.gerefi.mlv.LoggingStrategy;
import com.gerefi.sensor_logs.BinaryLogEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DbcFile {
    public final LinkedHashMap<Integer, DbcPacket> packets = new LinkedHashMap<>();

    public static final boolean debugEnabled = false;

    private List<BinaryLogEntry> list;

    boolean logOnlyTranslatedFields;

    public DbcFile(boolean logOnlyTranslatedFields) {
        this.logOnlyTranslatedFields = logOnlyTranslatedFields;
    }

    public static DbcFile readFromFile(String fileName) throws IOException {
        System.out.println(new Date() + " Reading DBC file from " + fileName + "...");
        DbcFile dbc = new DbcFile(LoggingStrategy.LOG_ONLY_TRANSLATED_FIELDS);
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            dbc.read(reader);
        }
        return dbc;
    }

    public void read(BufferedReader reader) throws IOException {
        DbcPacket currentPacket = null;
        String line;
        int lineIndex = -1;
        while ((line = reader.readLine()) != null) {
            lineIndex++;
            line = line.trim();
            if (line.startsWith("BO_")) {
                purgePacket(currentPacket);
                line = line.replaceAll(":", "");
                String[] tokens = line.split(" ");
                if (tokens.length < 3) {
                    // skipping header line
                    continue;
                }
                long decId = Long.parseLong(tokens[1]);
                if (decId > 2_000_000_000) {
                    System.err.println("Huh? Skipping ID=" + decId);
                    continue;
                }
                String packetName = tokens[2];
                currentPacket = new DbcPacket((int) decId, packetName);
            } else if (line.startsWith("CM_")) {
                purgePacket(currentPacket);
                line = replaceSpecialWithSpaces(line);
                String[] tokens = line.split(" ");
                if (tokens.length == 1) {
                    // skipping header line
                    continue;
                }
                if (tokens.length < 4)
                    throw new IllegalStateException("Failing to parse comment: " + line + " at " + lineIndex);
                int id = Integer.parseInt(tokens[2]);
                DbcPacket packet = findPacket(id);
                Objects.requireNonNull(packet, "packet for " + id);
                String originalName = tokens[3];
                String niceName = merge(tokens, 4);
                packet.replaceName(originalName, niceName);


            } else if (line.startsWith("SG_")) {
                DbcField field = DbcField.parseField(currentPacket, line);
                if (debugEnabled)
                    System.out.println("Found " + field);
                if (field != null)
                    currentPacket.add(field);

            } else {
                // skipping useless line
            }
        }
        purgePacket(currentPacket);

        System.out.println(getClass().getSimpleName() + ": Total " + packets.size() + " packets");
    }

    private static String merge(String[] tokens, int position) {
        StringBuilder sb = new StringBuilder();
        for (int i = position; i < tokens.length; i++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    private void purgePacket(DbcPacket currentPacket) {
        if (currentPacket != null)
            packets.put(currentPacket.getId(), currentPacket);
    }

    public static String replaceSpecialWithSpaces(String line) {
        line = line.replaceAll("[|+@(,)\\[\\]]", " ");
        line = line.replaceAll(" +", " ");
        return line;
    }

    public DbcPacket findPacket(int canId) {
        return packets.get(canId);
    }

    public List<BinaryLogEntry> getFieldNameEntries() {
        if (list == null) {
            list = LoggingStrategy.getFieldNameEntries(this, logOnlyTranslatedFields);
        }
        return list;
    }

    public DbcPacket getPacketByIndexSlow(int index) {
        return new ArrayList<>(packets.values()).get(index);
    }
}
