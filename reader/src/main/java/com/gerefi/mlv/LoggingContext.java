package com.gerefi.mlv;

import com.gerefi.can.CANPacket;
import com.gerefi.sensor_logs.BinaryLogEntry;
import com.gerefi.sensor_logs.BinarySensorLog;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoggingContext {
    public Map<String, Double> currentSnapshot = new HashMap<>();
    AtomicReference<Long> currentTime = new AtomicReference<>();

    public BinarySensorLog<BinaryLogEntry> getBinaryLogEntryBinarySensorLog(Collection<BinaryLogEntry> entries, String outputFileName) {
        return new BinarySensorLog<>(o -> {
            Double value = this.currentSnapshot.get(o.getName());
            if (value == null)
                return 0.0;
            return value;
        }, entries, getTimeProvider(), outputFileName);
    }

    public void writeLogContent(List<CANPacket> packets, BinarySensorLog<BinaryLogEntry> log, LoggingStrategy.PacketLogger logger) {
        for (CANPacket packetContent : packets) {
            currentTime.set((long) (packetContent.getTimeStamp() * 1000));
            boolean needLine = logger.takeValues(packetContent);
            if (needLine)
                log.writeSensorLogLine();
        }
        log.close();
    }

    public BinarySensorLog.TimeProvider getTimeProvider() {
        return () -> this.currentTime.get();
    }
}
