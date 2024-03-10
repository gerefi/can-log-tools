package com.gerefi.can;

import com.gerefi.can.analysis.ByteRateOfChangeReports;

import java.io.IOException;

public class ByteRateOfChangeVagSandbox {
    public static void main(String[] args) throws IOException {
        String inputFolderName = "C:\\stuff\\gerefi_documentation\\OEM-Docs\\VAG\\2006-Passat-B6";

        ByteRateOfChangeReports.scanInputFolder(inputFolderName, "fast-acceleration3.trc");
    }
}
