package com.gerefi.can;

import com.gerefi.can.reader.dbc.DbcFile;
import com.gerefi.can.reader.CANLineReader;
import com.gerefi.mlv.LoggingStrategy;
import com.gerefi.util.FolderUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConvertTrcToMegaLogViewerWithDBC {

    public static void doJob(String dbcFileName, String inputFolderName, String outputFolder) throws IOException {
        DbcFile dbc = DbcFile.readFromFile(dbcFileName);

        System.out.println("inputFolderName " + inputFolderName);
        System.out.println("outputFolder " + outputFolder);

        FolderUtil.FileAction fileAction = (simpleFileName, fullFileName) -> {
            List<CANPacket> packets = CANLineReader.getReader().readFile(fullFileName);

            String outputFileName = outputFolder + File.separator + simpleFileName + LoggingStrategy.MLG;

            LoggingStrategy.writeLog(dbc, packets, outputFileName);
        };


        FolderUtil.handleFolder(inputFolderName, fileAction, ".trc1");
    }
}
