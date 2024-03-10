package com.gerefi.can.analysis;

import com.gerefi.can.CANPacket;
import com.gerefi.can.Launcher;
import com.gerefi.can.reader.CANLineReader;
import com.gerefi.util.FolderUtil;

import java.io.*;
import java.util.*;

public class ByteRateOfChangeReports {
    /**
     * sweet baby O(n^2)
     */
    public static void compareEachReportAgainstAllOthers(String reportDestinationFolder, List<ByteRateOfChange.TraceReport> reports, CanMetaDataContext context) throws FileNotFoundException {
        for (int i = 0; i < reports.size(); i++) {
            for (int j = i + 1; j < reports.size(); j++)
                compareTwoReports(reportDestinationFolder, reports.get(i), reports.get(j), context);
        }
    }

    private static void compareTwoReports(String reportDestinationFolder, ByteRateOfChange.TraceReport traceReport1, ByteRateOfChange.TraceReport traceReport2, CanMetaDataContext context) throws FileNotFoundException {
        Set<ByteRateOfChange.ByteId> allKeys = new TreeSet<>();
        allKeys.addAll(traceReport1.getStatistics().keySet());
        allKeys.addAll(traceReport2.getStatistics().keySet());

        String comparingFolder = reportDestinationFolder + File.separator + "comparison";
        new File(comparingFolder).mkdirs();

        String outputFileName = comparingFolder + File.separator + traceReport1.getSimpleFileName() + "-vs-" + traceReport2.getSimpleFileName() + ".txt";
        PrintWriter report = new PrintWriter(new FileOutputStream(outputFileName));

        report.println("Comparing unique value count per byte " + traceReport1.getSummary() + " and " + traceReport2.getSummary());

        List<ByteVariationDifference> differences = new ArrayList<>();

        report.println("******************** Sorted by key ********************");


        for (ByteRateOfChange.ByteId id : allKeys) {
            if (context.counterBytes.contains(id)) {
                // skipping byte with a known counter
                continue;
            }
            if (id.getByteIndex() == 7 && context.withChecksum.contains(id.sid)) {
                // skipping known checksum byte
                continue;
            }

            ByteRateOfChange.ByteStatistics s1 = traceReport1.getStatistics().computeIfAbsent(id, ByteRateOfChange.ByteStatistics::new);
            ByteRateOfChange.ByteStatistics s2 = traceReport2.getStatistics().computeIfAbsent(id, ByteRateOfChange.ByteStatistics::new);

            if (s1.getUniqueValuesCount() != s2.getUniqueValuesCount()) {
                String msg = id + ": count=" + s1.getUniqueValuesCount() + " vs " + s2.getUniqueValuesCount();
                int deltaCount = Math.abs(s1.getUniqueValuesCount() - s2.getUniqueValuesCount());
                differences.add(new ByteVariationDifference(deltaCount, msg));
                report.println(msg + " delta=" + deltaCount + " / transitions=" + s1.totalTransitions + " vs " + s2.totalTransitions);
            } else {
                HashSet<Integer> diff = s1.getUniqueValues();
                diff.removeAll(s2.getUniqueValues());
                if (!diff.isEmpty()) {

                    report.println(id + " different sets " + s1.getUniqueValues() + " vs " + s2.getUniqueValues());

                } else {
                    // same number of unique values, same set of values
                    if (s1.totalTransitions != s2.totalTransitions) {
                        report.println(id + " total number of transitions " + s1.totalTransitions + "/" + s2.totalTransitions);
                    }
                }
            }
        }

        report.println("******************** Sorted by delta count ********************");
        differences.sort((o1, o2) -> o2.deltaCount - o1.deltaCount);
        for (ByteVariationDifference difference : differences)
            report.println(difference.msg);

        report.println(differences.size() + " total differences");
        report.println();
        report.println();
        report.println();
        report.println();
        report.close();
    }

    public static String createOutputFolder(String inputFolderName) {
        String reportDestinationFolder = inputFolderName + File.separator + "processed";
        new File(reportDestinationFolder).mkdirs();
        return reportDestinationFolder;
    }

    public static void scanInputFolder(String inputFolderName, String fileNameSuffix) throws IOException {
        String reportDestinationFolder = createOutputFolder(inputFolderName);

        CanMetaDataContext context = CanMetaDataContext.read(inputFolderName);

        List<ByteRateOfChange.TraceReport> reports = new ArrayList<>();

        FolderUtil.handleFolder(inputFolderName, (simpleFileName, fullInputFileName) -> {
            if (Launcher.fileNameFilter != null && !simpleFileName.contains(Launcher.fileNameFilter))
                return;

            List<CANPacket> logFileContent = CANLineReader.getReader().readFile(fullInputFileName);

            PerSidDump.handle(reportDestinationFolder, simpleFileName, logFileContent);
            // at the moment we overwrite counter detection report after we process each file
            CounterScanner.scanForCounters(reportDestinationFolder, simpleFileName, logFileContent);
            ChecksumScanner.scanForChecksums(reportDestinationFolder, simpleFileName, logFileContent);

            CanToMegaLogViewer.createMegaLogViewer(reportDestinationFolder, logFileContent, simpleFileName);

            PacketRatio.write(reportDestinationFolder, logFileContent, simpleFileName);

            ByteRateOfChange.TraceReport report = ByteRateOfChange.process(reportDestinationFolder, simpleFileName, logFileContent);
            report.save(simpleFileName + "-ByteRateOfChange.txt");

            reports.add(report);
        }, fileNameSuffix);


        System.out.println("Processing " + reports.size() + " report(s)");
        compareEachReportAgainstAllOthers(reportDestinationFolder, reports, context);
    }

    static class ByteVariationDifference {
        private final int deltaCount;
        private final String msg;

        public ByteVariationDifference(int deltaCount, String msg) {
            this.deltaCount = deltaCount;
            this.msg = msg;
        }
    }
}
