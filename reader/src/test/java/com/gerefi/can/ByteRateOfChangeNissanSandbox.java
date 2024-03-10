package com.gerefi.can;

import com.gerefi.can.analysis.ByteRateOfChangeReports;
import com.gerefi.can.reader.ReaderType;
import com.gerefi.can.reader.ReaderTypeHolder;

import java.io.IOException;

public class ByteRateOfChangeNissanSandbox {
    public static void main(String[] args) throws IOException {
        ReaderTypeHolder.INSTANCE.type = ReaderType.PCAN;

        String inputFolderName = "C:\\stuff\\gerefi_documentation\\OEM-Docs\\Nissan\\2011_Xterra\\CAN-Nov-2022";

        ByteRateOfChangeReports.scanInputFolder(inputFolderName, "pcan.trc");
    }
}
