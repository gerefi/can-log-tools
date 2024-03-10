package com.gerefi.can.reader;

import com.gerefi.can.TrcToMlq;

public enum ReaderTypeHolder {
    INSTANCE;

    public ReaderType type;

    public ReaderType getType() {
        if (type == null)
            type = TrcToMlq.parseCurrentReaderTypeSetting();
        return type;
    }
}
