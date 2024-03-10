package com.gerefi.can;

import java.io.IOException;

public class RallySandbox {
    public static void main(String[] args) throws IOException {
        Launcher.main(new String[]{
                "C:\\stuff\\gerefi_documentation\\OEM-Docs\\Mitsubishi\\2009-rallyart",
                Launcher.FILENAME_SUFFIX_PROPERTY,
                ".log",
//                Launcher.FILENAME_FILTER_PROPERTY,
//                "auto",
        });
    }
}
