package com.gerefi.can.deprecated;

import com.gerefi.can.reader.CANLineReader;
import com.gerefi.can.reader.impl.PcanTrcReader2_0;

import java.io.IOException;

public class BmwPcanCanValidatorSandbox {
    public static void main(String[] args) throws IOException {
        CANLineReader reader = PcanTrcReader2_0.INSTANCE;

//        CANoeCanValidator.validate("C:\\stuff\\gerefi_documentation\\OEM-Docs\\Bmw\\2003_7_Series_e65\\HeinrichG-V12-E65_ReverseEngineering\\E65-760-andrey-2021-feb-21-engine-off-acc-on.trc", reader);
//        CANoeCanValidator.validate("C:\\stuff\\gerefi_documentation\\OEM-Docs\\Bmw\\2003_7_Series_e65\\HeinrichG-V12-E65_ReverseEngineering\\E65-760-andrey-2021-feb-21-engine-off-inpa-tcu-reset-codes.trc", reader);

        CANoeCanValidator.validate("C:\\stuff\\gerefi_documentation\\OEM-Docs\\Bmw\\2003_7_Series_e65\\HeinrichG-V12-E65_ReverseEngineering\\E65-760-andrey-2021-feb-21-engine-off-inpa-reset-codes.trc", reader);

    }


}
