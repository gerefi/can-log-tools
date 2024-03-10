package com.gerefi.can.reader.impl;

import com.gerefi.can.reader.dbc.DbcFile;

import java.io.IOException;

public class ReadFullVagDbc {
    public static final String VAG_DBC_FILE = "opendbc/vw_golf_mk4.dbc";

    public static void main(String[] args) throws IOException {
        DbcFile.readFromFile(VAG_DBC_FILE);
    }
}
